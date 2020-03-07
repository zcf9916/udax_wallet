package com.github.wxiaoqi.security.admin.rest.front;

import com.github.wxiaoqi.security.admin.biz.front.CfgDescriptionTemplateBiz;
import com.github.wxiaoqi.security.common.entity.admin.CfgDescriptionTemplate;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("cfgDescriptionTemplate")
public class CfgDescriptionTemplateController extends BaseController<CfgDescriptionTemplateBiz, CfgDescriptionTemplate> {

}
