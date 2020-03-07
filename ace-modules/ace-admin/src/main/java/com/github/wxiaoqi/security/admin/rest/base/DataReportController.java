package com.github.wxiaoqi.security.admin.rest.base;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.admin.biz.base.DataReportBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.UserTransactionModel;
import com.github.wxiaoqi.security.common.entity.front.DcAssetAccount;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 后台报表统计查询
 */
@RestController
@RequestMapping("/dataReport")
public class DataReportController {

    @Autowired
    private DataReportBiz dataReportBiz;

    /**
     * 提币汇总
     *
     * @param param
     */
    @RequestMapping(value = "/withdraw", method = RequestMethod.GET)
    public TableResultResponse<UserTransactionModel> pageFrontWithdraw(@RequestParam Map<String, Object> param) {
        validDate(param);
        Query query = new Query(param);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<UserTransactionModel> list = dataReportBiz.pageFrontWithdraw(param);
        return new TableResultResponse<UserTransactionModel>(result.getTotal(), list);
    }

    /**
     * 入金汇总
     *
     * @param param
     */
    @RequestMapping(value = "/recharge", method = RequestMethod.GET)
    public TableResultResponse<UserTransactionModel> pageFrontRecharge(@RequestParam Map<String, Object> param) {
        validDate(param);
        Query query = new Query(param);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        //只查入金成功的
        param.put("status", EnableType.DISABLE.value());
        List<UserTransactionModel> list = dataReportBiz.pageFrontRecharge(param);
        return new TableResultResponse<UserTransactionModel>(result.getTotal(), list);
    }


    /**
     *资产汇总
     *
     * @param param
     */
    @RequestMapping(value = "/assetAccount", method = RequestMethod.GET)
    public TableResultResponse<DcAssetAccount> pageAssetAccount(@RequestParam Map<String, Object> param) {
        validDate(param);
        Query query = new Query(param);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<DcAssetAccount> list = dataReportBiz.pageAssetAccount(param);
        return new TableResultResponse<DcAssetAccount>(result.getTotal(), list);
    }

    /**
     * 转账汇总
     *
     * @param param
     */
    @RequestMapping(value = "/transfer", method = RequestMethod.GET)
    public TableResultResponse<UserTransactionModel> pageFrontTransfer(@RequestParam Map<String, Object> param) {
        validDate(param);
        Query query = new Query(param);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<UserTransactionModel> list = dataReportBiz.pageFrontTransfer(param);
        return new TableResultResponse<UserTransactionModel>(result.getTotal(), list);
    }

    /**
     * 收支汇总
     *
     * @param param
     */
    @RequestMapping(value = "/inAndExReport", method = RequestMethod.GET)
    public TableResultResponse<UserTransactionModel> pageInAndExReport(@RequestParam Map<String, Object> param) {
        validDate(param);
        return dataReportBiz.pageInAndExReport(param);
    }


    private void validDate(@RequestParam Map<String, Object> param) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            param.put("exchangeId", BaseContextHandler.getExId());
        }
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
