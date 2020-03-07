package com.github.wxiaoqi.security.common.mapper.fund;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.fund.FundProductProfiltInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Map;

public interface FundProductProfiltInfoMapper extends WalletBaseMapper<FundProductProfiltInfo> {

    public FundProductProfiltInfo selectForUpdate(@Param(value = "cm") Map<String,Object> params);
}