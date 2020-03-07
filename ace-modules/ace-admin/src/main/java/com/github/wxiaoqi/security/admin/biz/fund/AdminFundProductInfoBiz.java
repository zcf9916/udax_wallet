package com.github.wxiaoqi.security.admin.biz.fund;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.entity.fund.*;
import com.github.wxiaoqi.security.common.entity.merchant.MchTradeDetail;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.enums.fund.FundChangeType;
import com.github.wxiaoqi.security.common.enums.fund.FundPurchaseStatus;
import com.github.wxiaoqi.security.common.enums.fund.FundStatus;
import com.github.wxiaoqi.security.common.mapper.fund.*;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.*;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Tang
 * @date 2019-3-26 19:43:46
 */
@Service
@Slf4j
public class AdminFundProductInfoBiz extends BaseBiz<FundProductInfoMapper,FundProductInfo> {

    @Autowired
    private FundProductProfiltInfoMapper productProfiltInfoMapper;
    @Autowired
    private FundPurchaseInfoMapper purchaseInfoMapper;
    @Autowired
    private FundAccountAssertMapper accountAssertMapper;
    @Autowired
    private FundAccountAssertLogMapper accountAssertLogMapper;

    @Override
    @Transactional
    public void insertSelective(FundProductInfo record) {

        record.setPublishTime(new Date());
        record.setFundId(IdGenerator.nextId());
        record.setStatus(FundStatus.PUBLISH.value());
        record.setCycleStarttime(record.getBuyEndtime());
        record.setSubscripeRate(BigDecimal.valueOf(0));
        record.setProportion(record.getProportion().divide(BigDecimal.valueOf(100),4, RoundingMode.HALF_UP));
        record.setExpectProfit(record.getExpectProfit().divide(BigDecimal.valueOf(100),4, RoundingMode.HALF_UP));
        EntityUtils.setCreatAndUpdatInfo(record);
        mapper.insertSelective(record);

        //新增收益表
        FundProductProfiltInfo productProfilt = new FundProductProfiltInfo();
        productProfilt.setFundId(record.getFundId());
        productProfilt.setExchangeId(record.getExchangeId());
        productProfilt.setUpdateTime(new Date());
        productProfilt.setSymbol(record.getDcCode()+"/USDT");
        EntityUtils.setCreatAndUpdatInfo(productProfilt);
        productProfiltInfoMapper.insertSelective(productProfilt);
    }

    @Override
    @Transactional
    public void deleteById(Object id) {//上架或下架操作
        FundProductInfo fundProductInfo = mapper.selectByPrimaryKey(id);
        if(EnableType.ENABLE.value().equals(fundProductInfo.getEnable())){//产品目前为上架状态
            //则进行下架操作
            fundProductInfo.setEnable(EnableType.DISABLE.value());
            mapper.updateByPrimaryKeySelective(fundProductInfo);
        }else{
            //则进行上架操作
            fundProductInfo.setEnable(EnableType.ENABLE.value());
            mapper.updateByPrimaryKeySelective(fundProductInfo);
        }
    }

    @Override
    public FundProductInfo selectById(Object id) {

        FundProductInfo  fundProductInfo = mapper.selectByPrimaryKey(id);
        //添加总投资人数和基金明细总数
        Map<String,Long> map= (Map<String, Long>) purchaseInfoMapper.getInvestors(fundProductInfo.getFundId());
        fundProductInfo.setTotalInvestors(map.get("totalInvestors"));
        fundProductInfo.setTotalOrder(map.get("totalOrder"));
        if(map.get("currProfilt") !=null){
            fundProductInfo.setCurrProfilt(new BigDecimal(String.valueOf(map.get("currProfilt"))));
        }
        return fundProductInfo;
    }

    @Transactional
    public ObjectRestResponse updateCurrProfilt(Long fundId,BigDecimal currProfilt){

        if (fundId!=null){
            FundProductInfo fundProductInfo = new FundProductInfo();
            fundProductInfo.setFundId(fundId);
            fundProductInfo = mapper.selectOne(fundProductInfo);
            if (fundProductInfo.getStatus().equals(FundStatus.SETTLEING.value())) {//已经结算
                return new ObjectRestResponse().status(ResponseCode.SETTLED_CANNOT_SETRATE);
            }

            FundProductProfiltInfo  profiltInfo = new FundProductProfiltInfo();
            profiltInfo.setFundId(fundId);
            List<FundProductProfiltInfo> profiltInfos =  productProfiltInfoMapper.select(profiltInfo);;
            if (StringUtil.listIsNotBlank(profiltInfos)){
                profiltInfo.setCurrProfilt(currProfilt.divide(BigDecimal.valueOf(100),4, RoundingMode.HALF_UP));
                profiltInfo.setUpdateTime(new Date());

                Example example = new Example(FundProductProfiltInfo.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("fundId", fundId);

                productProfiltInfoMapper.updateByExampleSelective(profiltInfo,example);
            }else {
                return new ObjectRestResponse().status(ResponseCode.FUND_NOT_EXIST);
            }
        }
        return new ObjectRestResponse();
    }

    // 基金结算
    @Transactional(rollbackFor = Exception.class)
    public ObjectRestResponse updateSettleAccount(Long id, Long fundId) {
        try {
            FundProductInfo fundProductInfo = mapper.selectIdForUpdate(id);
            ObjectRestResponse objectRestResponse  = validFundSettleAccount(fundProductInfo, fundId);// 验证是否满足结算条件

            if(!objectRestResponse.isRel()) return  objectRestResponse;

            Map<String, Object> paramMap = InstanceUtil.newHashMap("fund_id", fundId);
            // 获取实际收益和当前净值
            FundProductProfiltInfo profilt = productProfiltInfoMapper.selectForUpdate(paramMap);
            BigDecimal currProfilt = profilt.getCurrProfilt();// 实际收益率
            //BigDecimal currOneWorth = profilt.getCurrOneWorth();// 当前净值
            FundPurchaseInfo paramPurchaseInfo = new FundPurchaseInfo();
            paramPurchaseInfo.setFundId(fundId);
            List<FundPurchaseInfo> purchaseList = purchaseInfoMapper.select(paramPurchaseInfo);
            paramMap.clear();
            FundAccountAssert paramAssert = new FundAccountAssert();
            for (FundPurchaseInfo purchase : purchaseList) {
                // 锁定申购表
                purchaseInfoMapper.selectIdForUpdate(purchase.getId());
                // 锁定资产表
                paramAssert.setUserId(purchase.getUserId());
                paramAssert.setDcCode(purchase.getDcCode());
                FundAccountAssert account = accountAssertMapper.selectForUpdate(paramAssert);

                // 更新申购表
                BigDecimal changeType = BigDecimal.valueOf(0.0);
                if (currProfilt.compareTo(BigDecimal.valueOf(0.0)) > 0) {// 当前净值大于零,既有收益的时候才会扣除手续费
                    BigDecimal totalProfilt = purchase.getOrderVolume().multiply(currProfilt).setScale(8,
                            BigDecimal.ROUND_DOWN);// 总收益=投资额*实际收益率
                    BigDecimal returnProfilt = totalProfilt.multiply(fundProductInfo.getProportion()).setScale(8,
                            BigDecimal.ROUND_DOWN);// 返回收益额=总收益*返回比例proportion
                    changeType = totalProfilt.subtract(returnProfilt);// 手续费 = 总收益 - 返回收益额
                    purchase.setReturnVolume(
                            purchase.getOrderVolume().add(returnProfilt).setScale(8, BigDecimal.ROUND_DOWN));
                    purchase.setOrderChrge(changeType); // 记录手续费
                } else {
                    purchase.setReturnVolume(
                            purchase.getOrderVolume().multiply(currProfilt.add(new BigDecimal(1))).setScale(8, BigDecimal.ROUND_DOWN));
                }

                purchase.setProfiltVolume(purchase.getReturnVolume().subtract(purchase.getOrderVolume()));// 收益数量等于返还数量减去申购数量
                purchase.setYield(currProfilt);
                //purchase.setCurrOneWorh(profilt.getCurrOneWorth());// 当前净值
                purchase.setUpdateTime(new Date());
                purchase.setSettleTime(new Date());// 结算时间
                purchase.setStatus(FundPurchaseStatus.SETTLED.value());
                purchaseInfoMapper.updateByPrimaryKeySelective(purchase);
                // 更新基金资产表
                account.setAvailableAmount(account.getAvailableAmount().add(purchase.getReturnVolume()));
                if (account.getFreezeAmount().subtract(purchase.getOrderVolume())
                        .compareTo(BigDecimal.valueOf(0.0)) < 0) {
                    return new ObjectRestResponse().status(ResponseCode.FUND_GREATER_THAN_FREEZE);
                }
                account.setFreezeAmount(account.getFreezeAmount().subtract(purchase.getOrderVolume()));
                account.setTotalAmount(account.getAvailableAmount().add(account.getFreezeAmount()));
                account.setUpdateTime(new Date());
                accountAssertMapper.updateByPrimaryKeySelective(account);

                // 记录申购解冻记录
                FundAccountAssertLog log = new FundAccountAssertLog();
                log.setUserId(purchase.getUserId());
                log.setFundId(fundId);
                log.setRemark(fundProductInfo.getFundName());// 基金名称
                log.setChangeType(FundChangeType.UNFREEZE.value());// 冻结
                log.setChangeAmount(purchase.getOrderVolume());// 申购数量
                log.setDcCode(purchase.getDcCode());// 代币
                log.setUpdateTime(new Date());
                accountAssertLogMapper.insertSelective(log);

                // 记录清盘收益增减记录
                FundAccountAssertLog chargeLog = new FundAccountAssertLog();
                chargeLog.setUserId(purchase.getUserId());
                chargeLog.setFundId(fundId);
                chargeLog.setRemark(fundProductInfo.getFundName());// 基金名称
                chargeLog.setChangeType(FundChangeType.PROFILT.value());// 清盘收益增减
                chargeLog.setChangeAmount(purchase.getProfiltVolume());// 收益数量=返还数量减去申购数量
                chargeLog.setDcCode(purchase.getDcCode());// 代币
                chargeLog.setUpdateTime(new Date());
                accountAssertLogMapper.insertSelective(chargeLog);

                if (purchase.getProfiltVolume().compareTo(BigDecimal.valueOf(0.0)) > 0) {// 只有收益数量大于零才会计算手续费
                    // 记录申购手续费扣除记录
                    FundAccountAssertLog commissionLog = new FundAccountAssertLog();
                    commissionLog.setUserId(purchase.getUserId());
                    commissionLog.setFundId(fundId);
                    commissionLog.setRemark(fundProductInfo.getFundName());// 基金名称
                    commissionLog.setChangeType(FundChangeType.COMMISSION.value());// 申购手续费
                    commissionLog.setChangeAmount(changeType);// 手续费
                    commissionLog.setDcCode(purchase.getDcCode());// 代币
                    commissionLog.setUpdateTime(new Date());
                    accountAssertLogMapper.insertSelective(commissionLog);
                }
            }
            // 更新基金产品表状态为已清盘
            FundProductInfo fundProduct = new FundProductInfo();
            fundProduct.setId(id);
            fundProduct.setStatus(FundStatus.SETTLED.value());
            fundProduct.setUpdateTime(new Date());
            mapper.updateByPrimaryKeySelective(fundProduct);

        }  catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(ExceptionUtil.getStackTraceAsString(e));
        }
        return new ObjectRestResponse();
    }

    // 校验基金产品结算信息
    private ObjectRestResponse  validFundSettleAccount(FundProductInfo fundProductInfo, Long fundId) {

        if (null == fundProductInfo) {
            // throw new IllegalArgumentException(getMessage("FUND_NOT_EXIST"));
            return new ObjectRestResponse().status(ResponseCode.FUND_NOT_EXIST);
        }

        if (null == fundProductInfo || fundProductInfo.getFundId().longValue() != fundId.longValue()) {
            // throw new IllegalArgumentException(getMessage("FUND_NOT_EXIST"));
            return new ObjectRestResponse().status(ResponseCode.FUND_NOT_EXIST);
        }
        // 验证当前时间和锁定结束时间
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cycleEndtime = LocalDateUtil.date2LocalDateTime(fundProductInfo.getCycleEndtime());// 锁定结束时间
        if (now.isBefore(cycleEndtime) || !fundProductInfo.getStatus().equals(FundStatus.SETTLEING.value())) {
            // 未达到结算条件
            return new ObjectRestResponse().status(ResponseCode.NO_SATISFY_SETTLEING);
        }
        return new ObjectRestResponse();
    }

    public List<FundPurchaseInfo> getPurchaseList(Map<String, Object> param) {
        return purchaseInfoMapper.selectCustomPage(param);
    }
}