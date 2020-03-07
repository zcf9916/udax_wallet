package com.github.wxiaoqi.security.admin.rest.ud;

import com.github.wxiaoqi.security.admin.biz.ud.LockDetailBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.ud.HUnLockDetail;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("lockDetail")
public class LockDetailController extends BaseController<LockDetailBiz, HUnLockDetail> {

    @Override
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public TableResultResponse<HUnLockDetail> list(@RequestParam Map<String, Object> params) {
        validDate(params);
        return queryListByCustomPage(params);
    }

    private void validDate(@RequestParam Map<String, Object> param) {
        LocalDate beginDateTime = null;
        LocalDate endDateTime = null;
        if (StringUtils.isNotBlank((String) param.get("beginDate"))) {
            beginDateTime = LocalDate.parse((String) param.get("beginDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            param.put("exchId", BaseContextHandler.getExId());
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
