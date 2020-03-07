package com.github.wxiaoqi.security.admin.rest.front;

import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.security.admin.util.GetCommonDataUtil;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.wxiaoqi.security.admin.biz.base.EmailAuditorBiz;
import com.github.wxiaoqi.security.common.entity.admin.BaseEmailAuditor;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("emailAuditor")
public class EmailAuditorController extends BaseController<EmailAuditorBiz, BaseEmailAuditor> {

    @RequestMapping(value = "/pageQuery", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getList(@RequestParam Map<String, Object> params) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            params.put("whiteExchId", BaseContextHandler.getExId());
        }
        //查询列表数据
        JSONObject resultData = new JSONObject();
        resultData.put("data",queryListByCustomPage(params));
        resultData.put("whiteExchId", BaseContextHandler.getExId());//白标交易所的Id
        resultData.put("whiteExchList", GetCommonDataUtil.getWhiteExchList());//查询所有交易所
        return resultData;
    }

}
