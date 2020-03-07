package com.github.wxiaoqi.security.common.mapper.merchant;

import org.apache.ibatis.annotations.Param;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.merchant.MchTradeDetail;

public interface MchTradeDetailMapper extends WalletBaseMapper<MchTradeDetail> {

	MchTradeDetail selectByOrderNo(@Param("cm") MchTradeDetail mchparam);
}