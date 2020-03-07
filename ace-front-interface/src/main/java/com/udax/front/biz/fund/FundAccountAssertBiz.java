package com.udax.front.biz.fund;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.configuration.KeyConfiguration;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.fund.FundAccountAssert;
import com.github.wxiaoqi.security.common.entity.fund.FundAccountAssertLog;
import com.github.wxiaoqi.security.common.enums.AccountLogType;
import com.github.wxiaoqi.security.common.enums.AccountSignType;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.enums.fund.FundChangeType;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.mapper.fund.FundAccountAssertLogMapper;
import com.github.wxiaoqi.security.common.mapper.fund.FundAccountAssertMapper;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.udax.front.biz.DcAssertAccountBiz;
import com.udax.front.service.ServiceUtil;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 *
 * @author zhoucf
 * @date 2019-3-26 19:43:46
 */
@Service
@Slf4j
public class FundAccountAssertBiz extends BaseBiz<FundAccountAssertMapper,FundAccountAssert> {

    @Autowired
    private KeyConfiguration configuration;


    @Autowired
    private DcAssertAccountBiz dcAssertAccountBiz;

    @Autowired
    private FundAccountAssertLogMapper fundAccountAssertLogMapper;



    //锁记录
    @Transactional(rollbackFor = Exception.class)
    public FundAccountAssert lockRecord(String dcCode,Long userId) throws Exception {
        FundAccountAssert param = new FundAccountAssert();
        param.setUserId(userId);
        param.setDcCode(dcCode);
        //锁表
        FundAccountAssert fundAccountAssert = mapper.selectForUpdate(param);
        if(fundAccountAssert == null ) {
            insertNewRecord(userId, dcCode);
            fundAccountAssert = mapper.selectForUpdate(param);
        }
        return fundAccountAssert;
    }


    //插入一条新记录
    private FundAccountAssert insertNewRecord(Long userId,String dcCode) throws Exception {
        FundAccountAssert	fundAccountAssert = new FundAccountAssert();
        fundAccountAssert.setDcCode(dcCode);
        fundAccountAssert.setUserId(userId);
        //fundAccountAssert.setExchangeId(BaseContextHandler.getExId());
        fundAccountAssert.setTotalAmount(BigDecimal.ZERO.setScale(8,BigDecimal.ROUND_HALF_UP));
        fundAccountAssert.setAvailableAmount(BigDecimal.ZERO.setScale(8,BigDecimal.ROUND_HALF_UP));
        fundAccountAssert.setFreezeAmount(BigDecimal.ZERO.setScale(8,BigDecimal.ROUND_HALF_UP));
        fundAccountAssert.setUpdateTime(new Date());
        fundAccountAssert.setUmac(ServiceUtil.macSign(fundAccountAssert,configuration.getSignKey()));
        mapper.insertSelective(fundAccountAssert);
        return fundAccountAssert;
    }




    /**
     * 币币账户转入基金账户申请
     */
    @Transactional(rollbackFor = Exception.class)
    public void transInFundAccount(String symbol, BigDecimal amount) throws  Exception{
        if(StringUtils.isBlank(symbol) || amount == null || amount.compareTo(new BigDecimal(0)) <= 0){
            throw new BusinessException(ResponseCode.PARAM_ERROR.name());
        }
        Long userId = BaseContextHandler.getUserID();
//        //锁表
//        DcAssetAccount tranAccount = dcAssertAccountBiz.lockRecord(dcCode,userId);
//        //判断余额是否足够
//        if( tranAccount.getAvailableAmount().compareTo(amount) < 0){
//            return new ObjectRestResponse().status(ResponseCode.BALANCE_NOT_ENOUGH);
//        }
//
//        try{
//            //校验mac
//            boolean flag =ServiceUtil.verifySign(tranAccount,configuration.getSignKey(),tranAccount.getUmac());
//            if (!flag) {
//                // 返回校验失败
//                return new ObjectRestResponse().status(ResponseCode.ASSERT_30001);
//            }
//        }catch (Exception e){
//            logger.error(e.getMessage(),e);
//            return new ObjectRestResponse().status(ResponseCode.ASSERT_30001);
//        }
        String transNo = String.valueOf(IdGenerator.nextId()) ;
        AccountAssertLogVo vo = new AccountAssertLogVo();
        vo.setUserId(userId);
        vo.setSymbol(symbol);
        vo.setAmount(amount);
        vo.setChargeSymbol(symbol);
        vo.setType(AccountLogType.ACCOUNT_TO_FUND);//币币账户转基金
        vo.setTransNo(transNo);//提现流水号
        vo.setRemark(AccountLogType.ACCOUNT_TO_FUND.name());
        dcAssertAccountBiz.signUpdateAssert(vo,AccountSignType.ACCOUNT_PAY_AVAILABLE);//扣减可用余额

        //锁住基金表
        FundAccountAssert reciveAccount = lockRecord(symbol,userId);
        try{
            //校验mac
            boolean flag =ServiceUtil.verifySign(reciveAccount,configuration.getSignKey(),reciveAccount.getUmac());
            if (!flag) {
                // 返回校验失败
                throw new BusinessException(ResponseCode.ASSERT_30001.name());
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw new BusinessException(ResponseCode.ASSERT_30001.name());
        }


        //基金账户资产增加,并重新生成umac
        reciveAccount.setTotalAmount(reciveAccount.getTotalAmount().add(amount));
        reciveAccount.setAvailableAmount(reciveAccount.getAvailableAmount().add(amount));
        reciveAccount.setUmac(ServiceUtil.macSign(reciveAccount,configuration.getSignKey()));
//        //付款方资产变动,并重新生成umac
//        tranAccount.setTotalAmount(tranAccount.getTotalAmount().subtract(amount));
//        tranAccount.setAvailableAmount(tranAccount.getAvailableAmount().subtract(amount));
//        tranAccount.setUmac(ServiceUtil.macSign(tranAccount,configuration.getSignKey()));
//
//        if(tranAccount.getAvailableAmount().compareTo(BigDecimal.ZERO) < 0 ||
//                tranAccount.getTotalAmount().compareTo(BigDecimal.ZERO) < 0){
//            return new ObjectRestResponse().status(ResponseCode.BALANCE_NOT_ENOUGH);
//        }


        //更新数据
       // dcAssetAccountMapper.updateByPrimaryKeySelective(tranAccount);
        mapper.updateByPrimaryKeySelective(reciveAccount);

        //记录日志
        insertLog(userId,amount,symbol,FundChangeType.DC_IN,transNo);
    }

    /**
     * 基金账户转入币币账户
     */
    @Transactional(rollbackFor = Exception.class)
    public void transToBBAccount(String symbol,BigDecimal amount) throws  Exception{
        if(StringUtils.isBlank(symbol) || amount == null || amount.compareTo(new BigDecimal(0)) <= 0){
            throw new BusinessException(ResponseCode.PARAM_ERROR.name());
        }
        Long userId = BaseContextHandler.getUserID();
//        //锁表
//        DcAssetAccount reciveAccount = dcAssertAccountBiz.lockRecord(dcCode,userId);
//         try{
//            //校验mac
//            boolean flag =ServiceUtil.verifySign(reciveAccount,configuration.getSignKey(),reciveAccount.getUmac());
//            if (!flag) {
//                // 返回校验失败
//                return new ObjectRestResponse().status(ResponseCode.ASSERT_30001);
//            }
//        }catch (Exception e){
//            logger.error(e.getMessage(),e);
//            return new ObjectRestResponse().status(ResponseCode.ASSERT_30001);
//        }
        String transNo = String.valueOf(IdGenerator.nextId()) ;
        AccountAssertLogVo vo = new AccountAssertLogVo();
        vo.setUserId(userId);
        vo.setSymbol(symbol);
        vo.setAmount(amount);
        vo.setChargeSymbol(symbol);
        vo.setType(AccountLogType.FUND_TO_ACCOUNT);//基金转币币
        vo.setTransNo(transNo);//提现流水号
        vo.setRemark(AccountLogType.FUND_TO_ACCOUNT.name());
        dcAssertAccountBiz.signUpdateAssert(vo,AccountSignType.ACCOUNT_RECHARGE);//增加可用余额


        //锁住基金表
        FundAccountAssert tranAccount = lockRecord(symbol,userId);
        //判断余额是否足够
        if( tranAccount.getAvailableAmount().compareTo(amount) < 0){
            throw new BusinessException(ResponseCode.FUND_BALANCE_NOT_ENOUGH.name());
        }

        try{
            //校验mac
            boolean flag =ServiceUtil.verifySign(tranAccount,configuration.getSignKey(),tranAccount.getUmac());
            if (!flag) {
                // 返回校验失败
                throw new BusinessException(ResponseCode.ASSERT_30001.name());
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw new BusinessException(ResponseCode.ASSERT_30001.name());
        }


//        //基金账户资产增加,并重新生成umac
//        reciveAccount.setTotalAmount(reciveAccount.getTotalAmount().add(amount));
//        reciveAccount.setAvailableAmount(reciveAccount.getAvailableAmount().add(amount));
//        reciveAccount.setUmac(ServiceUtil.macSign(reciveAccount,configuration.getSignKey()));
        //付款方资产变动,并重新生成umac
        tranAccount.setTotalAmount(tranAccount.getTotalAmount().subtract(amount));
        tranAccount.setAvailableAmount(tranAccount.getAvailableAmount().subtract(amount));
        tranAccount.setUmac(ServiceUtil.macSign(tranAccount,configuration.getSignKey()));

        if(tranAccount.getAvailableAmount().compareTo(BigDecimal.ZERO) < 0 ||
                tranAccount.getTotalAmount().compareTo(BigDecimal.ZERO) < 0 ||
                tranAccount.getFreezeAmount().compareTo(BigDecimal.ZERO) < 0){
            throw new BusinessException(ResponseCode.FUND_BALANCE_NOT_ENOUGH.name());
        }


        //更新数据
        //dcAssetAccountMapper.updateByPrimaryKeySelective(reciveAccount);
        mapper.updateByPrimaryKeySelective(tranAccount);

        insertLog(userId,amount,symbol,FundChangeType.DC_OUT,transNo);

    }


    private void insertLog(Long userId,BigDecimal amount,String dcCode,FundChangeType changeType,String transNo){
        //记录日志
        FundAccountAssertLog log = new FundAccountAssertLog();
        log.setTransNo(transNo);
        log.setUserId(userId);
        log.setChangeType(changeType.value());//转入
        log.setChangeAmount(amount);//轉入轉出數量
        log.setDcCode(dcCode);//代币
        log.setUpdateTime(new Date());
        fundAccountAssertLogMapper.insert(log);
    }


}