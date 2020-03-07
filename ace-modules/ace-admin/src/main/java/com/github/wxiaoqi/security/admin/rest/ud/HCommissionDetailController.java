package com.github.wxiaoqi.security.admin.rest.ud;


import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.security.admin.biz.ud.HCommissionDetailBiz;
import com.github.wxiaoqi.security.admin.util.GetCommonDataUtil;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.ud.HCommissionDetail;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("udCommissionDetail")
public class HCommissionDetailController extends BaseController<HCommissionDetailBiz, HCommissionDetail> {

    @Autowired
    HCommissionDetailBiz  commissionDetailBiz;

    @RequestMapping(value = "/pageQuery",method = RequestMethod.GET)
    public JSONObject getList(@RequestParam Map<String, Object> params){

        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)){
            params.put("exchangeId",BaseContextHandler.getExId());
        }
        JSONObject resultData = new JSONObject();
        resultData.put("data",queryListByCustomPage(params));
        resultData.put("whiteExchId",BaseContextHandler.getExId());//白标交易所的Id
        resultData.put("whiteExchList", GetCommonDataUtil.getWhiteExchList());//查询所有交易所
        return resultData;
    }


    //分成报表统计
    @RequestMapping(value = "/pageReportQuery",method = RequestMethod.GET)
    public JSONObject getReportList(@RequestParam Map<String, Object> params){
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)){
            params.put("exchangeId",BaseContextHandler.getExId());
        }
        JSONObject resultData = new JSONObject();
        resultData.put("data",commissionDetailBiz.queryCommissionReport(params));
        resultData.put("whiteExchId",BaseContextHandler.getExId());//白标交易所的Id
        resultData.put("whiteExchList", GetCommonDataUtil.getWhiteExchList());//查询所有交易所
        return resultData;
    }
}
