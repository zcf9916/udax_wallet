package com.udax.front.biz.fund;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.fund.FundAccountAssert;
import com.github.wxiaoqi.security.common.entity.fund.FundAccountAssertLog;
import com.github.wxiaoqi.security.common.entity.fund.FundProductInfo;
import com.github.wxiaoqi.security.common.entity.fund.FundPurchaseInfo;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.enums.fund.FundChangeType;
import com.github.wxiaoqi.security.common.enums.fund.FundPurchaseStatus;
import com.github.wxiaoqi.security.common.mapper.fund.FundAccountAssertLogMapper;
import com.github.wxiaoqi.security.common.mapper.fund.FundAccountAssertMapper;
import com.github.wxiaoqi.security.common.mapper.fund.FundProductInfoMapper;
import com.github.wxiaoqi.security.common.mapper.fund.FundPurchaseInfoMapper;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.LocalDateUtil;
import com.github.wxiaoqi.security.common.util.OrderUtil;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 *
 * @author zhoucf
 * @date 2019-3-26 19:43:46
 */
@Service
@Slf4j
public class FundPurchaseInfoBiz extends BaseBiz<FundPurchaseInfoMapper,FundPurchaseInfo> {

    @Autowired
    private FundProductInfoMapper productInfoMapper;

    @Autowired
    private FundAccountAssertMapper accountAssertMapper;

    @Autowired
    private FundAccountAssertBiz accountAssertBiz;

    @Autowired
    private FundAccountAssertLogMapper accountAssertLogMapper;
    /**
     * 购买基金
     *
     */
    @Transactional(rollbackFor = Exception.class)
    public ObjectRestResponse generateFund(Long fundId, BigDecimal orderVolume) throws  Exception{
        Long userId = BaseContextHandler.getUserID();
        // 锁定基金信息
        FundProductInfo fund = productInfoMapper.selectFundIdForUpdate(fundId);
        ObjectRestResponse objectRestResponse =  validFundProductInfo(fund);// 验证基金产品数据
        if(!objectRestResponse.isRel()) return  objectRestResponse;
        // 锁表
        FundAccountAssert account = accountAssertBiz.lockRecord(fund.getDcCode(),userId);
        // 验证account信息
        objectRestResponse = validAccountInfo(account, orderVolume, fund);
        if(!objectRestResponse.isRel()) return  objectRestResponse;


        // 写入申购表基金账户
        FundPurchaseInfo info = new FundPurchaseInfo();
        info.setExchangeId(BaseContextHandler.getAppExId());
        info.setFundId(fund.getFundId());
        info.setFundName(fund.getFundName());
        info.setOrderNo(IdGenerator.nextId());
        info.setUserId(userId);
        info.setOrderChrge(fund.getSubscripeRate().multiply(orderVolume).setScale(8, BigDecimal.ROUND_UP));// 手续费
        info.setOrderVolume(orderVolume.subtract(info.getOrderChrge()));// 申购数量 = 购买数量 - 手续费
        //info.setOrderOneWorth(new BigDecimal(1.0));// 申购净值
        info.setStatus(FundPurchaseStatus.SUBSCRIBE.value());// 已认购
        info.setDcCode(fund.getDcCode());
        mapper.insertSelective(info);

        //更新资产
        FundAccountAssert accountAssert = new FundAccountAssert();
        accountAssert.setTotalAmount(account.getTotalAmount().subtract(info.getOrderChrge()).setScale(8));// 总数量需要增减的
        accountAssert.setFreezeAmount( account.getFreezeAmount().add(info.getOrderVolume()).setScale(8));// 冻结数量需要增减的
        accountAssert.setAvailableAmount(account.getAvailableAmount().add(orderVolume));// 可用数量需要增减的
        accountAssert.setId(account.getId());
        accountAssert.setUpdateTime(new Date());
        accountAssertMapper.updateByPrimaryKeySelective(accountAssert);

        // 更新基金的实际规模
        Map<String, Object> updateProductParam = new HashMap<>();
        updateProductParam.put("id", fundId);
        updateProductParam.put("amount", info.getOrderVolume());// 扣除手续费的申购数量
        productInfoMapper.updateActualScale(updateProductParam);

        // 记录申购冻结记录
        FundAccountAssertLog log = new FundAccountAssertLog();
        log.setUserId(userId);
        log.setFundId(fundId);
        log.setRemark(fund.getFundName());// 基金名称
        log.setChangeType(FundChangeType.FREEZE.value());// 冻结
        log.setChangeAmount(info.getOrderVolume());// 申购数量
        log.setDcCode(fund.getDcCode());// 代币
       // log.setFlowPrimary(info.getOrderNo());// 订单号
        accountAssertLogMapper.insertSelective(log);
        return objectRestResponse;
    }




    // 校验账户信息
    private ObjectRestResponse validAccountInfo(FundAccountAssert account, BigDecimal orderVolume, FundProductInfo fund) {
        if (account == null || orderVolume.compareTo(account.getAvailableAmount()) > 0) {
            logger.error("基金申购数量不能大于可用账户余额:" + fund.getFundName());
            return new ObjectRestResponse().status(ResponseCode.FUND_GREATER_THAN_BALANCE);
        }

        if (orderVolume.compareTo(fund.getMinBuyNum()) < 0) {
            logger.error("基金申购数量不能小于最小申购数量:" + fund.getFundName());
            return new ObjectRestResponse().status(ResponseCode.FUND_LESS_THAN_MINNUM);
        }
        // 总量不等于可用加冻结
        // if(account.getTotolAmount().compareTo(account.getAvailableAmount().add(account.getFreezeAmount()))
        // != 0){
        // logger.error("用户账户代币:" + fund.getDcCode()+"的总量不等于可用数量+冻结数量 ");
        // throw new IllegalArgumentException(getMessage("OPERATE_FAILED"));
        // }
        if (fund.getOverRange() != 1) {// 如果不允许超额认购 1 允许 2不允许
            // 实际规模+最小认购数量 > =预计规模 设置比例为100
            if (fund.getActualScale().add(orderVolume).compareTo(fund.getExpectScale()) > 0) {
                return new ObjectRestResponse().status(ResponseCode.FUND_SELLOUT);
            }
        }
        return new ObjectRestResponse();
    }

    // 校验基金产品信息
    private ObjectRestResponse validFundProductInfo(FundProductInfo fund) {
        if (fund == null || StringUtils.isBlank(fund.getDcCode())) {
           return new ObjectRestResponse().status(ResponseCode.FUND_NOT_EXIST);
        }
        // 验证当前时间和申购结束时间
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime buyEndTime = LocalDateUtil.date2LocalDateTime(fund.getBuyEndtime());// 申购结束时间
        LocalDateTime buyBeginTime = LocalDateUtil.date2LocalDateTime(fund.getBuyStarttime());// 申购开始时间
        if (now.isBefore(buyBeginTime)) {
            // 基金申购已经结束
            return new ObjectRestResponse().status(ResponseCode.FUND_NOT_BEGIN);
        }
        if (now.isAfter(buyEndTime)) {
            // 基金申购已经结束
            return new ObjectRestResponse().status(ResponseCode.FUND_END);
        }
        // 申购费率
        if (fund.getSubscripeRate().compareTo(new BigDecimal(1)) > 0) {
            return new ObjectRestResponse().status(ResponseCode.FUND_RATE_ERROR);
        }
        return new ObjectRestResponse();
    }
//
//    // 基金结算
//    @Transactional
//    public String updateSettleAccount(Long id, Long fundId) {
//        try {
//            FundProductInfo fundProductInfo = fundProductInfoMapper.selectIdForUpdate(id);
//            String msg = validFundSettleAccount(fundProductInfo, fundId);// 验证是否满足结算条件
//            if (msg != null) {
//                return msg;
//            }
//            Map<String, Object> paramMap = InstanceUtil.newHashMap("fund_id", fundId);
//            // 获取实际收益和当前净值
//            FundProductProfiltInfo profilt = fundProductProfiltInfoMapper.selectForUpdate(paramMap);
//            BigDecimal currProfilt = profilt.getCurrProfilt();// 实际收益率
//            BigDecimal currOneWorth = profilt.getCurrOneWorth();// 当前净值
//            List<FundPurchaseInfo> purchaseList = fundPurchaseInfoMapper.selectByMap(paramMap);
//            paramMap.clear();
//            for (FundPurchaseInfo purchase : purchaseList) {
//                // 锁定申购表
//                fundPurchaseInfoMapper.selectIdForUpdate(purchase.getId());
//                // 锁定资产表
//                paramMap.put("userId", purchase.getUserId());
//                paramMap.put("dcCode", purchase.getDcCode());
//                FundAccountAssert account = fundAccountAssertMapper.selectForUpdate(paramMap);
//
//                // 更新申购表
//                BigDecimal changeType = BigDecimal.valueOf(0.0);
//                if (currOneWorth.compareTo(BigDecimal.valueOf(1.0)) > 0) {// 当前净值大于1,既有收益的时候才会扣除手续费
//                    BigDecimal totalProfilt = purchase.getOrderVolume().multiply(currProfilt).setScale(8,
//                            BigDecimal.ROUND_DOWN);// 总收益=投资额*实际收益率
//                    BigDecimal returnProfilt = totalProfilt.multiply(fundProductInfo.getProportion()).setScale(8,
//                            BigDecimal.ROUND_DOWN);// 返回收益额=总收益*返回比例proportion
//                    changeType = totalProfilt.subtract(returnProfilt);// 手续费 = 总收益 - 返回收益额
//                    purchase.setReturnVolume(
//                            purchase.getOrderVolume().add(returnProfilt).setScale(8, BigDecimal.ROUND_DOWN));
//                    purchase.setOrderChrge(changeType); // 记录手续费
//                } else {
//                    purchase.setReturnVolume(
//                            purchase.getOrderVolume().multiply(currOneWorth).setScale(8, BigDecimal.ROUND_DOWN));
//                }
//
//                purchase.setProfiltVolume(purchase.getReturnVolume().subtract(purchase.getOrderVolume()));// 收益数量等于返还数量减去申购数量
//                purchase.setYield(currProfilt);
//                purchase.setCurrOneWorh(profilt.getCurrOneWorth());// 当前净值
//                purchase.setUpdateTime(new Date());
//                purchase.setSettleTime(new Date());// 结算时间
//                purchase.setStatus(FundPurchaseStatus.SETTLED.value());
//                fundPurchaseInfoMapper.updateById(purchase);
//                // 更新基金资产表
//                account.setAvailableAmount(account.getAvailableAmount().add(purchase.getReturnVolume()));
//                if (account.getFreezeAmount().subtract(purchase.getOrderVolume())
//                        .compareTo(BigDecimal.valueOf(0.0)) < 0) {
//                    return "FUND_GREATER_THAN_FREEZE";
//                }
//                account.setFreezeAmount(account.getFreezeAmount().subtract(purchase.getOrderVolume()));
//                account.setTotolAmount(account.getAvailableAmount().add(account.getFreezeAmount()));
//                account.setUpdateTime(new Date());
//                fundAccountAssertMapper.updateById(account);
//
//                // 记录申购解冻记录
//                FundAccountAssertLog log = new FundAccountAssertLog();
//                log.setUserId(purchase.getUserId());
//                log.setFundId(fundId);
//                log.setRemark(fundProductInfo.getFundName());// 基金名称
//                log.setChangeType(FundChangeType.UNFREEZE.value());// 冻结
//                log.setChangeAmount(purchase.getOrderVolume());// 申购数量
//                log.setDcCode(purchase.getDcCode());// 代币
//                log.setFlowPrimary(purchase.getOrderId());// 订单号
//                log.setUpdateTime(new Date());
//                fundAccountAssertLogMapper.insert(log);
//
//                // 记录清盘收益增减记录
//                FundAccountAssertLog chargeLog = new FundAccountAssertLog();
//                chargeLog.setUserId(purchase.getUserId());
//                chargeLog.setFundId(fundId);
//                chargeLog.setRemark(fundProductInfo.getFundName());// 基金名称
//                chargeLog.setChangeType(FundChangeType.PROFILT.value());// 清盘收益增减
//                chargeLog.setChangeAmount(purchase.getProfiltVolume());// 收益数量=返还数量减去申购数量
//                chargeLog.setDcCode(purchase.getDcCode());// 代币
//                chargeLog.setFlowPrimary(purchase.getOrderId());// 订单号
//                chargeLog.setUpdateTime(new Date());
//                fundAccountAssertLogMapper.insert(chargeLog);
//
//                if (purchase.getProfiltVolume().compareTo(BigDecimal.valueOf(0.0)) > 0) {// 只有收益数量大于零才会计算手续费
//                    // 记录申购手续费扣除记录
//                    FundAccountAssertLog commissionLog = new FundAccountAssertLog();
//                    commissionLog.setUserId(purchase.getUserId());
//                    commissionLog.setFundId(fundId);
//                    commissionLog.setRemark(fundProductInfo.getFundName());// 基金名称
//                    commissionLog.setChangeType(FundChangeType.COMMISSION.value());// 申购手续费
//                    commissionLog.setChangeAmount(changeType);// 手续费
//                    commissionLog.setDcCode(purchase.getDcCode());// 代币
//                    commissionLog.setFlowPrimary(purchase.getOrderId());// 订单号
//                    commissionLog.setUpdateTime(new Date());
//                    fundAccountAssertLogMapper.insert(commissionLog);
//                }
//            }
//            // 更新基金产品表状态为已清盘
//            FundProductInfo fundProduct = new FundProductInfo();
//            fundProduct.setId(id);
//            fundProduct.setStatus(FundStatus.SETTLED.value());
//            fundProduct.setUpdateTime(new Date());
//            fundProductInfoMapper.updateById(fundProduct);
//
//        } catch (DuplicateKeyException e) {
//            logger.error(Constants.Exception_Head, e);
//            throw new BusinessException("已经存在相同记录.");
//        } catch (Exception e) {
//            logger.error(Constants.Exception_Head, e);
//            throw new RuntimeException(ExceptionUtil.getStackTraceAsString(e));
//        }
//        return null;
//    }
//
//    // 校验基金产品结算信息
//    private String validFundSettleAccount(FundProductInfo fundProductInfo, Long fundId) {
//
//        if (null == fundProductInfo) {
//            // throw new IllegalArgumentException(getMessage("FUND_NOT_EXIST"));
//            return "FUND_NOT_EXIST";
//        }
//
//        if (null == fundProductInfo || fundProductInfo.getFundId().longValue() != fundId.longValue()) {
//            // throw new IllegalArgumentException(getMessage("FUND_NOT_EXIST"));
//            return "FUND_NOT_EXIST";
//        }
//        // 验证当前时间和锁定结束时间
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime cycleEndtime = LocalDateUtil.date2LocalDateTime(fundProductInfo.getCycleEndtime());// 锁定结束时间
//        if (now.isBefore(cycleEndtime) || !fundProductInfo.getStatus().equals(FundStatus.SETTLEING.value())) {
//            // 未达到清盘条件
//            // throw new IllegalArgumentException(getMessage("NO_SATISFY_SETTLEING"));
//            return "NO_SATISFY_SETTLEING";
//        }
//        return null;
//    }


}