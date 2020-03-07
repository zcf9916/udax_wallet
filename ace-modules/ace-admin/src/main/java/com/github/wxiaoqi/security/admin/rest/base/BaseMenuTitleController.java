package com.github.wxiaoqi.security.admin.rest.base;


import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.wxiaoqi.security.admin.biz.base.BaseMenuTitleBiz;
import com.github.wxiaoqi.security.common.entity.admin.BaseMenuTitle;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;

import tk.mybatis.mapper.entity.Example;

@Controller
@RequestMapping("menuTitle")
public class BaseMenuTitleController extends BaseController<BaseMenuTitleBiz, BaseMenuTitle> {

    @Autowired
    private BaseMenuTitleBiz baseMenuTitleBiz;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<BaseMenuTitle> page(@RequestParam(defaultValue = "10") int limit,
                                                   @RequestParam(defaultValue = "1") int offset, String name, @RequestParam(defaultValue = "0") Long menuId) {
        Example example = new Example(BaseMenuTitle.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("menuId", menuId);
        if (StringUtils.isNotBlank(name)) {
            criteria.andLike("name", "%" + name + "%");
        }
        List<BaseMenuTitle> titles = baseMenuTitleBiz.selectByExample(example);
        return new TableResultResponse<BaseMenuTitle>(titles.size(), titles);
    }
}
