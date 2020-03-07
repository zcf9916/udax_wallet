package com.github.wxiaoqi.security.admin.biz.front;


import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.*;
import com.github.wxiaoqi.security.common.enums.CurrencyInfoType;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.admin.BasicSymbolMapper;
import com.github.wxiaoqi.security.common.mapper.admin.CfgCurrencyChargeMapper;
import com.github.wxiaoqi.security.common.mapper.admin.CfgSymbolDescriptionMapper;
import com.github.wxiaoqi.security.common.mapper.admin.WhiteExchInfoMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import com.github.wxiaoqi.security.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class BasicSymbolBiz extends BaseBiz<BasicSymbolMapper, BasicSymbol> {

    @Autowired
    private CfgCurrencyChargeMapper cfgCurrencyChargeMapper;

    @Autowired
    private CfgCurrencyChargeBiz cfgCurrencyChargeBiz;


    @Autowired
    private WhiteExchInfoMapper whiteExchInfoMapper;

    @Autowired
    private BasicSymbolMapper basicSymbolMapper;

    @Autowired
    private BasicSymbolImageBiz basicSymbolImageBiz;

    @Autowired
    private CfgSymbolDescriptionBiz cfgSymbolDescriptionBiz;


    @Autowired
    private CfgDcRechargeWithdrawBiz cfgDcRechargeWithdrawBiz;

    @Override
    public void insertSelective(BasicSymbol entity) {

        BasicSymbol symbol = new BasicSymbol();
        symbol.setSymbol(entity.getSymbol());
        //页面开启展示
        if (EnableType.ENABLE.value().equals(entity.getIsShow())){
            symbol.setIsShow(entity.getIsShow());
            BasicSymbol one = mapper.selectOne(symbol);
            if (one !=null) {
                throw new UserInvalidException(Resources.getMessage("CONFIG_IS_SHOW"));
            }
        }
        if (!StringUtils.isEmpty(entity.getProtocolType())){
            symbol.setProtocolType(entity.getProtocolType());
            List<BasicSymbol> list = mapper.select(symbol);
            if (list.size() > 0) {
                throw new UserInvalidException(Resources.getMessage("CONFIG_CURRENCY_INFO_REPEAT"));
            }
        }

        BasicSymbol symbolIsShow = new BasicSymbol();
        symbolIsShow.setSymbol(entity.getSymbol());
        entity.setSymbol(entity.getSymbol().toUpperCase().trim());//转大写
        entity.setCurrencyType(CurrencyInfoType.COIN_TRANSFER.value());
        EntityUtils.setCreatAndUpdatInfo(entity);
        mapper.insertSelective(entity);
        cacheReturn();
        basicSymbolImageBiz.cacheReturn();
    }

    public void updateSelectiveById(BasicSymbol entity) {
        BasicSymbol symbol = new BasicSymbol();
        symbol.setSymbol(entity.getSymbol().trim());
        if (EnableType.ENABLE.value().equals(entity.getIsShow()) && !StringUtils.isEmpty(entity.getProtocolType())){
            symbol.setIsShow(entity.getIsShow());
            List<BasicSymbol> list = mapper.select(symbol);
            if (list.size()==1){
                if (!list.get(0).getProtocolType().equals(entity.getProtocolType())){
                    throw new UserInvalidException(Resources.getMessage("CONFIG_IS_SHOW"));
                }
            }
        }
        EntityUtils.setUpdatedInfo(entity);
        entity.setSymbol(entity.getSymbol().trim());
        mapper.updateByPrimaryKeySelective(entity);
        updateIsShow(entity);
        cacheReturn();
        basicSymbolImageBiz.cacheReturn();
    }

    @Override
    public List<BasicSymbol> selectListAll() {
        List<BasicSymbol> list = (List<BasicSymbol>) CacheUtil.getCache().get(Constants.CacheServiceType.BASIC_SYMBOL);
        if (StringUtil.listIsBlank(list)) {
            Example example = new Example(BasicSymbol.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("status", EnableType.ENABLE.value())
            .andEqualTo("isShow",EnableType.ENABLE.value());
            list = this.selectByExample(example);
        }
        return list;
    }



    private void updateIsShow(BasicSymbol entity) {
        CfgCurrencyCharge charge = new CfgCurrencyCharge();
        charge.setSymbol(entity.getSymbol());
        if (!StringUtils.isEmpty(entity.getProtocolType())){
            charge.setProtocolType(entity.getProtocolType());
        }
        List<CfgCurrencyCharge> currencyList = cfgCurrencyChargeBiz.selectList(charge);
        if (!StringUtil.listIsBlank(currencyList)){
            currencyList.forEach(charge1 -> {
                if (!entity.getIsShow().equals(charge1.getIsShow())){
                    //不相同时更新
                    charge1.setIsShow(entity.getIsShow());
                    cfgCurrencyChargeBiz.updateById(charge1);
                }
            });
        }
        CfgSymbolDescription description = new CfgSymbolDescription();
        description.setSymbol(entity.getSymbol());
        if (!StringUtils.isEmpty(entity.getProtocolType())){
            description.setProtocolType(entity.getProtocolType());
        }

        List<CfgSymbolDescription> descriptionList = cfgSymbolDescriptionBiz.selectList(description);
        if (!StringUtil.listIsBlank(descriptionList)){
            descriptionList.forEach(cfgSymbolDescription -> {
                if (!entity.getIsShow().equals(cfgSymbolDescription.getIsShow())){
                   cfgSymbolDescription.setIsShow(entity.getIsShow());
                    cfgSymbolDescriptionBiz.updateById(cfgSymbolDescription);
                }
            });
        }
        CfgDcRechargeWithdraw withdraw = new CfgDcRechargeWithdraw();
        withdraw.setSymbol(entity.getSymbol());
        if (!StringUtils.isEmpty(entity.getProtocolType())){
            withdraw.setProtocolType(entity.getProtocolType());
        }
        List<CfgDcRechargeWithdraw> currencyChargeList = cfgDcRechargeWithdrawBiz.selectList(withdraw);
        if (!StringUtil.listIsBlank(currencyChargeList)){
            currencyChargeList.forEach(c ->{
                if (!entity.getIsShow().equals(c.getIsShow())){
                    c.setIsShow(entity.getIsShow());
                    cfgDcRechargeWithdrawBiz.updateById(c);
                }
            } );
        }
    }

    public void deleteById(Object id) {
        //删除基础货币时 删除货币配置信息
        cfgCurrencyChargeMapper.deleteByBasicId(id);
        mapper.deleteByPrimaryKey(id);
        cacheReturn();
        basicSymbolImageBiz.cacheReturn();
        cfgCurrencyChargeBiz.cacheReturn();
    }

    public List<BasicSymbol> cacheReturn() {
        List<BasicSymbol> list = null;
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.BASIC_SYMBOL);
        try {
            list = mapper.cacheReturn();
            Map<Long, List<BasicSymbol>> allMap =null;
            if (!StringUtil.listIsBlank(list)) {
                allMap = list.stream().collect(Collectors.groupingBy(BasicSymbol::getExchId));
                allMap.forEach((k, v) -> {
                    CacheUtil.getCache().set(Constants.CacheServiceType.BASIC_SYMBOL_EXCH + ":" + k, (ArrayList)v);
                });
            }
            Example example = new Example(BasicSymbol.class);
            Example.Criteria criteria = example.createCriteria();
            example.setOrderByClause("sort");
            criteria.andEqualTo("status", EnableType.ENABLE.value())
            .andEqualTo("isShow",EnableType.ENABLE.value());
            List<BasicSymbol> symbols = this.selectByExample(example);
            symbols.forEach(basicSymbol -> {
                CacheUtil.getCache().set(Constants.CacheServiceType.BASIC_SYMBOL+basicSymbol.getSymbol(), basicSymbol);
            });
            CacheUtil.getCache().set(Constants.CacheServiceType.BASIC_SYMBOL, (ArrayList<BasicSymbol>) symbols);

            //带重复的币种信息
            BasicSymbol basicSymbol = new BasicSymbol();
            basicSymbol.setStatus(EnableType.ENABLE.value());
            List<BasicSymbol> repeatSymbolList = mapper.select(basicSymbol);
            CacheUtil.getCache().set(Constants.CacheServiceType.BASIC_SYMBOL_REPEAT,
                    (ArrayList<BasicSymbol>) repeatSymbolList);


            logger.info("==基础货币:BasisSymbol缓存完成,缓存条数：{}", list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Map<String, Object> getUserQuote() {
        HashMap<String, Object> map = new HashMap<>();
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            //获取源币种
            WhiteExchInfo exchInfo = whiteExchInfoMapper.selectByPrimaryKey(BaseContextHandler.getExId());
            map.put("dstSymbol", basicSymbolMapper.selectBasicSymbol(exchInfo.getDstSymbol()));
            List<String> list = new ArrayList<>();
            if (exchInfo != null && !StringUtils.isEmpty(exchInfo.getSrcSymbolId())) {
                for (String s : exchInfo.getSrcSymbolId().split(",")) {
                    list.add(s);
                }
                List<BasicSymbol> dstSymbols = mapper.selectBasicSrcSymbol(list);
                map.put("srcSymbols", dstSymbols);
            }
        }
        return map;
    }

    public List<BasicSymbol> getExchBasicSymbol() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("exchId", BaseContextHandler.getExId());
        return basicSymbolMapper.selectSymbolByExchId(map);
    }

    public List<BasicSymbol> repeatSymbolList() {
        List<BasicSymbol> repeatSymbolList = (List<BasicSymbol>)
                CacheUtil.getCache().get(Constants.CacheServiceType.BASIC_SYMBOL_REPEAT);
        if (StringUtil.listIsBlank(repeatSymbolList)) {
            BasicSymbol basicSymbol = new BasicSymbol();
            basicSymbol.setStatus(EnableType.ENABLE.value());
            //带重复的币种信息
           repeatSymbolList = mapper.select(basicSymbol);
        }
        return repeatSymbolList;
    }
}
