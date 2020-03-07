package com.github.wxiaoqi.security.admin.rest.front;


import com.github.wxiaoqi.security.admin.biz.front.CfgDcRechargeWithdrawBiz;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.CfgDcRechargeWithdraw;
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
@RequestMapping("rechargeWithdraw")
public class CfgDcRechargeWithdrawController extends BaseController<CfgDcRechargeWithdrawBiz, CfgDcRechargeWithdraw> {

    @RequestMapping(value = "/pageAll", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<CfgDcRechargeWithdraw> list(@RequestParam Map<String, Object> params) {
        //配置交易所默认配置信息
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            params.put("exchangeId", BaseContextHandler.getExId());
        }
        return super.queryListByCustomPage(params);
    }


    @RequestMapping(value = "/pageAdmin", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<CfgDcRechargeWithdraw> listPageAdmin(@RequestParam Map<String, Object> params) {
        return super.queryListByCustomPage(params);
    }
}
