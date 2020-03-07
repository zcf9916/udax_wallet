package com.github.wxiaoqi.security.admin.rest.front;


import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.wxiaoqi.security.admin.biz.front.CfgChargeTemplateBiz;
import com.github.wxiaoqi.security.common.entity.admin.CfgChargeTemplate;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.rest.BaseController;

import tk.mybatis.mapper.entity.Example;

@Controller
@RequestMapping("chargeTemplate")
public class CfgChargeTemplateController extends BaseController<CfgChargeTemplateBiz, CfgChargeTemplate> {

    @RequestMapping(value = "/templates/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<CfgChargeTemplate> list(@PathVariable Long id) {
        Example example = new Example(CfgChargeTemplate.class);
        example.createCriteria().andEqualTo("chargeType", id);
        return baseBiz.selectByExample(example);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseBody
    public List<CfgChargeTemplate> all() {
        Example example = new Example(CfgChargeTemplate.class);
        example.createCriteria().andEqualTo("status", EnableType.ENABLE.value());
        return baseBiz.selectByExample(example);
    }
}
