package com.github.wxiaoqi.security.admin.rest.front;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.wxiaoqi.security.admin.biz.base.ParamBiz;
import com.github.wxiaoqi.security.common.entity.admin.Param;
import com.github.wxiaoqi.security.common.rest.BaseController;

@Controller
@RequestMapping("param")
public class BaseParamController extends BaseController<ParamBiz, Param> {

}
