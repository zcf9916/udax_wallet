package com.github.wxiaoqi.security.admin.rest.base;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.wxiaoqi.security.admin.biz.base.GroupTypeBiz;
import com.github.wxiaoqi.security.common.entity.admin.GroupType;
import com.github.wxiaoqi.security.common.rest.BaseController;

@Controller
@RequestMapping("groupType")
public class GroupTypeController extends BaseController<GroupTypeBiz, GroupType> {
    @Autowired
    private GroupTypeBiz groupTypeBiz;


    @RequestMapping(value = "/selectGroupTypeByUser", method = RequestMethod.GET)
    @ResponseBody
    public List<GroupType> getGroupTypeByUser() {
        return groupTypeBiz.selectGroupTypeByUser();
    }

}
