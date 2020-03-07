package com.github.wxiaoqi.security.admin.biz.ud;

import com.github.wxiaoqi.security.admin.biz.front.AssetAccountBiz;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.entity.ud.*;
import com.github.wxiaoqi.security.common.enums.AccountLogType;
import com.github.wxiaoqi.security.common.enums.AccountSignType;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.enums.ud.QueueStatus;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.mapper.ud.HParamMapper;
import com.github.wxiaoqi.security.common.mapper.ud.HQueueMapper;
import com.github.wxiaoqi.security.common.mapper.ud.HUnLockDetailMapper;
import com.github.wxiaoqi.security.common.mapper.ud.HUserInfoMapper;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

/**
 * 申购排单详情表
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HQueueBiz extends BaseBiz<HQueueMapper, HQueue> {

    @Autowired
    private AssetAccountBiz dcAssertAccountBiz;

    @Autowired
    private HParamMapper paramMapper;

    @Autowired
    private HUserInfoMapper userInfoMapper;

    @Autowired
    HUnLockDetailMapper lockDetailMapper;

    //释放用户资产
    @Transactional(rollbackFor = Exception.class)
    public void adminToRelase(String  orderNo) throws Exception{

        logger.error("管理后台手动开始释放锁定资产,订单号:" + orderNo);
        HQueue queue = mapper.selectForUpdate(orderNo);//锁定当前订单

        if(queue == null){
            throw new BusinessException(ResponseCode.ORDER_NOT_EXIST.name());
        }

        if(!QueueStatus.WAIT_MATCH.value().equals(queue.getStatus())){
            throw new BusinessException(ResponseCode.NOT_RELASE_CONDITION.name());
        }

        AccountAssertLogVo payVo = new AccountAssertLogVo();
        payVo.setUserId(queue.getUserId());
        payVo.setSymbol(queue.getSymbol());
        payVo.setAmount(queue.getLockAmount());
        payVo.setChargeSymbol(queue.getSymbol());
        payVo.setType(AccountLogType.UD_UNFREEZE);//释放冻结金额
        payVo.setTransNo(queue.getOrderNo());
        payVo.setRemark(AccountLogType.UD_UNFREEZE.name());
        dcAssertAccountBiz.dcSignUpdateAssert(payVo, AccountSignType.ACCOUNT_WITHDRAW_DEDUTION);//解冻资产

        //更新申购单状态为失效
        Example example = new Example(HQueue.class);
        example.createCriteria().andEqualTo("orderNo",orderNo);
        HQueue updateQueue = new HQueue();
        updateQueue.setStatus(QueueStatus.INVALID.value());
        mapper.updateByExampleSelective(updateQueue,example);

        //锁定用户
        example = new Example(HUserInfo.class);
        example.createCriteria().andEqualTo("userId",queue.getUserId());
        HUserInfo updateUserInfo = new HUserInfo();
        updateUserInfo.setStatus(EnableType.DISABLE.value());
        userInfoMapper.updateByExampleSelective(updateUserInfo,example);
    }
}
