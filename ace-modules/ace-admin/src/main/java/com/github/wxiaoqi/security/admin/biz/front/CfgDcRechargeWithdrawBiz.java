package com.github.wxiaoqi.security.admin.biz.front;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.BasicSymbol;
import com.github.wxiaoqi.security.common.entity.admin.CfgDcRechargeWithdraw;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.admin.CfgDcRechargeWithdrawMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class CfgDcRechargeWithdrawBiz extends BaseBiz<CfgDcRechargeWithdrawMapper, CfgDcRechargeWithdraw> {

    @Autowired
    private CfgSymbolDescriptionBiz cfgSymbolDescriptionBiz;

    @Autowired
    private BasicSymbolBiz basicSymbolBiz;

    @Override
    public void insertSelective(CfgDcRechargeWithdraw entity) {
        setProtocolType(entity);
        if (entity.getSystemConfig().equals(EnableType.ENABLE.value())) {
            if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
                entity.setExchId(BaseContextHandler.getExId());
            }else if (entity.getExchId() == null){
                throw new UserInvalidException(Resources.getMessage("CONFIG_EXCH_ID"));
            }
            //获取币种最小充币数量并设置到新的币种
            CfgDcRechargeWithdraw config = mapper.getConfig(entity.getSymbol(), EnableType.DISABLE.value(),entity.getProtocolType());
            if (config==null){
                throw new UserInvalidException(Resources.getMessage("CONFIG_CFG_DC_RECHARGE_WITHDRAW"));
            }
            entity.setMinRechargeAmount(config.getMinRechargeAmount());
        }
        CfgDcRechargeWithdraw withdraw = new CfgDcRechargeWithdraw();
        withdraw.setExchId(entity.getExchId());
        withdraw.setSymbol(entity.getSymbol());
        withdraw.setProtocolType(entity.getProtocolType());
        withdraw.setSystemConfig(entity.getSystemConfig());
        CfgDcRechargeWithdraw one = mapper.selectOne(withdraw);
        if (one != null) {
            throw new UserInvalidException(Resources.getMessage("CONFIG_CURRENCY_INFO_REPEAT"));
        }
        if (entity.getMaxWithdrawAmount().compareTo(entity.getMinWithdrawAmount())<=0){
            throw new UserInvalidException(Resources.getMessage("CONFIG_WITHDRAW_AMOUNT"));
        }
        updateIsShow(entity);
        mapper.insertSelective(entity);
        cacheReturn();
        cfgSymbolDescriptionBiz.cacheReturn();
    }


    @Override
    public void updateSelectiveById(CfgDcRechargeWithdraw entity) {
        setProtocolType(entity);
        updateIsShow(entity);
        if (entity.getSystemConfig().equals(EnableType.ENABLE.value())) {
            CfgDcRechargeWithdraw config = mapper.getConfig(entity.getSymbol(), EnableType.DISABLE.value(),entity.getProtocolType());
            entity.setMinRechargeAmount(config.getMinRechargeAmount());
        }else {
            CfgDcRechargeWithdraw withdraw = new CfgDcRechargeWithdraw();
            withdraw.setSymbol(entity.getSymbol());
            List<CfgDcRechargeWithdraw> list = mapper.select(withdraw);
            list.forEach(cfgDcRechargeWithdraw -> {
                cfgDcRechargeWithdraw.setMinRechargeAmount(entity.getMinRechargeAmount());
                mapper.updateByPrimaryKeySelective(cfgDcRechargeWithdraw);
            });
        }
        EntityUtils.setUpdatedInfo(entity);
        mapper.updateByPrimaryKeySelective(entity);
        cacheReturn();
        cfgSymbolDescriptionBiz.cacheReturn();
    }

    @Override
    public void deleteById(Object id) {
        CfgDcRechargeWithdraw withdraw = mapper.selectByPrimaryKey(id);
        if (withdraw.getSystemConfig().equals(EnableType.DISABLE.value())) {
            mapper.deleteAll(withdraw.getSymbol(),withdraw.getProtocolType());
        }else {
            mapper.deleteByPrimaryKey(id);
        }
        cacheReturn();
        cfgSymbolDescriptionBiz.cacheReturn();
    }

    private void setProtocolType(CfgDcRechargeWithdraw entity) {
        if (StringUtils.isNotEmpty(entity.getSymbol())&& entity.getSymbol().contains("/")){
            String[] split = entity.getSymbol().split("/");
            entity.setSymbol(split[0]);
            entity.setProtocolType(split[1]);
        }
    }

    public void updateIsShow(CfgDcRechargeWithdraw entity){
        //获取币种isShow
        BasicSymbol symbol = new BasicSymbol();
        symbol.setSymbol(entity.getSymbol().trim());
        symbol.setProtocolType(entity.getProtocolType());
        BasicSymbol basicSymbol = basicSymbolBiz.selectOne(symbol);
        if (basicSymbol==null){
            return;
        }
        if (entity.getIsShow() ==null || !basicSymbol.getIsShow().equals(entity.getIsShow())){
            entity.setIsShow(basicSymbol.getIsShow());
        }
    }
    public List<CfgDcRechargeWithdraw> cacheReturn() {
        List<CfgDcRechargeWithdraw> list = null;
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.CFG_DCRECHARGE_WITHDRAW_EXCH);
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.CFG_DCRECHARGE_WITHDRAW_MAP);
        try {
            CfgDcRechargeWithdraw withdraw = new CfgDcRechargeWithdraw();
            withdraw.setStatus(EnableType.ENABLE.value());
            list = mapper.select(withdraw);
            HashMap<String, CfgDcRechargeWithdraw> map = new HashMap<>();
            list.forEach((w -> {
                String symbolType=w.getSymbol().trim();
                if (!StringUtils.isEmpty(w.getProtocolType())) {
                    symbolType = symbolType +w.getProtocolType();
                }
                CacheUtil.getCache().set(Constants.CacheServiceType.CFG_DCRECHARGE_WITHDRAW_EXCH + symbolType+":"+ w.getExchId(), w);
                if (EnableType.ENABLE.value().equals(w.getIsShow())){
                    CacheUtil.getCache().set(Constants.CacheServiceType.CFG_DCRECHARGE_WITHDRAW + w.getSymbol().trim()+":"+ w.getExchId(), w);
                    map.put(w.getSymbol()+":"+w.getExchId(),w);
                }
            }));

            CacheUtil.getCache().set(Constants.CacheServiceType.CFG_DCRECHARGE_WITHDRAW_MAP, map);
            logger.info(" ==币种充提币配置表:CfgCurrencyTransfer  缓存完成,缓存条数：{}", list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
