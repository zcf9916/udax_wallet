package com.udax.front.biz;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.lock.UserSymbolLock;
import com.github.wxiaoqi.security.common.entity.lock.UserSymbolLockDetail;
import com.github.wxiaoqi.security.common.enums.AccountLogType;
import com.github.wxiaoqi.security.common.enums.AccountSignType;
import com.github.wxiaoqi.security.common.enums.EmailTemplateType;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.mapper.lock.UserSymbolLockDetailMapper;
import com.github.wxiaoqi.security.common.mapper.lock.UserSymbolLockMapper;
import com.github.wxiaoqi.security.common.util.SendUtil;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import com.udax.front.util.CacheBizUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 *
 * @author Mr.AG
 * @email 463540703@qq.com
 * @date 2017-12-26 19:43:46
 */
@Service
@Slf4j
public class UserSymbolLockDetailBiz extends BaseBiz<UserSymbolLockDetailMapper,UserSymbolLockDetail> {

    @Autowired
    private DcAssertAccountBiz assertAccountBiz;

    @Autowired
    private UserSymbolLockMapper lockMapper;

    @Autowired
    private FrontUserBiz frontUserBiz;

    /**
     * 释放代币
     */
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void releaseSymbol(UserSymbolLockDetail detail) {
        try{
            if(detail.getIsFree().intValue() == EnableType.ENABLE.value()){
                return;
            }
            AccountAssertLogVo payVo = new AccountAssertLogVo();
            payVo.setTransNo(detail.getId().toString());
            payVo.setUserId(detail.getUserId());
            payVo.setSymbol(detail.getSymbol());
            payVo.setAmount(detail.getFreeAmount());
            payVo.setChargeSymbol(detail.getSymbol());
            payVo.setType(AccountLogType.UNLOCK_ASSERT);
            payVo.setRemark(AccountLogType.UNLOCK_ASSERT.name());
            //解凍資產
            assertAccountBiz.signUpdateAssert(payVo,AccountSignType.ACCOUNT_WITHDRAW_DEDUTION);

            while(true) {
                UserSymbolLockDetail param = new UserSymbolLockDetail();
                param.setIsFree(EnableType.ENABLE.value());
                param.setVersion(detail.getVersion() + 1);//乐观锁
                param.setUpdateTime(new Date());
                Example example = new Example(UserSymbolLockDetail.class);
                example.createCriteria().andEqualTo("version", detail.getVersion())
                        .andEqualTo("id", detail.getId());
                int result = mapper.updateByExampleSelective(param, example);
                //乐观锁冲突  重新执行
                if (result < 1) {
                    detail = mapper.selectByPrimaryKey(detail.getId());
                    if(detail.getIsFree().intValue() == EnableType.ENABLE.value()){
                        return;
                    }
                    continue;
                }
                break;
            }
            while(true) {
                UserSymbolLock lock = lockMapper.selectByPrimaryKey(detail.getLockId());
                if (lock == null) {
                    throw new BusinessException("锁仓数据不存在");
                }
                UserSymbolLock lockParam = new UserSymbolLock();
                lockParam.setFreedTime(lock.getFreedTime() + 1);
                lockParam.setVersion(lock.getVersion() + 1);
                lockParam.setFreedAmount(lock.getFreedAmount().add(detail.getFreeAmount()));
                //如果是最后一次释放
                if (lockParam.getFreedTime() == lock.getTotalTime()) {
                    lockParam.setFreedAmount(lock.getTotalAmount());
                    lockParam.setIsFreed(EnableType.ENABLE.value());//释放完毕
                }
                Example example = new Example(UserSymbolLock.class);
                example.createCriteria().andEqualTo("version", lock.getVersion())
                        .andEqualTo("id", lock.getId());
                int result = lockMapper.updateByExampleSelective(lockParam, example);
                //乐观锁冲突  重新执行
                if (result < 1) {
                    continue;
                }
                break;
            }
            FrontUser frontUser = CacheBizUtil.getFrontUserCache(detail.getUserId(),frontUserBiz);
            if(frontUser ==null || frontUser.getUserInfo() == null){
                return;
            }
            SendUtil.sendEmail(EmailTemplateType.UNLOCK_ASSERT_TEMPLATE.value(),frontUser.getEmail(),frontUser.getUserInfo().getExchangeId(),null,frontUser.getUserName(),detail.getFreeAmount(),detail.getSymbol());
        } catch ( Exception e){
            logger.error("释放代币出错,用戶Id:" +  detail.getUserId() + ";代币:" + detail.getSymbol(),e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    /**
     * 入金之后重新计算释放数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDetail(String symbol,Long userId,BigDecimal totalAmount) {
        Example example = new Example(UserSymbolLockDetail.class);
        example.setOrderByClause("id");
        example.createCriteria().andEqualTo("userId",userId).andEqualTo("symbol",symbol);
        List<UserSymbolLockDetail> lockDetailList = mapper.selectByExample(example);
        int totalTime = lockDetailList.size();//总释放次数
        //重新分配的释放数量
        BigDecimal newFreeAmount = totalAmount.divide(new BigDecimal(totalTime)).setScale(8,BigDecimal.ROUND_UP);
        //已释放的数量
        BigDecimal freedAmount = lockDetailList.stream().filter((l) -> l.getIsFree().intValue() == EnableType.ENABLE.value()).map(UserSymbolLockDetail::getFreeAmount).reduce(BigDecimal.ZERO,(a, b) -> a.add(b));
        List<UserSymbolLockDetail> unfreeList = lockDetailList.stream().filter((l) -> l.getIsFree().intValue() == EnableType.DISABLE.value()).collect(Collectors.toList());
        unfreeList.stream().forEach((l) -> l.setFreeAmount(newFreeAmount));
        //除了最后一次锁仓的总预计释放量
        BigDecimal newTotalAmount = unfreeList.stream().limit(unfreeList.size() - 1).map(UserSymbolLockDetail::getFreeAmount).reduce(BigDecimal.ZERO,(a, b) -> a.add(b));
        unfreeList.get(unfreeList.size() -1).setFreeAmount(totalAmount.subtract(newTotalAmount.add(freedAmount)));
        for(UserSymbolLockDetail detail : unfreeList){
            UserSymbolLockDetail param = new UserSymbolLockDetail();
            param.setFreeAmount(detail.getFreeAmount());
            param.setVersion(detail.getVersion() + 1);//乐观锁
            param.setUpdateTime(new Date());
            example  = new Example(UserSymbolLockDetail.class);
            example.createCriteria().andEqualTo("version",detail.getVersion())
                    .andEqualTo("id",detail.getId());
            int result = mapper.updateByExampleSelective(param,example);
            //乐观锁冲突  重新执行
            if(result < 1){
                throw new BusinessException("锁仓时,乐观锁冲突");
            }
        }


    }


}