package com.github.wxiaoqi.security.admin.util;

import java.util.ArrayList;
import java.util.List;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.Group;
import com.github.wxiaoqi.security.common.entity.admin.GroupLeader;
import com.github.wxiaoqi.security.common.mapper.admin.GroupMapper;

import tk.mybatis.mapper.entity.Example;

public class GroupsIdUtil {

    public static  List<Group> getGroups(Long userId, GroupMapper mapper){
        Group group = null;
        ArrayList<Group> userGroups = new ArrayList<>();
        List<Group> groups = mapper.selectGroupByUserId(userId);
        if (groups.size() < 0) {
            return new ArrayList<>();
        }
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            Group g2 = groups.get(0);
            g2.setParentId(AdminCommonConstant.ROOT);
        }
        for (Group g : groups) {
            recursionAddGroups(userGroups, g,mapper);
        }
        return userGroups;
    }

    private static void recursionAddGroups(ArrayList<Group> userGroups, Group g,GroupMapper mapper) {
        Long currentGroupId=  g.getId();
        userGroups.add(g);
        List<Group> groupList = mapper.selectGroupByParent(currentGroupId);
        if (groupList.size() < 0) {
            return;
        }
        for (Group group : groupList) {
            recursionAddGroups(userGroups, group,mapper);
        }
    }
}
