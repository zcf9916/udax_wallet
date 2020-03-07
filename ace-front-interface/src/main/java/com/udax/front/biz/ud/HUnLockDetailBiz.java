package com.udax.front.biz.ud;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.WhiteExchInfo;
import com.github.wxiaoqi.security.common.entity.ud.*;
import com.github.wxiaoqi.security.common.enums.AccountLogType;
import com.github.wxiaoqi.security.common.enums.AccountSignType;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.mapper.admin.WhiteExchInfoMapper;
import com.github.wxiaoqi.security.common.mapper.ud.*;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.github.wxiaoqi.security.common.vo.ValuationModeVo;
import com.udax.front.biz.CacheBiz;
import com.udax.front.biz.DcAssertAccountBiz;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import com.udax.front.service.ServiceUtil;
import com.udax.front.util.CacheBizUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 
 *
 * @author Mr.AG
 * @email 463540703@qq.com
 * @date 2017-12-26 19:43:46
 */
@Service
@Slf4j
public class HUnLockDetailBiz extends BaseBiz<HUnLockDetailMapper, HUnLockDetail> {


    @Autowired
    private DcAssertAccountBiz dcAssertAccountBiz;



    @Autowired
    private HUserInfoMapper userInfoMapper;


    @Autowired
    private HParamBiz paramBiz;

    @Autowired
    private CacheBiz cacheBiz;

    //用户解锁
    @Transactional(rollbackFor = Exception.class)
    public void unlock() throws Exception{
        HUserInfo userInfo = new HUserInfo();
        userInfo.setUserId(BaseContextHandler.getUserID());
        userInfo = userInfoMapper.selectOne(userInfo);

        if(userInfo == null){
            throw new BusinessException(ResponseCode.USER_NOT_EXIST.name());
        }
        //用户没有被锁定
        if(userInfo.getStatus().equals(EnableType.ENABLE.value())){
            throw new BusinessException(ResponseCode.DONOT_NEED_UNLOCK.name());
        }

        String defaultLtCode  = CacheBizUtil.getLtCode(BaseContextHandler.getAppExId(),cacheBiz);

//        String defaultLtCode = null;
//        List<ValuationModeVo> list = CacheBizUtil.getValuationManner(cacheBiz,BaseContextHandler.getAppExId(),BaseContextHandler.getLanguage());
//        if(StringUtil.listIsNotBlank(list)){
//            for(ValuationModeVo l : list) {
//                //查找默认的结算币种
//                if (StringUtils.isNotBlank(l.getDefaultSymbol())) {
//                    defaultLtCode = l.getDefaultSymbol();
//                    break;
//                }
//            }
//        }
        if( StringUtils.isBlank(defaultLtCode)){
            throw new BusinessException(ResponseCode.PARAM_ERROR.name());
        }

        HParam hParam = ServiceUtil.getUdParamByKey("UNLOCK_AMOUNT",paramBiz);

        HUnLockDetail unLockDetail = new HUnLockDetail();
        unLockDetail.setUserId(BaseContextHandler.getUserID());
        unLockDetail.setAmount(new BigDecimal(hParam.getUdValue()));
        unLockDetail.setOrderNo(String.valueOf(IdGenerator.nextId()));
        unLockDetail.setType(EnableType.DISABLE.value());
        unLockDetail.setCreateTime(new Date());
        mapper.insertSelective(unLockDetail);

        AccountAssertLogVo payVo = new AccountAssertLogVo();
        payVo.setUserId(BaseContextHandler.getUserID());
        payVo.setSymbol(defaultLtCode);
        payVo.setAmount(unLockDetail.getAmount());
        payVo.setChargeSymbol(defaultLtCode);
        payVo.setType(AccountLogType.UD_UNLOCK);//UD 社区解锁
        payVo.setTransNo(unLockDetail.getOrderNo());
        payVo.setRemark(AccountLogType.UD_UNLOCK.name());
        dcAssertAccountBiz.signUpdateAssert(payVo,AccountSignType.ACCOUNT_PAY_AVAILABLE);//扣除可用资产

        HUserInfo updateParam = new HUserInfo();
        updateParam.setId(userInfo.getId());
        updateParam.setStatus(EnableType.ENABLE.value());
        userInfoMapper.updateByPrimaryKeySelective(updateParam);
    }



}