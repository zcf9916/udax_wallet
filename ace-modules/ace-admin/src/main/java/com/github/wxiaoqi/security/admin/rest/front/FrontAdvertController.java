package com.github.wxiaoqi.security.admin.rest.front;


import com.github.wxiaoqi.security.admin.biz.front.FrontAdvertBiz;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.FrontAdvert;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-07-01 20:32
 */
@Controller
@RequestMapping("advertManager")
public class FrontAdvertController extends BaseController<FrontAdvertBiz, FrontAdvert> {


    @RequestMapping(value = "/pageAdvert", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<FrontAdvert> list(@RequestParam Map<String, Object> params) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            params.put("exchangeId", BaseContextHandler.getExId());
        }
        return super.queryListByCustomPage(params);
    }

}
