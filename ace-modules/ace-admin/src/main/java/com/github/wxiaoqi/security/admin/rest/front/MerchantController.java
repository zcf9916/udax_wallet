package com.github.wxiaoqi.security.admin.rest.front;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.admin.biz.front.MerchantBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.merchant.Merchant;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import com.github.wxiaoqi.security.common.util.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("merchant")
public class MerchantController  extends BaseController<MerchantBiz, Merchant> {

    @RequestMapping(value = "/pageQuery", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<Merchant> getList(@RequestParam Map<String, Object> params) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT))  {
            params.put("exchangeId", BaseContextHandler.getExId());
        }
        LocalDate beginDateTime = null;
        LocalDate endDateTime = null;
        if(StringUtils.isNotBlank((String)params.get("beginDate"))){
            beginDateTime = LocalDate.parse((String)params.get("beginDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        //结束时间取: 23:59:59
        if(StringUtils.isNotBlank((String)params.get("endDate"))){
            endDateTime = LocalDate.parse((String)params.get("endDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            endDateTime=endDateTime.plusDays(1);
            params.put("endDate",endDateTime.toString());
        }
        if(beginDateTime != null && endDateTime !=null){
            if(beginDateTime.isAfter(endDateTime)){
                throw new UserInvalidException(Resources.getMessage("BASE_BEGINDATE_ENDDATE"));
            }
        }
        //查询列表数据
        Query query = new Query(params);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Merchant> list = super.queryListNoPage(params);
        return new TableResultResponse<Merchant>(result.getTotal(), list);
    }
}
