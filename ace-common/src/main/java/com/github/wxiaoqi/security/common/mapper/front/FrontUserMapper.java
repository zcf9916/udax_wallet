package com.github.wxiaoqi.security.common.mapper.front;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface FrontUserMapper extends WalletBaseMapper<FrontUser> {

    /**
     * 级联查询fronguserInfo表
     * @param param
     * @return
     */
    public FrontUser selectUnionUserInfo(@Param("cm") Map<String,Object> param);


    /**
     * 级联查询UD社区的huserInfo表
     * @param param
     * @return
     */
    public FrontUser selectUDUionUserInfo(@Param("cm") Map<String,Object> param);


    /**
     * 级联查询赌场的casinoUserInfo表
     * @param param
     * @return
     */
    public FrontUser selectCasinoUionUserInfo(@Param("cm") Map<String,Object> param);


}