package com.github.wxiaoqi.security.admin.rest.base;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.admin.biz.base.DictDataBiz;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.DictData;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.Query;
import com.github.wxiaoqi.security.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private DictDataBiz dictDataBiz;

    /**
     * 获取语言
     *
     * @return
     */
    @RequestMapping(value = "/language", method = RequestMethod.GET)
    public List<DictData> getLanguage() {
        List<DictData> dictData = null;
        dictData = (List<DictData>) CacheUtil.getCache().get(Constants.CacheServiceType.DICT_DATA +Constants.DirtTypeConstant.LANGUAGE);
        if (!StringUtil.listIsNotBlank(dictData)) {
            dictData = dictDataBiz.selectListData(Constants.DirtTypeConstant.LANGUAGE, "");
        }
        return dictData;
    }

    /**
     * 获取手续费类型
     */
    @RequestMapping(value = "/chargeTypes", method = RequestMethod.GET)
    public List<DictData> getChargeType(HttpServletRequest request) {
        String language = request.getHeader("locale");
        List<DictData> dictData = null;
        dictData = (List<DictData>) CacheUtil.getCache().get(Constants.CacheServiceType.DICT_DATA +Constants.DirtTypeConstant.CHARGE_VALUE +":" + language);
        if (!StringUtil.listIsNotBlank(dictData)) {
            dictData = dictDataBiz.selectListData(Constants.DirtTypeConstant.CHARGE_VALUE, language);
        }
        return dictData;
    }

    /**
     * 获取用户认证不通过原因
     */
    @RequestMapping(value = "/userValid", method = RequestMethod.GET)
    public List<DictData> getUserValid(HttpServletRequest request) {
        String language = request.getHeader("locale");
        List<DictData> dictData = null;
        dictData = (List<DictData>) CacheUtil.getCache().get(Constants.CacheServiceType.DICT_DATA + Constants.DirtTypeConstant.USER_VALID +":" + language);
        if (!StringUtil.listIsNotBlank(dictData)) {
            dictData = dictDataBiz.selectListData(Constants.DirtTypeConstant.USER_VALID, language);
        }
        return dictData;
    }

    /**
     * 获取商家不通过原因
     */
    @RequestMapping(value = "/merchantValid", method = RequestMethod.GET)
    public List<DictData> getMerchantValid(HttpServletRequest request) {
        String language = request.getHeader("locale");
        List<DictData> dictData = null;
        dictData = (List<DictData>) CacheUtil.getCache().get(Constants.CacheServiceType.DICT_DATA +Constants.DirtTypeConstant.MERCHANT_VALID  +":"+ language);
        if (!StringUtil.listIsNotBlank(dictData)) {
            dictData = dictDataBiz.selectListData(Constants.DirtTypeConstant.MERCHANT_VALID, language);
        }
        return dictData;
    }

    /**
     * 获取提现审核不通过原因
     */
    @RequestMapping(value = "/withdrawVerify", method = RequestMethod.GET)
    public List<DictData> getWithdrawVerify(HttpServletRequest request) {
        String language = request.getHeader("locale");
        List<DictData> dictData = null;
        dictData = (List<DictData>) CacheUtil.getCache().get(Constants.CacheServiceType.DICT_DATA +Constants.DirtTypeConstant.WITHDRAW_VERIFY +":"+ language);
        if (!StringUtil.listIsNotBlank(dictData)) {
            dictData = dictDataBiz.selectListData(Constants.DirtTypeConstant.WITHDRAW_VERIFY, language);
        }
        return dictData;
    }

    /**
     * 获取IFR方案周期
     */
    @RequestMapping(value = "/planCycle", method = RequestMethod.GET)
    public List<DictData> getPlanCycle(HttpServletRequest request) {
        String language = request.getHeader("locale");
        List<DictData> dictData = null;
        dictData = (List<DictData>) CacheUtil.getCache().get(Constants.CacheServiceType.DICT_DATA +Constants.DirtTypeConstant.IFR_PLAN_CYCLE +":"+ language);
        if (dictData== null) {
            dictData = dictDataBiz.selectListData(Constants.DirtTypeConstant.WITHDRAW_VERIFY, language);
        }
        return dictData;
    }

    /**
     * 获取IFR 汇率币种
     */
    @RequestMapping(value = "/ifrSymbolList", method = RequestMethod.GET)
    public List<DictData> getIfrSymbolList(HttpServletRequest request) {
        List<DictData> dictData = null;
        dictData = (List<DictData>) CacheUtil.getCache().get(Constants.CacheServiceType.DICT_DATA +Constants.DirtTypeConstant.IFR_FB_LIST);
        if (dictData== null) {
            dictData = dictDataBiz.selectListData(Constants.DirtTypeConstant.IFR_FB_LIST, null);
        }
        return dictData;
    }

    /**
     *  获取计价方式并分页
     */
    @RequestMapping("/valuationMode")
    public TableResultResponse<DictData> getValuationMode(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        params.put("dictType",Constants.DirtTypeConstant.FB_SYMBOL);
        List<DictData> dictData =dictDataBiz.queryListByCustomPage(params);
        return new TableResultResponse<DictData>(result.getTotal(), dictData);
    }


    /**
     * 获取UD社区等级配置
     */
    @RequestMapping(value = "/udLevel", method = RequestMethod.GET)
    public List<DictData> getUdLevel(HttpServletRequest request) {
        List<DictData> dictData = null;
        dictData = (List<DictData>) CacheUtil.getCache().get(Constants.CacheServiceType.DICT_DATA +Constants.DirtTypeConstant.UD_LEVEL);
        if (dictData== null) {
            dictData = dictDataBiz.selectListData(Constants.DirtTypeConstant.UD_LEVEL, null);
        }
        return dictData;
    }





    /**
     * 获取用户功能冻结列表
     */
    @RequestMapping(value = "/userFeaturesFreeze", method = RequestMethod.GET)
    public List<DictData> getUserFeaturesFreeze(HttpServletRequest request) {
        String language = request.getHeader("locale");
        List<DictData> dictData = null;
        dictData = (List<DictData>) CacheUtil.getCache().get(Constants.CacheServiceType.DICT_DATA +Constants.DirtTypeConstant.USER_FEATURES_FREEZE+":"+language);
        if (dictData== null) {
            dictData = dictDataBiz.selectListData(Constants.DirtTypeConstant.USER_FEATURES_FREEZE, language);
        }
        return dictData;
    }


    /**
     * 获取ETH 公链信息
     */
    @RequestMapping(value = "/ETHTokenList", method = RequestMethod.GET)
    public List<DictData> getETHTokenList(HttpServletRequest request) {
        List<DictData> dictData = null;
        dictData = (List<DictData>) CacheUtil.getCache().get(Constants.CacheServiceType.DICT_DATA +Constants.DirtTypeConstant.SYMBOL_LIST);
        if (dictData== null) {
            dictData = dictDataBiz.selectListData(Constants.DirtTypeConstant.SYMBOL_LIST,null);
        }
        return dictData;
    }


    /**
     * 获取多个链名
     */
    @RequestMapping(value = "/protocolTypeList", method = RequestMethod.GET)
    public List<DictData> protocolTypeList(HttpServletRequest request) {
        List<DictData> dictData = null;
        dictData = (List<DictData>) CacheUtil.getCache().get(Constants.CacheServiceType.DICT_DATA +Constants.DirtTypeConstant.PROTOCOL_TYPE_LIST);
        if (dictData== null) {
            dictData = dictDataBiz.selectListData(Constants.DirtTypeConstant.PROTOCOL_TYPE_LIST,null);
        }
        return dictData;
    }


    /**
     * 手续费分成等级
     */
    @RequestMapping(value = "/commissionLevel", method = RequestMethod.GET)
    public List<DictData> getCommissionLevel(HttpServletRequest request) {
        String language = request.getHeader("locale");
        List<DictData> dictData = null;
        dictData = (List<DictData>) CacheUtil.getCache().get(Constants.CacheServiceType.DICT_DATA +Constants.DirtTypeConstant.COMMISSION_LEVEL+":"+language);
        if (dictData== null) {
            dictData = dictDataBiz.selectListData(Constants.DirtTypeConstant.COMMISSION_LEVEL,language);
        }
        return dictData;
    }
}
