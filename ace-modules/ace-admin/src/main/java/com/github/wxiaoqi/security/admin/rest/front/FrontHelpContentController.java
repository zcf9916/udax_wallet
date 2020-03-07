package com.github.wxiaoqi.security.admin.rest.front;

import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.security.admin.biz.front.FrontHelpContentBiz;
import com.github.wxiaoqi.security.admin.biz.front.FrontHelpTypeBiz;
import com.github.wxiaoqi.security.admin.util.GetCommonDataUtil;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.FrontHelpContent;
import com.github.wxiaoqi.security.common.entity.admin.FrontHelpType;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("helpContent")
public class FrontHelpContentController extends BaseController<FrontHelpContentBiz, FrontHelpContent> {

    @Autowired
    private FrontHelpTypeBiz frontHelpTypeBiz;

    @RequestMapping(value = "/pageQuery", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getList(@RequestParam Map<String, Object> params) {
        FrontHelpType helpType = new FrontHelpType();
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            params.put("exchangeId", BaseContextHandler.getExId());
            helpType.setExchangeId(BaseContextHandler.getExId());
        }
        helpType.setEnable(EnableType.ENABLE.value());
        JSONObject resultData = new JSONObject();
        resultData.put("whiteExchId", BaseContextHandler.getExId());//白标交易所的Id
        resultData.put("whiteExchList", GetCommonDataUtil.getWhiteExchList());//查询所有交易所
        resultData.put("helpTypeList", frontHelpTypeBiz.selectList(helpType));//查询所有帮助类型
        resultData.put("data", queryListByCustomPage(params));
        return resultData;
    }

}
