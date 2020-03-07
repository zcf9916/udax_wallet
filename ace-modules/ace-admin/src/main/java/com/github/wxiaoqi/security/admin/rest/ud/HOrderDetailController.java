package com.github.wxiaoqi.security.admin.rest.ud;

import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.security.admin.biz.ud.HOrderDetailBiz;
import com.github.wxiaoqi.security.admin.util.GetCommonDataUtil;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.ud.HOrderDetail;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("udOrderDetails")
public class HOrderDetailController extends BaseController<HOrderDetailBiz, HOrderDetail> {

    @Autowired
    HOrderDetailBiz orderDetailBiz;

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

    //结算
    @RequestMapping(value = "/adminSettleProfit",method = RequestMethod.POST)
    public ObjectRestResponse<HOrderDetail> adminDoSettleProfit(@RequestParam String orderNo)  throws Exception{

        orderDetailBiz.adminSettleProfit(orderNo);

        return new ObjectRestResponse<HOrderDetail>();
    }
}
