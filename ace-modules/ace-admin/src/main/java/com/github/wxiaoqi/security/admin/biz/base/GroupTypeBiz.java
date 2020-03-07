package com.github.wxiaoqi.security.admin.biz.base;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.GroupType;
import com.github.wxiaoqi.security.common.mapper.admin.GroupTypeMapper;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-06-12 8:48
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GroupTypeBiz extends BaseBiz<GroupTypeMapper, GroupType> {

    @Autowired
    private GroupTypeMapper groupTypeMapper;

    /**
     * 根据登录的用户查询角色类型
     *
     * @return
     */
    public List<GroupType> selectGroupTypeByUser() {
        ArrayList<GroupType> groupTypes = new ArrayList<>();
        Long groupTypeId = groupTypeMapper.selectGroupLeadeByUserId(BaseContextHandler.getUserID());
        GroupType groupType = groupTypeMapper.selectByPrimaryKey(groupTypeId);
        groupTypes.add(groupType);
        return groupTypes;
    }
}
