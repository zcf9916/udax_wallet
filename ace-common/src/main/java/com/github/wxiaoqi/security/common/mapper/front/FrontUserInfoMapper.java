package com.github.wxiaoqi.security.common.mapper.front;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.front.FrontUserInfo;
import tk.mybatis.mapper.common.Mapper;

public interface FrontUserInfoMapper extends WalletBaseMapper<FrontUserInfo> {



    public FrontUserInfo selectByUserId(Long userId);
}