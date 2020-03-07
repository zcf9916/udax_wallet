package com.github.wxiaoqi.security.admin.rest.fund;

import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.security.admin.biz.fund.AdminFundManageInfoBiz;
import com.github.wxiaoqi.security.admin.util.GetCommonDataUtil;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.fund.FundManageInfo;
import com.github.wxiaoqi.security.common.rest.BaseController;
import com.github.wxiaoqi.security.common.util.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("adminFundManageInfo")
public class AdminFundManageInfoController extends BaseController<AdminFundManageInfoBiz, FundManageInfo> {

    @RequestMapping(value = "/pageQuery",method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getList(@RequestParam Map<String, Object> params){
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)){
            params.put("exchangeId",BaseContextHandler.getExId());
        }
        //查询列表数据
        Query query = new Query(params);
        JSONObject resultData = new JSONObject();
        resultData.put("data",pageQuery(query));
        resultData.put("whiteExchId",BaseContextHandler.getExId());//白标交易所的Id
        resultData.put("whiteExchList", GetCommonDataUtil.getWhiteExchList());//查询所有交易所
        return resultData;
    }

}
