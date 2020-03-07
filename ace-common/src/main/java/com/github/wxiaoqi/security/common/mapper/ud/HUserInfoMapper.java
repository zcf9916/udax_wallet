package com.github.wxiaoqi.security.common.mapper.ud;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.ud.HUserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface HUserInfoMapper extends WalletBaseMapper<HUserInfo> {

    //批量把用户的有效用户数量+1
    public   void increTotalChild(@Param(value = "list") List<Long> list);


    //直推用户+1
    public   void increDirectChild(Long id);


    //子节点的总投资额增加
    public   void increChildInvest(@Param(value = "cm") Map<String,Object> list);

    public List<HUserInfo> selectAllChildOfTopUser(Long userId);

    public List<HUserInfo> selectGlobalUser(@Param(value = "cm") Map<String,Object> pm);

    public List<HUserInfo> selectAllByChildUserId(@Param("id")Long id);



}