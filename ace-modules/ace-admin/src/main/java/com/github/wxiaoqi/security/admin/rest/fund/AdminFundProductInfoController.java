package com.github.wxiaoqi.security.admin.rest.fund;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.admin.biz.fund.AdminFundManageInfoBiz;
import com.github.wxiaoqi.security.admin.biz.fund.AdminFundProductInfoBiz;
import com.github.wxiaoqi.security.admin.biz.fund.FundStrategyBiz;
import com.github.wxiaoqi.security.admin.util.GetCommonDataUtil;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.fund.FundManageInfo;
import com.github.wxiaoqi.security.common.entity.fund.FundProductInfo;
import com.github.wxiaoqi.security.common.entity.fund.FundPurchaseInfo;
import com.github.wxiaoqi.security.common.entity.fund.FundStrategy;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.enums.fund.FundStatus;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("adminFundProductInfo")
public class AdminFundProductInfoController extends BaseController<AdminFundProductInfoBiz, FundProductInfo> {

    @Autowired
    AdminFundProductInfoBiz fundProductInfoBiz;
    @Autowired
    private AdminFundManageInfoBiz manageInfoBiz;
    @Autowired
    private FundStrategyBiz fundStrategyBiz;

    @RequestMapping(value = "/pageQuery", method = RequestMethod.GET)
    public JSONObject getList(@RequestParam Map<String, Object> params) {
        FundManageInfo manageInfo = new FundManageInfo();
        FundStrategy fundStrategy = new FundStrategy();

        Long exId = BaseContextHandler.getExId();
        if (!exId.equals(AdminCommonConstant.ROOT)) {
            params.put("exchangeId", BaseContextHandler.getExId());
            manageInfo.setExchangeId(exId);
            fundStrategy.setExchangeId(exId);
        }
        //查询列表数据
        JSONObject resultData = new JSONObject();
        resultData.put("data", queryListByCustomPage(params));
        resultData.put("whiteExchId", exId);//白标交易所的Id
        resultData.put("whiteExchList", GetCommonDataUtil.getWhiteExchList());//查询所有交易所
        resultData.put("fundManageInfoList", manageInfoBiz.selectList(manageInfo));//查询跟单管理人员
        resultData.put("fundStrategyList", fundStrategyBiz.selectList(fundStrategy));//查询跟单策略
        resultData.put("symbolList", GetCommonDataUtil.getSymbolList());//查询跟单策略
        return resultData;
    }

    /*查询待结算产品*/
    @RequestMapping(value = "/queryFundSettlement", method = RequestMethod.GET)
    public JSONObject queryFundSettlement(@RequestParam Map<String, Object> params) {
        FundManageInfo manageInfo = new FundManageInfo();
        FundStrategy fundStrategy = new FundStrategy();
        params.put("enable", EnableType.ENABLE.value());
        params.put("statusArr", new Integer[]{FundStatus.SETTLEING.value(), FundStatus.SETTLED.value()});
        Long exId = BaseContextHandler.getExId();
        if (!exId.equals(AdminCommonConstant.ROOT)) {
            params.put("exchangeId", exId);
            manageInfo.setExchangeId(exId);
            fundStrategy.setExchangeId(exId);
        }
        //查询列表数据
        JSONObject resultData = new JSONObject();
        resultData.put("data", queryListByCustomPage(params));
        resultData.put("whiteExchId", exId);//白标交易所的Id
        resultData.put("whiteExchList", GetCommonDataUtil.getWhiteExchList());//查询所有交易所
        resultData.put("fundManageInfoList", manageInfoBiz.selectList(manageInfo));//查询跟单管理人员
        resultData.put("fundStrategyList", fundStrategyBiz.selectList(fundStrategy));//查询跟单策略
        resultData.put("symbolList", GetCommonDataUtil.getSymbolList());//查询跟单策略
        return resultData;
    }

    /*产品结算*/
    @RequestMapping(value = "/doFundSettlement", method = RequestMethod.PUT)
    public ObjectRestResponse doFundSettlement(@RequestParam Long id, @RequestParam Long fundId) {

        return fundProductInfoBiz.updateSettleAccount(id, fundId);
    }

    /*设置实际收益率*/
    @RequestMapping(value = "/setCurrProfilt", method = RequestMethod.PUT)
    public ObjectRestResponse setCurrProfilt(@RequestParam Long fundId, @RequestParam BigDecimal currProfilt) {

        return fundProductInfoBiz.updateCurrProfilt(fundId, currProfilt);
    }

    /**
     * 跟单明细
     */
    @RequestMapping(value = "/pagePurchase", method = RequestMethod.GET)
    public TableResultResponse<FundPurchaseInfo> pagePurchase(@RequestParam Map<String, Object> param) {
        validDate(param);
        Page<Object> result = PageHelper.startPage(Integer.parseInt(param.get("page").toString()), Integer.parseInt(param.get("limit").toString()));
        List<FundPurchaseInfo> list = fundProductInfoBiz.getPurchaseList(param);
        return new TableResultResponse<FundPurchaseInfo>(result.getTotal(), list);
    }

    private void validDate(@RequestParam Map<String, Object> param) {
        LocalDate beginDateTime = null;
        LocalDate endDateTime = null;
        if (StringUtils.isNotBlank((String) param.get("beginDate"))) {
            beginDateTime = LocalDate.parse((String) param.get("beginDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        //结束时间取: 23:59:59
        if (StringUtils.isNotBlank((String) param.get("endDate"))) {
            endDateTime = LocalDate.parse((String) param.get("endDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            endDateTime = endDateTime.plusDays(1);
            param.put("endDate", endDateTime.toString());
        }
        if (beginDateTime != null && endDateTime != null) {
            if (beginDateTime.isAfter(endDateTime)) {
                throw new UserInvalidException(Resources.getMessage("BASE_BEGINDATE_ENDDATE"));
            }
        }
    }
}
