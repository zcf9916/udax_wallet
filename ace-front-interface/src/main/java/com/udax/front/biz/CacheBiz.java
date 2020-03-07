package com.udax.front.biz;

import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.*;
import com.github.wxiaoqi.security.common.entity.casino.CasinoParam;
import com.github.wxiaoqi.security.common.entity.casino.CasinoRebateConfig;
import com.github.wxiaoqi.security.common.entity.front.CmsConfig;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.mapper.admin.*;
import com.github.wxiaoqi.security.common.mapper.casino.CasinoParamMapper;
import com.github.wxiaoqi.security.common.mapper.casino.CasinoRebateConfigMapper;
import com.github.wxiaoqi.security.common.mapper.front.CmsConfigMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.DataSortUtil;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.model.IconModel;
import com.github.wxiaoqi.security.common.vo.ValuationModeVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CacheBiz {
    private Logger logger = LogManager.getLogger();

    @Autowired
    private CfgDcRechargeWithdrawMapper cfgDcRechargeWithdrawMapper;

    @Autowired
    private CfgCurrencyTransferMapper cfgCurrencyTransferMapper;

    @Autowired
    private FrontCountryMapper frontCountryMapper;

    @Autowired
    private BasicSymbolMapper basicSymbolMapper;

    @Autowired
    private DictDataMapper dictDataMapper;

    @Autowired
    private CfgCurrencyChargeMapper cfgCurrencyChargeMapper;

    @Autowired
    private ParamMapper paramMapper;

    @Autowired
    private WhiteExchInfoMapper whiteExchInfoMapper;

    @Autowired
    private FrontAdvertMapper frontAdvertMapper;

    @Autowired
    private FrontNoticeMapper frontNoticeMapper;

    @Autowired
    private BaseVersionMapper baseVersionMapper;

    @Autowired
    private BasicSymbolImageMapper basicSymbolImageMapper;

    @Autowired
    private CfgSymbolDescriptionMapper cfgSymbolDescriptionMapper;

    @Autowired
    private TransferExchMapper transferExchMapper;

    @Autowired
    private ValuationModeMapper valuationModeMapper;

    @Autowired
    private CfgDescriptionTemplateMapper cfgDescriptionTemplateMapper;


    @Autowired
    private CmsConfigMapper cmsConfigMapper;


    @Autowired
    private CasinoParamMapper casinoParamMapper;


    @Autowired
    private CasinoRebateConfigMapper casinoRebateConfigMapper;


    public List<CfgDcRechargeWithdraw> cacheReturnRechargeWithdraw() {
        List<CfgDcRechargeWithdraw> list = null;
        try {
            CfgDcRechargeWithdraw withdraw = new CfgDcRechargeWithdraw();
            withdraw.setStatus(EnableType.ENABLE.value());
            list = cfgDcRechargeWithdrawMapper.select(withdraw);
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
            logger.info("前端获取缓存失败,再次缓存CfgDcRechargeWithdraw 缓存条数：{}" + list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<CfgCurrencyTransfer> cacheReturnCurrencyTransfer() {
        List<CfgCurrencyTransfer> list = null;
        try {
            list = cfgCurrencyTransferMapper.cacheReturn();
            //按照交易所缓存
            Map<Long, ArrayList<CfgCurrencyTransfer>> allMap = InstanceUtil.newHashMap();
            if (list.size() > 0) {
                list.forEach(transfer -> {
                    if (allMap.get(transfer.getExchId()) != null) {
                        ArrayList<CfgCurrencyTransfer> cfgCurrencyTransfers = allMap.get(transfer.getExchId());
                        cfgCurrencyTransfers.add(transfer);
                    } else {
                        ArrayList<CfgCurrencyTransfer> arrayList = new ArrayList<>();
                        arrayList.add(transfer);
                        allMap.put(transfer.getExchId(), arrayList);
                    }
                });

            }
            allMap.forEach((k, v) -> {
                //通过源货币和交易所id 分组缓存
                CacheUtil.getCache().set(Constants.CacheServiceType.CFG_CURRENCY_TRANSFER_EXCH + ":" + k, v);
                v.forEach(transfer -> {
                    //根据源货币查询对应的目标货币集合并缓存
                    List<CfgCurrencyTransfer> srcSymbol = cfgCurrencyTransferMapper.selectTransferBySrcSymbol(transfer.getSrcSymbol(), transfer.getExchId());
                    CacheUtil.getCache().set(Constants.CacheServiceType.CFG_CURRENCY_TRANSFER_STRING + transfer.getSrcSymbol() + ":" + transfer.getExchId(), (ArrayList<CfgCurrencyTransfer>) srcSymbol);
                });
            });
            HashMap<String, Object> param = new HashMap<>();
            param.put("status", EnableType.ENABLE.value());
            List<CfgCurrencyTransfer> all = cfgCurrencyTransferMapper.selectQuery(param);
            //缓存单个CfgCurrencyTransfer 对象  参数: 源货币 + ":" + 目标货币
            if (StringUtil.listIsNotBlank(all)) {
                all.forEach(cfg -> {
                    CacheUtil.getCache().set(Constants.CacheServiceType.CFG_CURRENCY_TRANSFER_SYMBOL + cfg.getSrcSymbol() + ":" + cfg.getDstSymbol(), cfg);
                });
            }
            logger.info("前端获取缓存失败,再次缓存CfgCurrencyTransfer 缓存条数：{}" + list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<FrontCountry> cacheReturnFrontCountry() {
        List<FrontCountry> list = null;
        try {
            FrontCountry frontCountry = new FrontCountry();
            frontCountry.setStatus(EnableType.ENABLE.value());
            list = frontCountryMapper.select(frontCountry);
            DataSortUtil.sortAsc(list, "sort");
            CacheUtil.getCache().set(Constants.CacheServiceType.FRONT_COUNTRY, (ArrayList<FrontCountry>) list);
            logger.info("前端获取缓存失败,再次缓存 FrontCountry 缓存条数：{}" + list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<BasicSymbol> cacheReturnBasicSymbol() {
        List<BasicSymbol> list = null;
        try {
            list = basicSymbolMapper.cacheReturn();
            Map<Long, ArrayList<BasicSymbol>> allMap = InstanceUtil.newHashMap();
            if (list.size() > 0) {
                list.forEach(basicSymbol -> {
                    if (allMap.get(basicSymbol.getExchId()) != null) {
                        ArrayList<BasicSymbol> symbols = allMap.get(basicSymbol.getExchId());
                        symbols.add(basicSymbol);
                    } else {
                        ArrayList<BasicSymbol> basicSymbols = new ArrayList<>();
                        basicSymbols.add(basicSymbol);
                        allMap.put(basicSymbol.getExchId(), basicSymbols);
                    }
                });
                allMap.forEach((k, v) -> {
                    CacheUtil.getCache().set(Constants.CacheServiceType.BASIC_SYMBOL_EXCH + ":" + k, v);
                });
            }
            Example example = new Example(BasicSymbol.class);
            Example.Criteria criteria = example.createCriteria();
            example.setOrderByClause("sort");
            criteria.andEqualTo("status", EnableType.ENABLE.value());
            List<BasicSymbol> symbols = basicSymbolMapper.selectByExample(example);
            symbols.forEach(basicSymbol -> {
                CacheUtil.getCache().set(Constants.CacheServiceType.BASIC_SYMBOL + basicSymbol.getSymbol(), basicSymbol);
            });
            CacheUtil.getCache().set(Constants.CacheServiceType.BASIC_SYMBOL, (ArrayList<BasicSymbol>) symbols);

            BasicSymbol basicSymbol = new BasicSymbol();
            basicSymbol.setStatus(EnableType.ENABLE.value());
            //带重复的币种信息
            List<BasicSymbol> repeatSymbolList = basicSymbolMapper.select(basicSymbol);
            CacheUtil.getCache().set(Constants.CacheServiceType.BASIC_SYMBOL_REPEAT, (ArrayList<BasicSymbol>) repeatSymbolList);

            logger.info("前端获取缓存失败,再次缓存 BasisSymbol 缓存条数：{}" + list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void cacheReturnDictData() {
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.DICT_DATA);
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.DICT_DATA_DATA);// redis 可能存在旧数据
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.DICT_DATA_MAP);
        try {
            //获取类型集合
            List<DictData> dictDataList = dictDataMapper.GroupDictData();
            dictDataList.forEach(dictData -> {
                //根据type获取数据集合
                List<DictData> list = dictDataMapper.selectListData(dictData.getDictType(), "");
                //没有语言属性   直接缓存
                if (StringUtils.isEmpty(list.get(0).getLanguageType())) {
                    DataSortUtil.sortAsc(list, "sort");
                    CacheUtil.getCache().set(Constants.CacheServiceType.DICT_DATA + dictData.getDictType(), (ArrayList<DictData>) list);
                    //根据字典类型Map集合
                    HashMap<String, Object> map = new HashMap<>();
                    for (DictData data1 : list) {
                        map.put(data1.getDictLabel(), data1.getDictValue());
                    }
                    CacheUtil.getCache().set(Constants.CacheServiceType.DICT_DATA_MAP + dictData.getDictType(), map);
                } else {
                    //表示配置语言属性
                    HashMap<String, ArrayList<DictData>> dictDataLanguageMap = new HashMap<>();
                    list.forEach(data -> {
                        if (dictDataLanguageMap.get(data.getDictType() + ":" + data.getLanguageType()) != null) {
                            ArrayList<DictData> arrayList = dictDataLanguageMap.get(data.getDictType() + ":" + data.getLanguageType());
                            arrayList.add(data);
                        } else {
                            ArrayList<DictData> arrayList = new ArrayList<>();
                            arrayList.add(data);
                            dictDataLanguageMap.put(data.getDictType() + ":" + data.getLanguageType(), arrayList);
                        }
                    });
                    dictDataLanguageMap.forEach((k, v) -> {
                        CacheUtil.getCache().set(Constants.CacheServiceType.DICT_DATA + k, v);
                        //根据字典类型Map集合+ 语言
                        HashMap<String, Object> map = new HashMap<>();
                        v.forEach(dictData1 -> {
                            map.put(dictData.getDictLabel(), dictData.getDictValue());
                        });
                        CacheUtil.getCache().set(Constants.CacheServiceType.DICT_DATA_MAP + k, map);
                    });
                }
            });
            logger.info("前端获取缓存失败,再次缓存 DictData 缓存条数：{}" + dictDataList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 查库并缓存
     */
    public void cacheReturnCurrencyCharge() {
        try {
            List<CfgCurrencyCharge> charges = cfgCurrencyChargeMapper.selectChargeList();
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
            logger.info("前端获取缓存失败,再次缓存 CfgCurrencyCharge 缓存条数：{}" + charges.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 系统参数
     *
     * @return
     */
    public List<Param> cacheReturnParam() {
        List<Param> list = null;
        try {
            Param param = new Param();
            param.setStatus(EnableType.ENABLE.value());
            list = paramMapper.select(param);
            list.forEach((p -> {
                CacheUtil.getCache().set(Constants.BaseParam.SYSPARAM + p.getParamKey(), p);
            }));
            logger.info("前端获取缓存失败,再次缓存 Param 缓存条数：{}" + list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<WhiteExchInfo> cacheExchInfo() {
        WhiteExchInfo info = new WhiteExchInfo();
        info.setStatus(EnableType.ENABLE.value());
        List<WhiteExchInfo> list = whiteExchInfoMapper.select(info);
        list.forEach(whiteExchInfo -> {
            CacheUtil.getCache().set(Constants.CacheServiceType.WHITE_EXCH_INFO + whiteExchInfo.getDomainName(), whiteExchInfo);
        });
        logger.info("前端获取缓存失败,再次缓存 WhiteExchInfo 缓存条数：{}" + list.size());
        return list;

    }

    public List<FrontAdvert> cacheReturnFrontAdvert() {
        FrontAdvert frontAdvert = new FrontAdvert();
        frontAdvert.setStatus(EnableType.ENABLE.value());
        List<FrontAdvert> list = frontAdvertMapper.select(frontAdvert);
        HashMap<String, List<FrontAdvert>> map = new HashMap<>();
        list.forEach(advert -> {
            if (map.get(advert.getExchangeId() + ":" + advert.getLanguageType() + ":" + advert.getClientType()) != null) {
                List<FrontAdvert> adList = map.get(advert.getExchangeId() + ":" + advert.getLanguageType() + ":" + advert.getClientType());
                adList.add(advert);
            } else {
                ArrayList<FrontAdvert> advertList = new ArrayList<>();
                advertList.add(advert);
                map.put(advert.getExchangeId() + ":" + advert.getLanguageType() + ":" + advert.getClientType(), advertList);
            }
        });
        map.forEach((k, v) -> {
            CacheUtil.getCache().set(Constants.CacheServiceType.FRONT_ADVERT + k, (ArrayList) v);
        });
        logger.info("前端获取缓存失败,再次缓存 FrontAdvert 缓存条数：{}" + list.size());
        return list;
    }

    public List<FrontNotice> cacheReturnFrontNotice() {
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.FRONT_NOTICE);
        FrontNotice frontNotice = new FrontNotice();
        frontNotice.setStatus(EnableType.ENABLE.value());
        List<FrontNotice> list = frontNoticeMapper.select(frontNotice);
        HashMap<String, List<FrontNotice>> map = new HashMap<>();
        list.forEach(notice -> {
            if (map.get(notice.getExchangeId() + ":" + notice.getLanguageType() + ":" + notice.getClientType()) != null) {
                List<FrontNotice> adList = map.get(notice.getExchangeId() + ":" + notice.getLanguageType() + ":" + notice.getClientType());
                adList.add(notice);
            } else {
                ArrayList<FrontNotice> advertList = new ArrayList<>();
                advertList.add(notice);
                map.put(notice.getExchangeId() + ":" + notice.getLanguageType() + ":" + notice.getClientType(), advertList);
            }
        });
        map.forEach((k, v) -> {
            CacheUtil.getCache().set(Constants.CacheServiceType.FRONT_NOTICE + k, (ArrayList) v);
        });
        logger.info("前端获取缓存失败,再次缓存 FrontNotice  缓存条数：{}" + list.size());
        return list;
    }

    public List<BaseVersion> cacheReturnBaseVersion() {
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.BASE_VERSION);
        List<BaseVersion> list = baseVersionMapper.selectAll();
        list.forEach(baseVersion -> {
            CacheUtil.getCache().set(Constants.CacheServiceType.BASE_VERSION + baseVersion.getVersionChannel() + ":" + baseVersion.getExchId(), baseVersion);
        });
        logger.info("前端获取缓存失败,再次缓存 BaseVersion  缓存条数：{}" + list.size());
        return list;

    }

    public List<IconModel> cacheReturnBasicSymbolIcon() {
        List<IconModel> imageList = null;
        List<IconModel> basicSymbolList = null;
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.BASIC_SYMBOL_IMAGE);
        try {
            //获取默认币种图标集合
            basicSymbolList = basicSymbolMapper.selectListIcon(EnableType.ENABLE.value());
            //获取交易所个性币种图标集合
            imageList = basicSymbolImageMapper.selectExchAndImage();
            HashMap<String, IconModel> map = new HashMap<>();
            if (StringUtil.listIsNotBlank(imageList)) {
                imageList.forEach(exchImage -> {
                    map.put(exchImage.getSymbol() + exchImage.getExchangeId(), exchImage);
                });
            }


            Map<Long, ArrayList<IconModel>> allMap = InstanceUtil.newHashMap();
            for (WhiteExchInfo whiteExchInfo : whiteExchInfoMapper.selectAll()) {
                ArrayList<IconModel> list = new ArrayList<>();
                basicSymbolList.forEach(basicIcon -> {
                    if (map.get(basicIcon.getSymbol() + whiteExchInfo.getId()) != null) {
                        list.add(map.get(basicIcon.getSymbol() + whiteExchInfo.getId()));
                    } else {
                        list.add(basicIcon);
                    }
                });
                allMap.put(whiteExchInfo.getId(), list);
            }
            allMap.forEach((k, v) -> {
                CacheUtil.getCache().set(Constants.CacheServiceType.BASIC_SYMBOL_IMAGE + k, v);
            });
            logger.info("前端获取缓存失败,再次缓存 IconModel  缓存条数：{}" + basicSymbolList.size());
        } catch (Exception e) {
            logger.error("加载币种图标缓存时异常", e);
        }
        return basicSymbolList;
    }

    public void cacheReturnSymbolDescription() {
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.CFG_SYMBOL_DESCRIPTION);
        List<CfgSymbolDescription> descriptions = cfgSymbolDescriptionMapper.selectAll();

        //获取所有币种
        List<BasicSymbol> list = basicSymbolMapper.selectAll();

        List<WhiteExchInfo> exchInfos = whiteExchInfoMapper.selectAll();
        //获取所有语言
        List<DictData> dictData = dictDataMapper.selectListData(Constants.DirtTypeConstant.LANGUAGE, null);
        if (StringUtil.listIsBlank(list) && StringUtil.listIsBlank(dictData) && StringUtil.listIsBlank(exchInfos)) {
            return;
        }

        //没有设置描述信息
        if (StringUtil.listIsBlank(descriptions)) {

            for (WhiteExchInfo info : exchInfos) {

                for (DictData data : dictData) {
                    //遍历币种
                    list.forEach(basicSymbol -> {
                        //获取代币限制配置信息
                        CfgDcRechargeWithdraw config = getCfgDcRechargeWithdraw(info, basicSymbol);
                        if (config != null) {
                            saveCfgSymbolDescriptionRedis(info, basicSymbol, data, config);
                        }
                    });
                }
            }
        } else {
            //遍历交易所
            for (WhiteExchInfo info : exchInfos) {

                //遍历币种
                for (BasicSymbol basicSymbol : list) {
                    //遍历语言
                    for (DictData data : dictData) {
                        boolean isExists = false;
                        for (CfgSymbolDescription d : descriptions) {
                            if (d.getSymbol().equals(basicSymbol.getSymbol()) && d.getExchangeId().equals(info.getId()) && d.getLanguageType().equals(data.getDictValue())) {
                                CacheUtil.getCache().set(Constants.CacheServiceType.CFG_SYMBOL_DESCRIPTION + basicSymbol.getSymbol() + ":" + info.getId() + ":" + d.getLanguageType() + ":" + d.getProtocolType(), d);
                                if (EnableType.ENABLE.value().equals(d.getIsShow())) {
                                    CacheUtil.getCache().set(Constants.CacheServiceType.SYMBOL_DESCRIPTION + basicSymbol.getSymbol() + ":" + info.getId() + ":" + d.getLanguageType(), d);
                                }
                                isExists = true;
                            }
                        }
                        //表示没有配置内容 则读默认模板
                        if (!isExists) {
                            CfgDcRechargeWithdraw config = getCfgDcRechargeWithdraw(info, basicSymbol);
                            if (config != null) {
                                saveCfgSymbolDescriptionRedis(info, basicSymbol, data, config);
                            }
                            isExists = false;
                        }

                    }
                }
            }
        }
        logger.info("前端获取缓存失败,再次缓存 CfgSymbolDescription  缓存条数：{}" + descriptions.size());
    }


    private void saveCfgSymbolDescriptionRedis(WhiteExchInfo info, BasicSymbol basicSymbol, DictData data, CfgDcRechargeWithdraw config) {
        //根据语言获取模板
        CfgDescriptionTemplate templates = cfgDescriptionTemplateMapper.selectDespByLanguage(data.getDictValue());
        if (templates ==null){
            return;
        }
        CfgSymbolDescription desp = new CfgSymbolDescription();
        desp.setExchangeId(info.getId());
        desp.setSymbol(basicSymbol.getSymbol().trim());
        //设置语言
        desp.setLanguageType(data.getDictValue());
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
        CacheUtil.getCache().set(Constants.CacheServiceType.CFG_SYMBOL_DESCRIPTION + symbolType+ ":" + info.getId() + ":" + data.getDictValue(), desp);
        if (EnableType.ENABLE.value().equals(basicSymbol.getIsShow())){
            CacheUtil.getCache().set(Constants.CacheServiceType.SYMBOL_DESCRIPTION + basicSymbol.getSymbol().trim() + ":" + info.getId() + ":" + data.getDictValue(), desp);
        }
    }

    private CfgDcRechargeWithdraw getCfgDcRechargeWithdraw(WhiteExchInfo info, BasicSymbol basicSymbol) {
        String symbolType=basicSymbol.getSymbol().trim();
        if (!StringUtils.isEmpty(basicSymbol.getProtocolType())) {
            symbolType = symbolType +basicSymbol.getProtocolType();
        }

        CfgDcRechargeWithdraw config = null;
        config = (CfgDcRechargeWithdraw) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_DCRECHARGE_WITHDRAW_EXCH + symbolType+ ":" + info.getId());
        if (config == null) {
            config = (CfgDcRechargeWithdraw) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_DCRECHARGE_WITHDRAW_EXCH + symbolType+ ":" + AdminCommonConstant.ROOT);
            if (config==null){
                config = (CfgDcRechargeWithdraw) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_DCRECHARGE_WITHDRAW + basicSymbol.getSymbol().trim() + ":" + info.getId());
                if (config==null){
                    config = (CfgDcRechargeWithdraw) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_DCRECHARGE_WITHDRAW + basicSymbol.getSymbol().trim() + ":" + AdminCommonConstant.ROOT);
                }
            }
        }
        return config;
    }

    public void cacheReturnTransferExch() {
        //根据key 删除 redis 中所有
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.TRANSFER_EXCH);
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.TRANSFER_EXCH_LIST);
        try {
            List<TransferExch> list = transferExchMapper.selectChargeAll();
            Map<Long, ArrayList<TransferExch>> allMap = InstanceUtil.newHashMap();
            if (StringUtil.listIsNotBlank(list)) {
                list.forEach(exch -> {
                    if (allMap.get(exch.getExchId()) == null) {
                        ArrayList<TransferExch> arrayList = new ArrayList<>();
                        arrayList.add(exch);
                        allMap.put(exch.getExchId(), arrayList);
                    } else {
                        ArrayList<TransferExch> cfgCurrencyTransfers = allMap.get(exch.getExchId());
                        cfgCurrencyTransfers.add(exch);
                    }
                    //缓存Key UDAX-WALLET:transferExch:exchId:srcSymbol:dstSymbol
                    CacheUtil.getCache().set(Constants.CacheServiceType.TRANSFER_EXCH + exch.getExchId() + ":" + exch.getSrcSymbol() + ":" + exch.getDstSymbol(), exch);
                });
                allMap.forEach((k, v) -> {
                    //通过交易所id 缓存
                    CacheUtil.getCache().set(Constants.CacheServiceType.TRANSFER_EXCH_LIST + k, v);
                });
            }
            logger.info("前端获取缓存失败,再次缓存 TransferExch  缓存条数：{}" + list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void cacheValuationModeVo() {
        //清除缓存
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.VALUATION_MODE);
        Map<String, ArrayList<ValuationModeVo>> allMap = InstanceUtil.newHashMap();
        List<ValuationModeVo> list = valuationModeMapper.cacheReturn();
        if (StringUtil.listIsNotBlank(list)) {
            list.forEach(vo -> {
                if (allMap.get(vo.getExchId() + ":" + vo.getLanguageType()) == null) {
                    ArrayList<ValuationModeVo> arrayList = new ArrayList<>();
                    arrayList.add(vo);
                    allMap.put(vo.getExchId() + ":" + vo.getLanguageType(), arrayList);
                } else {
                    ArrayList<ValuationModeVo> cfgCurrencyTransfers = allMap.get(vo.getExchId() + ":" + vo.getLanguageType());
                    cfgCurrencyTransfers.add(vo);
                }
            });
        }
        allMap.forEach((k, v) -> {
            CacheUtil.getCache().set(Constants.CacheServiceType.VALUATION_MODE + k, v);
        });
        Map<String, ArrayList<ValuationModeVo>> allMap2 = InstanceUtil.newHashMap();
        list.forEach(vo -> {
            if (allMap2.get(vo.getExchId()) == null) {
                ArrayList<ValuationModeVo> arrayList = new ArrayList<>();
                arrayList.add(vo);
                allMap2.put(vo.getExchId() + "", arrayList);
            } else {
                ArrayList<ValuationModeVo> vos = allMap2.get(vo.getExchId());
                vos.add(vo);
            }
        });

        allMap2.forEach((k, v) -> {
            CacheUtil.getCache().set(Constants.CacheServiceType.VALUATION + k, v);
        });
        logger.info("前端获取缓存失败,再次缓存 ValuationModeVo  缓存条数：{}" + list.size());
    }

    public void cacheReturnCmsConfig() {
        CmsConfig config = new CmsConfig();
        config.setEnable(EnableType.ENABLE.value());
        List<CmsConfig> configList = cmsConfigMapper.select(config);
        Map<Long, List<CmsConfig>> configListMap = configList.stream().collect(Collectors.groupingBy(CmsConfig::getExchId));
        configListMap.forEach((exchId, cmsConfigs) -> {
            CacheUtil.getCache().set(Constants.CacheServiceType.CMS_CONFIG_BIZ + exchId, (ArrayList<CmsConfig>) cmsConfigs);
        });
        logger.info("前端获取缓存失败,再次缓存 CmsConfig  缓存条数：{}" + configList.size());
    }
    //获取行情价
    public Map<String,BigDecimal> getQuotation(){
        Map<String,BigDecimal> map = (Map<String,BigDecimal>)CacheUtil.getCache().hgetAll(Constants.CommonType.QUOTATION);
        if(map == null){
            map = InstanceUtil.newHashMap();
        }
        map.put("USDT/USDT",BigDecimal.ONE);
        return map;
    }

    public void cacheReturnCasinoParam() {
        List<CasinoParam> list = casinoParamMapper.selectAll();
        if (StringUtil.listIsBlank(list)){
            return;
        }
        list.forEach(casinoParam -> {
            CacheUtil.getCache().set(Constants.CACHE_NAMESPACE+casinoParam.getCasinoKey()+casinoParam.getExchId(), casinoParam);
        });
        logger.info("前端获取缓存失败,再次缓存 CasinoParam  缓存条数：{}" + list.size());
    }

    public void cacheReturnCasinoRebateConfig() {
        CasinoRebateConfig rebateConfig = new CasinoRebateConfig();
        rebateConfig.setEnable(EnableType.ENABLE.value());
        List<CasinoRebateConfig> list = casinoRebateConfigMapper.select(rebateConfig);
        if (StringUtil.listIsBlank(list)){
            return;
        }
        Map<Long, List<CasinoRebateConfig>> longListMap = list.stream().collect(Collectors.groupingBy(CasinoRebateConfig::getExchId));
        longListMap.forEach((aLong, casinoRebateConfigs) -> {
            CacheUtil.getCache().set(Constants.CacheServiceType.CASINO_REBATE_CONFIG+aLong, (ArrayList)casinoRebateConfigs);
        });
        logger.info("前端获取缓存失败,再次缓存 CasinoRebateConfig  缓存条数：{}" + list.size());
    }
}
