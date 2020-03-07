package com.github.wxiaoqi.security.admin.rest.ud;

import com.github.wxiaoqi.security.admin.biz.ud.HBonusConfigBiz;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.ud.HBonusConfig;
import com.github.wxiaoqi.security.common.entity.ud.HParam;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("bonusConfig")
public class HBonusConfigController extends BaseController<HBonusConfigBiz, HBonusConfig> {

    @RequestMapping(value = "/page",method = RequestMethod.GET)
    public TableResultResponse<HBonusConfig> list(@RequestParam Map<String, Object> params){
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            params.put("exchId", BaseContextHandler.getExId());
        }
        return super.queryListByCustomPage(params);
    }

}
