package com.github.wxiaoqi.security.admin.rest.ifr;

import com.github.wxiaoqi.security.admin.biz.ifr.IfrPlanAdminBiz;
import com.github.wxiaoqi.security.common.entity.ifr.IfrPlan;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ifrPlan")
public class IfrPlanAdminController extends BaseController<IfrPlanAdminBiz, IfrPlan> {
}
