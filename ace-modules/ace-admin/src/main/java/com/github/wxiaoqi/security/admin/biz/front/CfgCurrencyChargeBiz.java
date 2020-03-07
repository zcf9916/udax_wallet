package com.github.wxiaoqi.security.admin.biz.front;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.CfgCurrencyCharge;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.mapper.admin.CfgCurrencyChargeMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class CfgCurrencyChargeBiz extends BaseBiz<CfgCurrencyChargeMapper, CfgCurrencyCharge> {


    @Autowired
    private BasicSymbolBiz basicSymbolBiz;

    public CfgCurrencyCharge selectChargeByIdOrExchId(Map<String, Object> param) {
        return mapper.selectChargeByBasisId(param);
    }

    public void insert(CfgCurrencyCharge entity) {
        entity.setSymbol(entity.getSymbol().trim());
        EntityUtils.setCreatAndUpdatInfo(entity);
        entity.setCurrencyType(EnableType.ENABLE.value());
        entity.setRealUserLevel(EnableType.ENABLE.value());
        mapper.insertCharge(entity);
        basicSymbolBiz.cacheReturn();
        this.cacheReturn();
    }

    public void updateCharge(CfgCurrencyCharge charge) {
        charge.setSymbol(charge.getSymbol().trim());
        EntityUtils.setUpdatedInfo(charge);
        mapper.updateCharge(charge);
        basicSymbolBiz.cacheReturn();
        this.cacheReturn();
    }

    public void cacheReturn() {
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.CFG_CURRENCY_CHARGE);
        try {
            List<CfgCurrencyCharge> charges = mapper.selectChargeList();

            charges.forEach((charge -> {
                String symbolType=charge.getSymbol().trim();
                if (!StringUtils.isEmpty(charge.getProtocolType())) {
                    symbolType =symbolType +charge.getProtocolType();
                }

                if (charge.getExchId().equals(AdminCommonConstant.ROOT)) {
                    CacheUtil.getCache().set(Constants.CacheServiceType.CFG_CURRENCY_CHARGE +symbolType + charge.getExchId(), charge);
                    if (EnableType.ENABLE.value().equals(charge.getIsShow())){
                        CacheUtil.getCache().set(Constants.CacheServiceType.CURRENCY_CHARGE + charge.getSymbol()+charge.getExchId(), charge);
                    }
                } else {
                    CacheUtil.getCache().set(Constants.CacheServiceType.CFG_CURRENCY_CHARGE + symbolType +charge.getExchId() , charge);
                    if (EnableType.ENABLE.value().equals(charge.getIsShow())){
                        CacheUtil.getCache().set(Constants.CacheServiceType.CURRENCY_CHARGE + charge.getSymbol() + charge.getExchId() , charge);
                    }
                }
            }));


            logger.info("==货币配置表:cfgCurrencyCharge  缓存完成 ,缓存条数：{}", charges.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
