package com.github.wxiaoqi.security.admin.rest.front;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.admin.biz.front.HedgeDetailBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.HedgeDetail;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("hedgeDetail")
public class HedgeDetailController extends BaseController<HedgeDetailBiz, HedgeDetail> {

    @RequestMapping(value = "/pageHedgeDetail", method = RequestMethod.GET)
    public TableResultResponse<HedgeDetail> pageHedgeDetail(@RequestParam Map<String, Object> param) {
        validDate(param);
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            param.put("exchangeId", BaseContextHandler.getExId());
        }
        Page<Object> result = PageHelper.startPage(Integer.parseInt(param.get("page").toString()), Integer.parseInt(param.get("limit").toString()));
        List<HedgeDetail> list = baseBiz.queryListByCustomPage(param);
        return new TableResultResponse<HedgeDetail>(result.getTotal(), list);
    }

    private void validDate(@RequestParam Map<String, Object> param) {
        LocalDate beginDateTime = null;
        LocalDate endDateTime = null;
        if (StringUtils.isNotBlank((String) param.get("beginDate"))) {
            beginDateTime = LocalDate.parse((String) param.get("beginDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
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

    @RequestMapping(value = "/{id}/adminStatus", method = RequestMethod.PUT)
    @ResponseBody
    public ObjectRestResponse<HedgeDetail> updateByDealedStatus(@PathVariable Long id) {
        baseBiz.updateByDealedStatus(id);
        return new ObjectRestResponse<HedgeDetail>();
    }
}
