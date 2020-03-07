package com.github.wxiaoqi.security.admin.rest.base;


import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import org.springframework.web.bind.annotation.*;

import com.github.wxiaoqi.security.admin.biz.base.GateLogBiz;
import com.github.wxiaoqi.security.common.entity.admin.GateLog;
import com.github.wxiaoqi.security.common.rest.BaseController;

import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-07-01 20:32
 */
@RestController
@RequestMapping("gateLog")
public class GateLogController extends BaseController<GateLogBiz, GateLog> {
    @RequestMapping(value = "/pageLog",method = RequestMethod.GET)
    public TableResultResponse<GateLog> list(@RequestParam Map<String, Object> params){
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)){
            params.put("exchangeId",BaseContextHandler.getExId());
        }
        return super.queryListByCustomPage(params);
    }
}
