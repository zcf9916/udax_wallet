package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.User;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.HashMap;
import java.util.List;

public interface UserMapper extends WalletBaseMapper<User> {
    public List<User> selectMemberByGroupId(@Param("groupId") int groupId);

    public List<User> selectLeaderByGroupId(@Param("groupId") int groupId);

    public List<User> selectUserByGroupId(@Param("groupId") Long groupId);

    List<User> selectUserByQuery( @Param("cm") HashMap<String, Object> params);
}