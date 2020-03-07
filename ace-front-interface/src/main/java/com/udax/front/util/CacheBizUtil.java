package com.udax.front.util;

import com.alibaba.fastjson.JSON;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.*;
import com.github.wxiaoqi.security.common.entity.casino.CasinoParam;
import com.github.wxiaoqi.security.common.entity.casino.CasinoRebateConfig;
import com.github.wxiaoqi.security.common.entity.common.SendMsg;
import com.github.wxiaoqi.security.common.entity.front.CmsConfig;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.enums.CurrencyType;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.LocalDateUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.model.IconModel;
import com.github.wxiaoqi.security.common.vo.ValuationModeVo;
import com.udax.front.biz.CacheBiz;
import com.udax.front.biz.FrontUserBiz;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * 缓存业务逻辑工具类
 */
public class CacheBizUtil {
    protected static Logger logger = LogManager.getLogger(CacheBizUtil.class);

//    /**
//     * 获取密码免输入的时间
//     *
//     * @return
//     */
//    public static int getPwdExpire() {
//        String time = (String) CacheUtil.getCache().get(SYSPARAM + ":PWD_EXPIRY_DATE");
//        if (StringUtils.isBlank(time)) {
//            return 120;// 默认2小时
//        }
//        return Integer.parseInt(time);
//    }

    //获取各种类型订单的失效时间
    public static Date getExpireTime(String expireType, CacheBiz cacheBiz) {
        Param param = getParam(expireType, cacheBiz);
        int expireTime = Integer.valueOf(param.getParamValue());
        LocalDateTime nowTime = LocalDateTime.now();
        return LocalDateUtil.localDateTime2Date(nowTime.plusSeconds(expireTime));
    }


    /**
     * 根据用户Id获取用户缓存
     *
     * @param userId
     */
    public static FrontUser getFrontUserCache(Long userId, FrontUserBiz biz) {
        if (userId.longValue() < 1) {
            return null;
        }
        String json = (String) CacheUtil.getCache().hget(
                Constants.CacheServiceType.FRONTUSER + (userId.longValue() % Constants.REDIS_MAP_BATCH),
                userId.toString());
        FrontUser fu = new FrontUser();
        if (StringUtils.isBlank(json)) {
            fu = biz.cacheAndReturn(userId);
        } else {
            fu = JSON.parseObject(json, FrontUser.class);
            if (fu.getUserInfo() == null) {
                fu = biz.cacheAndReturn(userId);
            }
        }
        return fu;
    }

    /**
     * 设置短信缓存内容
     *
     * @param mobile
     * @param sendMsg
     */
    public static void setCacheSmgMsg(String mobile, SendMsg sendMsg) {

        CacheUtil.getCache().set(Constants.CommonType.SMSCODE + mobile, JSON.toJSONString(sendMsg), 300);

    }

    /**
     * 通过手机号获取短信缓存内容
     *
     * @param mobile
     * @return
     */
    public static SendMsg getCacheSmsMsg(String mobile) {
        SendMsg msg = null;
        String msgInfo = (String) CacheUtil.getCache().get(Constants.CommonType.SMSCODE + mobile);
        if (StringUtils.isNotBlank(msgInfo)) {
            msg = JSON.parseObject(msgInfo, SendMsg.class);
        }
        return msg;
    }

    /**
     * 清楚验证码缓存内容
     *
     * @param mobile
     * @return
     */
    public static void clearSmsMsg(String mobile) {
        CacheUtil.getCache().del(Constants.CommonType.SMSCODE + mobile);
    }


    /**
     * 获取以太坊代币信息
     *
     * @return
     */
    public static Map<String, Object> getTokenListCache(CacheBiz biz) {
        Map<String, Object> symbolMap = (Map<String, Object>) CacheUtil.getCache()
                .get(Constants.CacheServiceType.DICT_DATA_MAP + Constants.DirtTypeConstant.SYMBOL_LIST);
        if (StringUtil.mapIsBlank(symbolMap)) {
            biz.cacheReturnDictData();
            symbolMap = (Map<String, Object>) CacheUtil.getCache()
                    .get(Constants.CacheServiceType.DICT_DATA_MAP + Constants.DirtTypeConstant.SYMBOL_LIST);
        }
        return symbolMap;
    }

    /**
     * 获取代币费率信息(带链类型)
     *
     * @param symbol
     * @param exchId
     * @param protocolType
     * @return
     */
    public static CfgCurrencyCharge getSymbolCharge(CacheBiz biz, String symbol, Long exchId, String protocolType) {
        String symbolType = symbol;
        if (!StringUtils.isEmpty(protocolType)) {
            symbolType = symbol + protocolType;
        }
        CfgCurrencyCharge cfgCurrencyCharge = null;
        //根据币种和交易所id 获取代币费率配置信息
        cfgCurrencyCharge = (CfgCurrencyCharge) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_CURRENCY_CHARGE + symbolType + exchId);
        if (cfgCurrencyCharge == null) {
            cfgCurrencyCharge = (CfgCurrencyCharge) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_CURRENCY_CHARGE + symbolType + AdminCommonConstant.ROOT);
        }
        if (cfgCurrencyCharge == null) {
            cfgCurrencyCharge = (CfgCurrencyCharge) CacheUtil.getCache().get(Constants.CacheServiceType.CURRENCY_CHARGE + symbol + exchId);

        }
        if (cfgCurrencyCharge == null) {
            //没有取默认的
            cfgCurrencyCharge = (CfgCurrencyCharge) CacheUtil.getCache().get(Constants.CacheServiceType.CURRENCY_CHARGE + symbol + AdminCommonConstant.ROOT);
        }
        if (cfgCurrencyCharge == null) {
            biz.cacheReturnCurrencyCharge();
            cfgCurrencyCharge = (CfgCurrencyCharge) CacheUtil.getCache().get(Constants.CacheServiceType.CURRENCY_CHARGE + symbol + AdminCommonConstant.ROOT);
        }
        return cfgCurrencyCharge;
    }


    /**
     * 获取代币限制配置信息(带链类型)
     * 参数: symbol
     */
    public static CfgDcRechargeWithdraw getSymbolConfigShow(String symbol, String protocolType, Long exchId, CacheBiz biz) {
        String symbolType = symbol;
        if (!StringUtils.isEmpty(protocolType)) {
            symbolType = symbolType + protocolType;
        }
        CfgDcRechargeWithdraw config = null;
        config = (CfgDcRechargeWithdraw) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_DCRECHARGE_WITHDRAW_EXCH + symbolType + ":" + exchId);
        if (config == null) {
            config = (CfgDcRechargeWithdraw) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_DCRECHARGE_WITHDRAW_EXCH + symbolType + ":" + AdminCommonConstant.ROOT);
            if (config == null) {
                config = (CfgDcRechargeWithdraw) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_DCRECHARGE_WITHDRAW + symbol + ":" + exchId);
                if (config == null) {
                    biz.cacheReturnRechargeWithdraw();
                    config = (CfgDcRechargeWithdraw) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_DCRECHARGE_WITHDRAW + symbol + ":" + AdminCommonConstant.ROOT);
                }
            }
        }
        return config;
    }


    /**
     * 获取货币配置Map<symbol:exchId,CfgDcRechargeWithdraw>
     * 如果通过key :   symbol:exchId 获取为Null
     * 则  symbol:AdminCommonConstant.ROOT 取默认配置
     */

    public static Map<String, CfgDcRechargeWithdraw> getSymbolConfig(CacheBiz biz) {
        Map<String, CfgDcRechargeWithdraw> map = null;
        map = (Map<String, CfgDcRechargeWithdraw>) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_DCRECHARGE_WITHDRAW_MAP);
        if (map.isEmpty()) {
            biz.cacheReturnRechargeWithdraw();
            map = (Map<String, CfgDcRechargeWithdraw>) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_DCRECHARGE_WITHDRAW_MAP);
        }
        return map;
    }


    /**
     * 获取代币限制配置信息
     */
    public static String getWithdrawLockTime() {
        return (String) CacheUtil.getCache().get(Constants.LOCK_WITHDRAW_TIME);
    }

    /**
     * 根据代币获取可交易的币种列表 CurrencyTransferBiz
     *
     * @param biz
     * @param exchId 交易所id
     * @return
     */
    public static List<CfgCurrencyTransfer> getSymbolTransferList(CacheBiz biz, Long exchId) {
        List<CfgCurrencyTransfer> list = null;
        list = (List<CfgCurrencyTransfer>) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_CURRENCY_TRANSFER_EXCH + ":" + exchId);
        if (StringUtil.listIsBlank(list)) {
            biz.cacheReturnCurrencyTransfer();
            list = (List<CfgCurrencyTransfer>) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_CURRENCY_TRANSFER + exchId);
        }
        return list == null ? new ArrayList<>() : list;
    }

    /**
     * 根据代币获取可交易的币种列表
     *
     * @param srcSymbol:dstSymbol(转换币+目标币)
     */
    public static CfgCurrencyTransfer getSymbolTransfer(String srcSymbol, String dstSymbol, CacheBiz biz) {
        CfgCurrencyTransfer transfer = null;
        transfer = (CfgCurrencyTransfer) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_CURRENCY_TRANSFER_SYMBOL + srcSymbol + ":" + dstSymbol);
        if (transfer == null) {
            biz.cacheReturnCurrencyTransfer();
            transfer = (CfgCurrencyTransfer) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_CURRENCY_TRANSFER_SYMBOL + srcSymbol + ":" + dstSymbol);
        }
        return transfer;
    }

    /**
     * 获取国家列表
     * 参数:frontCountryBiz
     */
    public static List<FrontCountry> getFrontCountry(CacheBiz biz) {
        List<FrontCountry> list = null;
        list = (List<FrontCountry>) CacheUtil.getCache().get(Constants.CacheServiceType.FRONT_COUNTRY);
        if (StringUtil.listIsBlank(list)) {
            biz.cacheReturnFrontCountry();
            list = (List<FrontCountry>) CacheUtil.getCache().get(Constants.CacheServiceType.FRONT_COUNTRY);
        }
        return list == null ? new ArrayList<>() : list;
    }

    /**
     * 单个币种
     *
     * @param symbol
     * @param biz
     * @return
     */

    public static BasicSymbol getBasicSymbol(String symbol, CacheBiz biz) {
        BasicSymbol basicSymbol = (BasicSymbol) CacheUtil.getCache().get(Constants.CacheServiceType.BASIC_SYMBOL + symbol);
        if (basicSymbol == null) {
            biz.cacheReturnBasicSymbol();
            basicSymbol = (BasicSymbol) CacheUtil.getCache().get(Constants.CacheServiceType.BASIC_SYMBOL + symbol);
        }
        return basicSymbol;
    }

    /**
     * 获取基础币种(List)
     */
    public static List<BasicSymbol> getBasicSymbol(CacheBiz biz) {
        List<BasicSymbol> list = (List<BasicSymbol>) CacheUtil.getCache().get(Constants.CacheServiceType.BASIC_SYMBOL);
        if (StringUtil.listIsBlank(list)) {
            biz.cacheReturnBasicSymbol();
            list = (List<BasicSymbol>) CacheUtil.getCache().get(Constants.CacheServiceType.BASIC_SYMBOL);
        }
        return list == null ? new ArrayList<>() : list;
    }


    /**
     * 获取(重复)基础币种(List)
     */
    public static List<BasicSymbol> repeatSymbolList(CacheBiz biz) {
        List<BasicSymbol> list = (List<BasicSymbol>) CacheUtil.getCache().get(Constants.CacheServiceType.BASIC_SYMBOL_REPEAT);
        if (StringUtil.listIsBlank(list)) {
            biz.cacheReturnBasicSymbol();
            list = (List<BasicSymbol>) CacheUtil.getCache().get(Constants.CacheServiceType.BASIC_SYMBOL_REPEAT);
        }
        return list == null ? new ArrayList<>() : list;
    }


    /**
     * 根据交易所获取币种信息
     */
    public static List<BasicSymbol> getBasicSymbolExch(CacheBiz biz, Long exchId) {
        List<BasicSymbol> list = (List<BasicSymbol>) CacheUtil.getCache().get(Constants.CacheServiceType.BASIC_SYMBOL_EXCH + ":" + exchId);
        if (StringUtil.listIsBlank(list)) {
            biz.cacheReturnBasicSymbol();
            list = (List<BasicSymbol>) CacheUtil.getCache().get(Constants.CacheServiceType.BASIC_SYMBOL_EXCH + ":" + exchId);
        }
        return list == null ? new ArrayList<>() : list;
    }


    /**
     * 根据交易所获取币种信息
     */
    public static List<BasicSymbol> getBasicSymbolExchNotRepeat(CacheBiz biz, Long exchId) {
        List<BasicSymbol> list = getBasicSymbolExch(biz, exchId);
        if (StringUtil.listIsNotBlank(list)) {
            //根据symbol去重
            list = list.stream().collect(
                    collectingAndThen(
                            toCollection(() -> new TreeSet<>(comparing(n -> n.getSymbol()))), ArrayList::new)
            );
        }
        return list == null ? new ArrayList<>() : list;
    }


    /**
     * 获取图标
     */
    public static List<IconModel> getBasicSymbolIcon(CacheBiz biz, Long exchId) {
        List<IconModel> list = null;
        //根据交易所获取币种和图标
        list = (List<IconModel>) CacheUtil.getCache().get(Constants.CacheServiceType.BASIC_SYMBOL_IMAGE + exchId);
        if (StringUtil.listIsBlank(list)) {
            biz.cacheReturnBasicSymbolIcon();
            list = (List<IconModel>) CacheUtil.getCache().get(Constants.CacheServiceType.BASIC_SYMBOL_IMAGE + exchId);
        }
        return list == null ? new ArrayList<>() : list;
    }


    /**
     * 获取语言(数据字典)
     */

    public static List<DictData> getLanguage(CacheBiz biz) {
        List<DictData> list = null;
        list = (List<DictData>) CacheUtil.getCache().get(Constants.CacheServiceType.DICT_DATA + Constants.DirtTypeConstant.LANGUAGE);
        if (StringUtil.listIsBlank(list)) {
            biz.cacheReturnDictData();
            list = (List<DictData>) CacheUtil.getCache().get(Constants.CacheServiceType.DICT_DATA + Constants.DirtTypeConstant.LANGUAGE);
        }
        return list == null ? new ArrayList<>() : list;
    }

    /**
     * 获取系统参数
     */

    public static Param getParam(String paramKey, CacheBiz biz) {
        Param param;
        param = (Param) CacheUtil.getCache().get(paramKey);
        if (param == null) {
            List<Param> list = biz.cacheReturnParam();
            //是否从集合里面拿??
            param = (Param) CacheUtil.getCache().get(paramKey);
        }
        return param;
    }

    /**
     * 获取关联交易所id
     * 参数 :domainName
     */
    public static Long getExchId(String domainName, CacheBiz biz) {
        WhiteExchInfo info = null;
        info = (WhiteExchInfo) CacheUtil.getCache().get(Constants.CacheServiceType.WHITE_EXCH_INFO + domainName);
        if (info == null || info.getRegisterType() == null) {
            biz.cacheExchInfo();
            info = (WhiteExchInfo) CacheUtil.getCache().get(Constants.CacheServiceType.WHITE_EXCH_INFO + domainName);
        }
        return info != null ? info.getId() : null;
    }

    /**
     * 获取关联交易所標書
     * 参数 :domainName
     */
    public static String getExchInfo(Long exchId, CacheBiz biz) {
        WhiteExchInfo info = null;
        info = (WhiteExchInfo) CacheUtil.getCache().get(Constants.CacheServiceType.WHITE_EXCH_INFO + exchId);
        if (info == null || info.getRegisterType() == null) {
            biz.cacheExchInfo();
            info = (WhiteExchInfo) CacheUtil.getCache().get(Constants.CacheServiceType.WHITE_EXCH_INFO + exchId);
        }
        return info != null ? info.getDomainName() : null;
    }

    /**
     * 获取关联交易所標書
     * 参数 :domainName
     */
    public static Integer getExchRegisterType(Long exchId, CacheBiz biz) {
        WhiteExchInfo info = null;
        info = (WhiteExchInfo) CacheUtil.getCache().get(Constants.CacheServiceType.WHITE_EXCH_INFO + exchId);
        if (info == null) {
            biz.cacheExchInfo();
            info = (WhiteExchInfo) CacheUtil.getCache().get(Constants.CacheServiceType.WHITE_EXCH_INFO + exchId);
        }
        return info != null && info.getRegisterType() != null ? info.getRegisterType() : 1;
    }

    /**
     * 获取关联交易所排单系统币种
     * 参数 :domainName
     */
    public static String getLtCode(Long exchId, CacheBiz biz) {
        WhiteExchInfo info = null;
        info = (WhiteExchInfo) CacheUtil.getCache().get(Constants.CacheServiceType.WHITE_EXCH_INFO + exchId);
        if (info == null) {
            biz.cacheExchInfo();
            info = (WhiteExchInfo) CacheUtil.getCache().get(Constants.CacheServiceType.WHITE_EXCH_INFO + exchId);
        }
        return info != null ? info.getLtCode() : CurrencyType.USA.value();
    }


    /**
     * 获取广告
     * 根据交易所id
     * 客户端类型 app or pc
     * 语言如: zh  en 等
     *
     * @param exchId
     * @param biz
     * @return 返回结果集再次过滤 client_type (app 或者 pc )
     */
    public static List<FrontAdvert> getFrontAdvert(Long exchId, Integer clientType, String languageType, CacheBiz biz) {
        languageType = getLanguageApp(languageType);
        List<FrontAdvert> frontAdverts;
        frontAdverts = (List<FrontAdvert>) CacheUtil.getCache().get(Constants.CacheServiceType.FRONT_ADVERT + exchId + ":" + languageType + ":" + clientType);
        if (StringUtil.listIsBlank(frontAdverts)) {
            List<FrontAdvert> list = biz.cacheReturnFrontAdvert();
            frontAdverts = (List<FrontAdvert>) CacheUtil.getCache().get(Constants.CacheServiceType.FRONT_ADVERT + exchId + ":" + languageType + ":" + clientType);
        }
        return frontAdverts == null ? new ArrayList<>() : frontAdverts;
    }

    /**
     * 获取公告
     *
     * @param exchId       交易所id
     * @param languageType 语言 zh en 等
     * @param clientType   客户端类型 APP or pc
     * @param biz
     * @return
     */
    public static List<FrontNotice> getFrontNotice(Long exchId, String languageType, Integer clientType, CacheBiz biz) {
        languageType = getLanguageApp(languageType);
        List<FrontNotice> frontNoticeList;
        frontNoticeList = (List<FrontNotice>) CacheUtil.getCache().get(Constants.CacheServiceType.FRONT_NOTICE + exchId + ":" + languageType + ":" + clientType);
        if (StringUtil.listIsBlank(frontNoticeList)) {
            List<FrontNotice> list = biz.cacheReturnFrontNotice();
            frontNoticeList = (List<FrontNotice>) CacheUtil.getCache().get(Constants.CacheServiceType.FRONT_NOTICE + exchId + ":" + languageType + ":" + clientType);

        }
        return frontNoticeList == null ? new ArrayList<>() : frontNoticeList;
    }

    /**
     * 获取版本控制
     * 根据版本渠道  android or ios
     *
     * @param biz
     * @param iosOrAndroid
     * @return
     */
    public static BaseVersion getBaseVersion(CacheBiz biz, String iosOrAndroid, Long exchId) {
        BaseVersion version = null;
        version = (BaseVersion) CacheUtil.getCache().get(Constants.CacheServiceType.BASE_VERSION + iosOrAndroid + ":" + exchId);
        if (version == null) {
            biz.cacheReturnBaseVersion();
            version = (BaseVersion) CacheUtil.getCache().get(Constants.CacheServiceType.BASE_VERSION + iosOrAndroid + ":" + exchId);
        }
        return version;
    }


    /**
     * 获取计价方式
     */

    public static List<ValuationModeVo> getValuationManner(CacheBiz biz, Long exchId, String languageType) {
        languageType = getLanguageApp(languageType);
        List<ValuationModeVo> list = null;
        list = (List<ValuationModeVo>) CacheUtil.getCache().get(Constants.CacheServiceType.VALUATION_MODE + exchId + ":" + languageType);
        if (StringUtil.listIsBlank(list)) {
            biz.cacheValuationModeVo();
            list = (List<ValuationModeVo>) CacheUtil.getCache().get(Constants.CacheServiceType.DICT_DATA + Constants.DirtTypeConstant.LANGUAGE + ":" + languageType);
            if (StringUtil.listIsBlank(list)) {
                WhiteExchInfo info = (WhiteExchInfo) CacheUtil.getCache().get(Constants.CacheServiceType.WHITE_EXCH_INFO + exchId);
                //根据交易所配置语言获取缓存
                list = (List<ValuationModeVo>) CacheUtil.getCache().get(Constants.CacheServiceType.VALUATION_MODE + exchId + ":" + info.getLanguage());
            }
        }
        return list == null ? new ArrayList<>() : list;
    }


    public static List<ValuationModeVo> getValuation(CacheBiz biz, Long exchId) {

        List<ValuationModeVo> list = null;
        list = (List<ValuationModeVo>) CacheUtil.getCache().get(Constants.CacheServiceType.VALUATION + exchId);
        if (StringUtil.listIsBlank(list)) {
            biz.cacheValuationModeVo();
            list = (List<ValuationModeVo>) CacheUtil.getCache().get(Constants.CacheServiceType.VALUATION + exchId);
        }
        return list == null ? new ArrayList<>() : list;
    }

    /**
     * 获取代币描述信息(带链类型)
     *
     * @param biz
     * @param symbol   代币
     * @param exchId   交易所
     * @param language 语言
     * @return
     */
    public static CfgSymbolDescription getCfgSymbolDescription(CacheBiz biz, String symbol, Long exchId, String language, String protocolType) {
        language = getLanguageApp(language);
        String symbolType = symbol;
        if (!StringUtils.isEmpty(protocolType)) {
            symbolType = symbolType + protocolType;
        }
        CfgSymbolDescription desp = null;
        desp = (CfgSymbolDescription) CacheUtil.getCache().get(Constants.CacheServiceType.CFG_SYMBOL_DESCRIPTION + symbolType + ":" + exchId + ":" + language);
        if (desp == null) {
            desp = (CfgSymbolDescription) CacheUtil.getCache().get(Constants.CacheServiceType.SYMBOL_DESCRIPTION + symbol + ":" + exchId + ":" + language);
        }
        if (desp == null) {
            biz.cacheReturnSymbolDescription();
            desp = (CfgSymbolDescription) CacheUtil.getCache().get(Constants.CacheServiceType.SYMBOL_DESCRIPTION + symbol + ":" + exchId + ":" + language);
        }
        return desp;
    }


    private static String getLanguageApp(String language) {

        Map<String, Object> map = (Map<String, Object>) CacheUtil.getCache().get(Constants.CacheServiceType.DICT_DATA_MAP + Constants.DirtTypeConstant.LANGUAGE);
        if (!StringUtil.mapIsBlank(map)) {
            for (Map.Entry<String, Object> m : map.entrySet()) {
                if (m.getValue().equals(language)) {
                    return language;
                }
            }
        }
        return "en";
    }

    /**
     * 获取币币交易手续费
     */
    public static TransferExch getTransferExch(String srcSymbol, String dstSymbol, Long exchId, CacheBiz biz) {
        TransferExch exch = null;
        exch = (TransferExch) CacheUtil.getCache().get(Constants.CacheServiceType.TRANSFER_EXCH + exchId + ":" + srcSymbol + ":" + dstSymbol);
        if (exch == null) {
            biz.cacheReturnTransferExch();
            exch = (TransferExch) CacheUtil.getCache().get(Constants.CacheServiceType.TRANSFER_EXCH + exchId + ":" + srcSymbol + ":" + dstSymbol);
        }
        return exch;
    }

    /**
     * 获取币币交易最大 最小交易额 List
     *
     * @param exchId 如果list 为空 直接使用默认配置
     */

    public static List<TransferExch> getTransferExchList(Long exchId, CacheBiz biz) {
        List<TransferExch> list = null;
        list = (List<TransferExch>) CacheUtil.getCache().get(Constants.CacheServiceType.TRANSFER_EXCH_LIST + exchId);
        if (StringUtil.listIsBlank(list)) {
            biz.cacheReturnTransferExch();
            list = (List<TransferExch>) CacheUtil.getCache().get(Constants.CacheServiceType.TRANSFER_EXCH_LIST + exchId);
        }
        return list == null ? new ArrayList<>() : list;
    }

    /**
     * 获取手续费分成配置
     */
    public static List<CmsConfig> getCmsConfigList(Long exchId, CacheBiz biz) {
        List<CmsConfig> list = null;
        list = (List<CmsConfig>) CacheUtil.getCache().get(Constants.CacheServiceType.CMS_CONFIG_BIZ + exchId);
        if (StringUtil.listIsBlank(list)) {
            biz.cacheReturnCmsConfig();
            list = (List<CmsConfig>) CacheUtil.getCache().get(Constants.CacheServiceType.CMS_CONFIG_BIZ + exchId);
            if (list == null || list.size() == 0) {
                logger.error("获取分成配置失败,交易所id:" + exchId);
            }
        }

        return list == null ? new ArrayList<>() : list;
    }

    /**
     * 链类型与充值地址关联关系
     */

    public static Map<String, DictData> getRechargeProtocol(CacheBiz biz) {
        Map<String, DictData> dataMap = InstanceUtil.newHashMap();
        List<DictData> list = null;
        list = (List<DictData>) CacheUtil.getCache().get(Constants.CacheServiceType.DICT_DATA + Constants.DirtTypeConstant.RECHARGE_PROTOCOL);
        if (StringUtil.listIsBlank(list)) {
            biz.cacheReturnDictData();
            list = (List<DictData>) CacheUtil.getCache().get(Constants.CacheServiceType.DICT_DATA + Constants.DirtTypeConstant.RECHARGE_PROTOCOL);
        }
        if (!StringUtil.listIsBlank(list)) {
            dataMap = list.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData -> DictData));
        }
        return dataMap;
    }


    //获取参数配置信息
    public static String getCasinoParam(String key, CacheBiz biz,Long exchId) {
        CasinoParam param = (CasinoParam) CacheUtil.getCache().get(Constants.CACHE_NAMESPACE + key+exchId);
        if (param == null) {
            biz.cacheReturnCasinoParam();
            param = (CasinoParam) CacheUtil.getCache().get(Constants.CACHE_NAMESPACE + key+exchId);
        }
        return param ==null ? "":param.getCasinoValue();
    }

    //获取推荐返利信息
    public static List<CasinoRebateConfig> getCasinoRebateConfig(CacheBiz biz,Long exchId){
        List<CasinoRebateConfig> configList= (List<CasinoRebateConfig>) CacheUtil.getCache().get(Constants.CacheServiceType.CASINO_REBATE_CONFIG + exchId);
        if (StringUtil.listIsBlank(configList)){
            biz.cacheReturnCasinoRebateConfig();
            configList= (List<CasinoRebateConfig>) CacheUtil.getCache().get(Constants.CacheServiceType.CASINO_REBATE_CONFIG + exchId);
        }
        return StringUtil.listIsBlank(configList) ?new ArrayList<>():configList;
    }
}
