package com.github.wxiaoqi.security.admin.rest.ifr;

import com.github.wxiaoqi.security.admin.biz.ifr.IfrExchangeRateBiz;
import com.github.wxiaoqi.security.admin.biz.ifr.IfrPayOrderAdminBiz;
import com.github.wxiaoqi.security.admin.biz.ifr.IfrPlanAdminBiz;
import com.github.wxiaoqi.security.common.entity.ifr.IfrPayOrder;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ifrPayOrder")
public class IfrPayOrderAdminController extends BaseController<IfrPayOrderAdminBiz, IfrPayOrder> {

    @Autowired
    private IfrPlanAdminBiz ifrPlanAdminBiz;

    @Autowired
    private IfrExchangeRateBiz ifrExchangeRateBiz;

    @RequestMapping(value = "/pageAll", method = RequestMethod.GET)
    public Map<String,Object> listAll(@RequestParam Map<String, Object> params) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("data",super.queryListByCustomPage(params));
        map.put("ifrPlan",ifrPlanAdminBiz.selectListAll());
        map.put("ifrExchangeRate",ifrExchangeRateBiz.selectListAll());
        return map;
    }
}

