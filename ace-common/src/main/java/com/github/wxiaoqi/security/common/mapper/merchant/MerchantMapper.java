package com.github.wxiaoqi.security.common.mapper.merchant;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.merchant.Merchant;

public interface MerchantMapper extends WalletBaseMapper<Merchant> {
    Integer selectMerchantCount(@Param("cm")Map<String,Object> params);
}