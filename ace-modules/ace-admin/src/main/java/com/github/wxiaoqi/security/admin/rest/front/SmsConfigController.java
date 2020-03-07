package com.github.wxiaoqi.security.admin.rest.front;

import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.security.admin.util.GetCommonDataUtil;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.util.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.wxiaoqi.security.admin.biz.front.SmsConfigBiz;
import com.github.wxiaoqi.security.common.entity.admin.BaseSmsConfig;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("smsConfig")
public class SmsConfigController extends BaseController<SmsConfigBiz, BaseSmsConfig> {

    @RequestMapping(value = "/pageQuery",method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getList(@RequestParam Map<String, Object> params){
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)){
            params.put("whiteExchId",BaseContextHandler.getExId());
        }
        params.put("smsPlatUrl:like",params.get("smsPlatUrl"));//模糊搜索
        params.remove("smsPlatUrl");
        //查询列表数据
        Query query = new Query(params);
        JSONObject resultData = new JSONObject();
        resultData.put("whiteExchId",BaseContextHandler.getExId());//白标交易所的Id
        resultData.put("whiteExchList", GetCommonDataUtil.getWhiteExchList());//查询所有交易所
        resultData.put("data",pageQuery(query));
        return resultData;
    }


}
