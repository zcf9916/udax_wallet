package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.Group;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface GroupMapper extends WalletBaseMapper<Group> {
    public void deleteGroupMembersById (@Param("groupId") Long groupId);
    public void deleteGroupLeadersById (@Param("groupId") Long groupId);
    public void insertGroupMembersById (@Param("groupId") Long groupId,@Param("userId") Long userId);
    public void insertGroupLeadersById (@Param("groupId") Long groupId,@Param("userId") Long userId);

    /**
     * 根据用户查询群组
     * @param userId
     * @return
     */
    public List<Group> selectGroupByUserId(@Param("userId")Long userId);
    /**
     * 查询用户资群组
     */
    public List<Group> selectGroupByParent(@Param("parentId") Long parentId);
    /**
     * 根据用户ID查询群组
     */
     public String selectGroupLeader(@Param("userId") Long userId);
    /**
     * 根据用户ID查询群组id
     */
    public Long selectGroup(@Param("userId") Long userId);

    /**
     * 查询角色编码是否重复
     */
    public Integer selectGroupCode(@Param("code")String code);
}