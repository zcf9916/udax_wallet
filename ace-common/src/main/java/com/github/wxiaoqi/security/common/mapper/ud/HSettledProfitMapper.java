package com.github.wxiaoqi.security.common.mapper.ud;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.ud.HSettledProfit;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface HSettledProfitMapper extends WalletBaseMapper<HSettledProfit> {
    /**
     * 锁住某个订单
     *
     * @param orderNo
     */
    public HSettledProfit selectOrderNoForUpdate(String orderNo);

    //获取用户的所有利润
    public BigDecimal getAllProfit(@Param(value = "userId") Long userId);
}
