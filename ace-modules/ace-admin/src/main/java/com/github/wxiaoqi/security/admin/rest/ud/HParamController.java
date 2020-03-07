package com.github.wxiaoqi.security.admin.rest.ud;

import com.github.wxiaoqi.security.admin.biz.ud.HParamBiz;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.ud.HParam;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import com.github.wxiaoqi.security.common.util.Query;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("udParam")
public class HParamController extends BaseController<HParamBiz, HParam> {

    @RequestMapping(value = "/page",method = RequestMethod.GET)
    public TableResultResponse<HParam> list(@RequestParam Map<String, Object> params){
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            params.put("exchId", BaseContextHandler.getExId());
        }
      return super.queryListByCustomPage(params);
    }
}
