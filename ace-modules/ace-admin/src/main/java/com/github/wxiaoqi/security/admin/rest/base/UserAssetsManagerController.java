package com.github.wxiaoqi.security.admin.rest.base;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.admin.biz.front.UserAssetsManagerBiz;
import com.github.wxiaoqi.security.admin.vo.UserValidVo;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.entity.front.*;
import com.github.wxiaoqi.security.common.entity.merchant.MchRefundDetail;
import com.github.wxiaoqi.security.common.entity.merchant.MchTradeDetail;
import com.github.wxiaoqi.security.common.enums.TransferOrderStatus;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/userAssets")
public class UserAssetsManagerController {

    @Autowired
    private UserAssetsManagerBiz userAssetsManagerBiz;

    @RequestMapping(value = "/pageFrontRecharge", method = RequestMethod.GET)
    public TableResultResponse<FrontRecharge> list(@RequestParam Map<String, Object> param) {
        Page<Object> result= validDate(param);
        List<FrontRecharge> list = userAssetsManagerBiz.getFrontRechargeList(param);
        return new TableResultResponse<FrontRecharge>(result.getTotal(), list);
    }


    @RequestMapping(value = "/pageFrontWithdraw", method = RequestMethod.GET)
    public TableResultResponse<FrontWithdraw> pageFrontWithdraw(@RequestParam Map<String, Object> param) {
        Page<Object> result= validDate(param);
        List<FrontWithdraw> list = userAssetsManagerBiz.getFrontWithdraw(param);
        return new TableResultResponse<FrontWithdraw>(result.getTotal(), list);
    }

    @RequestMapping(value = "/pageTransferOrder", method = RequestMethod.GET)
    public TableResultResponse<TransferOrder> pageTransferOrder(@RequestParam Map<String, Object> param) {
        Page<Object> result= validDate(param);
        param.put("status",TransferOrderStatus.PAYED.value());//只查转账成功的
        List<TransferOrder> list = userAssetsManagerBiz.getTransferOrder(param);
        return new TableResultResponse<TransferOrder>(result.getTotal(), list);
    }


    @RequestMapping(value = "/{id}/pageFrontWithdraw", method = RequestMethod.PUT)
    @ResponseBody
    public ObjectRestResponse<UserValidVo> updateUserInfo(@RequestBody UserValidVo entity) throws Exception {
        userAssetsManagerBiz.updateSelectiveById(entity);
        return new ObjectRestResponse<UserValidVo>();
    }


    @RequestMapping(value = "/pageFrontAccount", method = RequestMethod.GET)
    public TableResultResponse<DcAssetAccount> pageFrontAccount(@RequestParam Map<String, Object> param) {
        Page<Object> result= validDate(param);
        List<DcAssetAccount> list = userAssetsManagerBiz.getFrontAccount(param);
        return new TableResultResponse<DcAssetAccount>(result.getTotal(), list);
    }

    @RequestMapping(value = "/pageFrontTransferDetail", method = RequestMethod.GET)
    public TableResultResponse<FrontTransferDetail> pageFrontTransferDetail(@RequestParam Map<String, Object> param) {
        param.put("status", TransferOrderStatus.PAYED.value());
        Page<Object> result= validDate(param);
        List<FrontTransferDetail> list = userAssetsManagerBiz.getFrontTransferDetail(param);
        return new TableResultResponse<FrontTransferDetail>(result.getTotal(), list);
    }


    @RequestMapping(value = "/pageMchTradeDetail", method = RequestMethod.GET)
    public TableResultResponse<MchTradeDetail> pageMchTradeDetail(@RequestParam Map<String, Object> param) {
        Page<Object> result= validDate(param);
        List<MchTradeDetail> list = userAssetsManagerBiz.getMchTradeDetail(param);
        return new TableResultResponse<MchTradeDetail>(result.getTotal(), list);
    }

    @RequestMapping(value = "/pageMchRefundDetail", method = RequestMethod.GET)
    public TableResultResponse<MchRefundDetail> pageMchRefundDetail(@RequestParam Map<String, Object> param) {
        Page<Object> result= validDate(param);
        List<MchRefundDetail> list = userAssetsManagerBiz.getMchRefundDetail(param);
        return new TableResultResponse<MchRefundDetail>(result.getTotal(), list);
    }

    private  Page<Object> validDate(@RequestParam Map<String, Object> param) {
        Query query = new Query(param);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
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
        return result;
    }
}
