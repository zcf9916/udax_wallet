package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.Menu;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface MenuMapper extends WalletBaseMapper<Menu> {
    public List<Menu> selectMenuByAuthorityId(@Param("authorityId") String authorityId,@Param("authorityType") String authorityType);

    /**
     * 根据用户和组的权限关系查找用户可访问菜单
     * @param userId
     * @return
     */
    public List<Menu> selectAuthorityMenuByUserIdAndLanguage (@Param("userId") Long userId,@Param("language") String language);
    public List<Menu> selectAuthorityMenuByUserId (@Param("userId") Long userId);

    /**
     * 根据用户和组的权限关系查找用户可访问的系统
     * @param userId
     * @return
     */
    public List<Menu> selectAuthoritySystemByUserId (@Param("userId") Long userId);

    /**
     * 根据语言获取所有菜单
     */
    public List<Menu> selectListByLanguage(@Param("cm")Map<String,Object> param);
}