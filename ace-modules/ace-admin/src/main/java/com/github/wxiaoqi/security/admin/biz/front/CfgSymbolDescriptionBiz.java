package com.github.wxiaoqi.security.admin.biz.front;

import com.github.wxiaoqi.security.admin.biz.base.DictDataBiz;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.*;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.mapper.admin.BasicSymbolMapper;
import com.github.wxiaoqi.security.common.mapper.admin.CfgDescriptionTemplateMapper;
import com.github.wxiaoqi.security.common.mapper.admin.CfgSymbolDescriptionMapper;
import com.github.wxiaoqi.security.common.mapper.admin.WhiteExchInfoMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import com.github.wxiaoqi.security.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class CfgSymbolDescriptionBiz extends BaseBiz<CfgSymbolDescriptionMapper, CfgSymbolDescription> {

    @Autowired
    private DictDataBiz dictDataBiz;

    @Autowired
    private BasicSymbolMapper basicSymbolMapper;

    @Autowired
    private WhiteExchInfoMapper whiteExchInfoMapper;

    @Autowired
    private CfgDescriptionTemplateMapper cfgDescriptionTemplateMapper;

    public void insertSelective(CfgSymbolDescription entity) {
        setProtocolType(entity);
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            entity.setExchangeId(BaseContextHandler.getExId());
        }
        CfgSymbolDescription description = setIsShow(entity);
        EntityUtils.setCreatAndUpdatInfo(description);
        mapper.insertSelective(description);
        this.cacheReturn();
    }


    public void updateSelectiveById(CfgSymbolDescription entity) {
        setProtocolType(entity);
        CfgSymbolDescription description = setIsShow(entity);
        EntityUtils.setCreatAndUpdatInfo(description);
        mapper.updateByPrimaryKeySelective(description);
        this.cacheReturn();
    }

    public void deleteById(Object id) {
        mapper.deleteByPrimaryKey(id);
        this.cacheReturn();
    }


    private CfgSymbolDescription setIsShow(CfgSymbolDescription entity) {
        BasicSymbol symbol = new BasicSymbol();
        symbol.setSymbol(entity.getSymbol());
        String proType="空";
        if (!StringUtils.isEmpty(entity.getProtocolType())){
            symbol.setProtocolType(entity.getProtocolType());
            proType=entity.getProtocolType();
        }
        BasicSymbol basicSymbol = basicSymbolMapper.selectOne(symbol);
        if (basicSymbol!=null){
            entity.setIsShow(basicSymbol.getIsShow());
        }else {
            logger.error("根据币种:"+entity.getSymbol()+"+链类型:"+proType+"获取币种基础信息失败!");
        }
        return entity;
    }
    private void setProtocolType(CfgSymbolDescription entity) {
        if (StringUtils.isNotEmpty(entity.getSymbol())&& entity.getSymbol().contains("/")){
            String[] split = entity.getSymbol().split("/");
            entity.setSymbol(split[0].trim());
            entity.setProtocolType(split[1]);
        }
    }
    /**
     * 缓存
     * @return
     */
    public List<CfgSymbolDescription> cacheReturn() {
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.CFG_SYMBOL_DESCRIPTION);
        List<CfgSymbolDescription> descriptions = mapper.selectAll();

        //获取所有币种
        List<BasicSymbol> list = basicSymbolMapper.selectAll();

        List<WhiteExchInfo> exchInfos = whiteExchInfoMapper.selectAll();
        //获取所有语言
        List<DictData>   dictData = dictDataBiz.selectListData(Constants.DirtTypeConstant.LANGUAGE,null);
        if (StringUtil.listIsBlank(list) && StringUtil.listIsBlank(dictData) && StringUtil.listIsBlank(exchInfos)) {
            return new ArrayList<>();
        }

        //没有设置描述信息
        if (StringUtil.listIsBlank(descriptions)) {

            for (WhiteExchInfo info :exchInfos) {

                for (DictData data : dictData) {
                    //遍历币种
                    list.forEach(basicSymbol -> {
                        //获取代币限制配置信息
                        CfgDcRechargeWithdraw config = getCfgDcRechargeWithdraw(info, basicSymbol);
                        if (config != null) {
                            saveCfgSymbolDescriptionRedis(info, basicSymbol, data.getDictValue(), config);
                        }
                    });
                }
            }
        } else {
            //遍历交易所
            for (WhiteExchInfo info :exchInfos) {

                //遍历币种
                for (BasicSymbol basicSymbol : list) {
                    //遍历语言
                    for (DictData data : dictData) {
                        boolean isExists = false;
                        //表示已经配置了内容
                        for (CfgSymbolDescription d : descriptions) {
                            if (d.getSymbol().equals(basicSymbol.getSymbol()) && d.getExchangeId().equals(info.getId()) && d.getLanguageType().equals(data.getDictValue())) {
                                CacheUtil.getCache().set(Constants.CacheServiceType.CFG_SYMBOL_DESCRIPTION + basicSymbol.getSymbol().trim() + ":" + info.getId() + ":" + d.getLanguageType()+":"+d.getProtocolType(), d);
                                if (EnableType.ENABLE.value().equals(d.getIsShow())){
                                    CacheUtil.getCache().set(Constants.CacheServiceType.SYMBOL_DESCRIPTION + basicSymbol.getSymbol().trim() + ":" + info.getId() + ":" + d.getLanguageType(), d);
                                }
                                isExists = true;
                            }
                        }
                        //表示没有配置内容 则读默认模板
                        if (!isExists) {
                            CfgDcRechargeWithdraw config = getCfgDcRechargeWithdraw(info, basicSymbol);
                            if (config != null) {
                                saveCfgSymbolDescriptionRedis(info, basicSymbol, data.getDictValue(), config);
                            }
                            isExists = false;
                        }
                    }
                }
            }
        }
        return descriptions;
    }

    private CfgDcRechargeWithdraw getCfgDcRechargeWithdraw(WhiteExchInfo info, BasicSymbol basicSymbol) {
        CfgDcRechargeWithdraw config = null;
        String symbolType=basicSymbol.getSymbol().trim();
        if (!StringUtils.isEmpty(basicSymbol.getProtocolType())) {
            symbolType = symbolType +basicSymbol.getProtocolType();
        }
        config = (CfgDcRechargeWithdraw) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_DCRECHARGE_WITHDRAW_EXCH + symbolType+":"+ info.getId());
        if (config == null) {
            config = (CfgDcRechargeWithdraw) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_DCRECHARGE_WITHDRAW_EXCH + symbolType + ":" + AdminCommonConstant.ROOT);
            if (config==null){
                config = (CfgDcRechargeWithdraw) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_DCRECHARGE_WITHDRAW + basicSymbol.getSymbol().trim() + ":" + info.getId());
                if (config==null){
                    config = (CfgDcRechargeWithdraw) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_DCRECHARGE_WITHDRAW + basicSymbol.getSymbol().trim() + ":" + AdminCommonConstant.ROOT);
                }
            }
        }
        return config;
    }

    private void saveCfgSymbolDescriptionRedis(WhiteExchInfo info, BasicSymbol basicSymbol, String language, CfgDcRechargeWithdraw config) {
        //根据语言获取模板
        CfgDescriptionTemplate templates = cfgDescriptionTemplateMapper.selectDespByLanguage(language);
        if (templates ==null){
            return;
        }
        CfgSymbolDescription desp = new CfgSymbolDescription();
        desp.setExchangeId(info.getId());
        desp.setSymbol(basicSymbol.getSymbol().trim());
        //设置语言
        desp.setLanguageType(language);
        desp.setIsShow(basicSymbol.getIsShow());
        desp.setProtocolType(basicSymbol.getProtocolType());
        //充币模板参数:1: 币种 2:最小充币 3 币种
        String rechargeMessage = String.format(templates.getRechargeDesp(), basicSymbol.getSymbol().trim()+"-"+basicSymbol.getProtocolType(), config.getMinRechargeAmount().stripTrailingZeros().toPlainString(), basicSymbol.getSymbol().trim());
        desp.setRechargeDesp(rechargeMessage);
        //提币模板参数: 1:最小提币数量 2 币种 3 最大提币数量 4 币种 ,5 当日最大提币数量,6 币种
        String withdrawMessage = String.format(templates.getWithdrawDesp(), config.getMinWithdrawAmount().stripTrailingZeros().toPlainString(), basicSymbol.getSymbol().trim(), config.getMaxWithdrawAmount().stripTrailingZeros().toPlainString(),
                basicSymbol.getSymbol().trim(),config.getMaxWithdrawDay().stripTrailingZeros().toPlainString(), basicSymbol.getSymbol().trim());
        desp.setWithdrawDesp(withdrawMessage);
        //转币描述信息没有参数直接填充
        if (config.getMinTransferAmount().compareTo(BigDecimal.ZERO)== 1){
            String transferDesp = String.format(templates.getTransferDesp(), config.getMinTransferAmount().stripTrailingZeros().toPlainString(), basicSymbol.getSymbol().trim());
            desp.setTransferDesp(transferDesp);
        }
        String symbolType=basicSymbol.getSymbol().trim();
        if (!StringUtils.isEmpty(basicSymbol.getProtocolType())) {
            symbolType = symbolType +basicSymbol.getProtocolType();
        }
        CacheUtil.getCache().set(Constants.CacheServiceType.CFG_SYMBOL_DESCRIPTION + symbolType+ ":" + info.getId() + ":" + language, desp);
        if (EnableType.ENABLE.value().equals(basicSymbol.getIsShow())){
            CacheUtil.getCache().set(Constants.CacheServiceType.SYMBOL_DESCRIPTION + basicSymbol.getSymbol().trim() + ":" + info.getId() + ":" + language, desp);
        }
    }
}
