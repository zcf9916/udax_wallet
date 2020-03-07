package com.github.wxiaoqi.security.admin.rest.front;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.wxiaoqi.security.admin.biz.front.CfgCurrencyTransferBiz;
import com.github.wxiaoqi.security.common.entity.admin.CfgCurrencyTransfer;
import com.github.wxiaoqi.security.common.rest.BaseController;

@Controller
@RequestMapping("currencyTransfer")
public class CfgCurrencyTransferController extends BaseController<CfgCurrencyTransferBiz, CfgCurrencyTransfer> {

}
