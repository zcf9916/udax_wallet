package com.github.wxiaoqi.security.admin.rest.front;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.wxiaoqi.security.admin.biz.front.FrontCountryBiz;
import com.github.wxiaoqi.security.common.entity.admin.FrontCountry;
import com.github.wxiaoqi.security.common.rest.BaseController;

@Controller
@RequestMapping("frontCountry")
public class FrontCountryController extends BaseController<FrontCountryBiz, FrontCountry> {

}
