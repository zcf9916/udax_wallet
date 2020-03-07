package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.GroupType;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface GroupTypeMapper extends WalletBaseMapper<GroupType> {
    public Long selectGroupLeadeByUserId(@Param("userId") Long userId);
}