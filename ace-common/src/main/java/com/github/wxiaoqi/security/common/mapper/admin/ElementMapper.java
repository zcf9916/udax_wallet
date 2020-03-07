package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.Element;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface ElementMapper extends WalletBaseMapper<Element> {
    public List<Element> selectAuthorityElementByUserId(@Param("userId")Long userId);
    public List<Element> selectAuthorityMenuElementByUserId(@Param("userId")Long userId,@Param("menuId")Long menuId);
    public List<Element> selectAuthorityElementByClientId(@Param("clientId")String clientId);
    public List<Element> selectAllElementPermissions();

    List<Long> selectElementByMenuIdAndUserId(@Param("currentGroupId")Long currentGroupId, @Param("menuId")Long menuId, @Param("resourceType")String resourceType);

    List<Element> selectElement(@Param("cm") Map<String, Object> params);

    List<Element>  selectElementByMenuId(@Param("menuId") String menuId);

    List<Element> selectElementByMenuAll(@Param("cm")Map<String,Object> params);
}