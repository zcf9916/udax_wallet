package com.github.wxiaoqi.security.admin.rest.front;

import com.github.wxiaoqi.security.admin.biz.front.CommissionLogBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.front.CommissionLog;
import com.github.wxiaoqi.security.common.enums.EmailTemplateType;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import com.github.wxiaoqi.security.common.util.Query;
import com.github.wxiaoqi.security.common.util.SendUtil;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("commissionLog")
public class CommissionLogController extends BaseController<CommissionLogBiz, CommissionLog> {

    /**
     * 后台生成 用户转账
     * 分成数据
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/{orderId}/generateTransferOrder",method = RequestMethod.PUT)
    public ObjectRestResponse<CommissionLog> generateTransferOrder(@PathVariable String orderId){
        baseBiz.generateTransferOrder(orderId);
        return new ObjectRestResponse<CommissionLog>();
    }

    /**
     * 后台生成 币币交易
     * 分成数据
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/{orderId}/generateFrontTransferDetail",method = RequestMethod.PUT)
    public ObjectRestResponse<CommissionLog> generateFrontTransferDetail(@PathVariable String orderId){
        baseBiz.generateFrontTransferDetail(orderId);
        return new ObjectRestResponse<CommissionLog>();
    }


    @RequestMapping(value = "/logPage",method = RequestMethod.GET)
    public TableResultResponse<CommissionLog> list(@RequestParam Map<String, Object> params){
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)){
            params.put("exchangeId",BaseContextHandler.getExId());
        }
        LocalDate beginDateTime = null;
        LocalDate endDateTime = null;
        if (StringUtils.isNotBlank((String) params.get("beginDate"))) {
            beginDateTime = LocalDate.parse((String) params.get("beginDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        //结束时间取: 23:59:59
        if (StringUtils.isNotBlank((String) params.get("endDate"))) {
            endDateTime = LocalDate.parse((String) params.get("endDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            endDateTime = endDateTime.plusDays(1);
            params.put("endDate", endDateTime.toString());
        }
        if (beginDateTime != null && endDateTime != null) {
            if (beginDateTime.isAfter(endDateTime)) {
                throw new UserInvalidException(Resources.getMessage("BASE_BEGINDATE_ENDDATE"));
            }
        }
       return super.queryListByCustomPage(params);
    }


    @RequestMapping(value = "/{id}/settlement",method = RequestMethod.PUT)
    public ObjectRestResponse<CommissionLog> settlement(@PathVariable Long id) throws Exception {
        baseBiz.settlement(id);
        return new ObjectRestResponse<CommissionLog>();
    }


    @RequestMapping(value = "/batchSettlement",method = RequestMethod.PUT)
    public ObjectRestResponse<Object> batchSettlement(@RequestParam Map<String, Object> param) throws Exception {
        LocalDate beginDateTime = null;
        LocalDate endDateTime = null;
        if (StringUtils.isNotBlank((String) param.get("beginDate"))) {
            beginDateTime = LocalDate.parse((String) param.get("beginDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        //结束时间取: 23:59:59
        if (StringUtils.isNotBlank((String) param.get("endDate"))) {
            endDateTime = LocalDate.parse((String) param.get("endDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            endDateTime = endDateTime.plusDays(1);
        }
        if (beginDateTime != null && endDateTime != null) {
            if (beginDateTime.isAfter(endDateTime)) {
                throw new UserInvalidException(Resources.getMessage("BASE_BEGINDATE_ENDDATE"));
            }
        }
        Integer count= baseBiz.batchSettlement(beginDateTime,endDateTime);
        return new ObjectRestResponse<>().rel(true).status(200).msg(Resources.getMessage("BATCH_SETTLEMENT") +count);
    }


}
