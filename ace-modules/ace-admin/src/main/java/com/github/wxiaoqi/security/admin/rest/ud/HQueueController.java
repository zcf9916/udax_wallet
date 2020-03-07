package com.github.wxiaoqi.security.admin.rest.ud;


import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.security.admin.biz.ud.HQueueBiz;
import com.github.wxiaoqi.security.admin.util.GetCommonDataUtil;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.ud.HQueue;
import com.github.wxiaoqi.security.common.enums.ud.QueueStatus;
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
@RequestMapping("udQueue")
public class HQueueController extends BaseController<HQueueBiz, HQueue> {

    @Autowired
    HQueueBiz queueBiz;

    @RequestMapping(value = "/pageQuery",method = RequestMethod.GET)
    public JSONObject getList(@RequestParam Map<String, Object> params){
        params.put("status", QueueStatus.WAIT_MATCH.value());//默认只查询等待排队的
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)){
            params.put("exchangeId",BaseContextHandler.getExId());//根据交易所查询
        }
        JSONObject resultData = new JSONObject();
        resultData.put("data",queryListByCustomPage(params));
        resultData.put("whiteExchId",BaseContextHandler.getExId());//白标交易所的Id
        resultData.put("whiteExchList", GetCommonDataUtil.getWhiteExchList());//查询所有交易所
        return resultData;
    }


    //释放上一轮锁定资产
    @RequestMapping(value = "/adminToRelase",method = RequestMethod.POST)
    public ObjectRestResponse<HQueue> adminDoRelase(@RequestParam String orderNo)  throws Exception{

        queueBiz.adminToRelase(orderNo);

        return new ObjectRestResponse<HQueue>();
    }
}
