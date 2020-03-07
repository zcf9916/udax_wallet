package com.github.wxiaoqi.security.admin.rest.ifr;

import com.github.wxiaoqi.security.admin.biz.ifr.IfrExchangeRateBiz;
import com.github.wxiaoqi.security.common.entity.ifr.IfrExchangeRate;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ifrExchangeRate")
public class IfrExchangeRateController extends BaseController<IfrExchangeRateBiz, IfrExchangeRate> {
}
