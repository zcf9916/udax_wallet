package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.WhiteExchInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface WhiteExchInfoMapper extends WalletBaseMapper<WhiteExchInfo> {

   public Long selectExchInfoByGroupCode(@Param("code")String code);
}