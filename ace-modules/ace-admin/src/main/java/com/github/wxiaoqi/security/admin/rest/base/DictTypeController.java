package com.github.wxiaoqi.security.admin.rest.base;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.wxiaoqi.security.admin.biz.base.DictTypeBiz;
import com.github.wxiaoqi.security.common.entity.admin.DictType;
import com.github.wxiaoqi.security.common.rest.BaseController;

@Controller
@RequestMapping("dictType")
public class DictTypeController extends BaseController<DictTypeBiz,DictType> {

}
