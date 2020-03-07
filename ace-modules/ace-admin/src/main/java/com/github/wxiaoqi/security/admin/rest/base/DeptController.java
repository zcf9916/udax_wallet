package com.github.wxiaoqi.security.admin.rest.base;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.wxiaoqi.security.admin.biz.base.DeptBiz;
import com.github.wxiaoqi.security.common.entity.admin.Dept;
import com.github.wxiaoqi.security.common.rest.BaseController;

@Controller
@RequestMapping("dept")
public class DeptController extends BaseController<DeptBiz, Dept> {

}
