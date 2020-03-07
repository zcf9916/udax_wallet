package com.github.wxiaoqi.security.admin.rest.front;

import com.github.wxiaoqi.security.admin.biz.front.CfgSymbolDescriptionBiz;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.CfgSymbolDescription;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("cfgSymbolDescription")
public class CfgSymbolDescriptionController extends BaseController<CfgSymbolDescriptionBiz, CfgSymbolDescription> {
    @Override
    public TableResultResponse<CfgSymbolDescription> list(@RequestParam Map<String, Object> params) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            params.put("exchangeId", BaseContextHandler.getExId());
        }
        return super.queryListByCustomPage(params);
    }
}
