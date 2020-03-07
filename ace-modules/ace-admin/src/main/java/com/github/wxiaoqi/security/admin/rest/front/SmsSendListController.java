package com.github.wxiaoqi.security.admin.rest.front;

import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.security.admin.biz.front.SmsSendListBiz;
import com.github.wxiaoqi.security.common.entity.common.BaseSms;
import com.github.wxiaoqi.security.common.rest.BaseController;
import com.github.wxiaoqi.security.common.util.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("getSmsList")
public class SmsSendListController extends BaseController<SmsSendListBiz, BaseSms> {

    @RequestMapping(value = "/pageQuery", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getList(@RequestParam Map<String, Object> params) {

        params.put("phone:like", params.get("phone"));//模糊搜索
        params.remove("phone");
        params.put("orderByInfo", "id desc");
        //查询列表数据
        Query query = new Query(params);
        JSONObject resultData = new JSONObject();
        resultData.put("data", pageQuery(query));
        return resultData;
    }

}
