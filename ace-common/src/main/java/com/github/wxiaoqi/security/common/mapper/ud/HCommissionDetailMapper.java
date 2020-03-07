package com.github.wxiaoqi.security.common.mapper.ud;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.ud.HCommissionDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface HCommissionDetailMapper extends WalletBaseMapper<HCommissionDetail> {

    List<HCommissionDetail> queryCommissionReport(@Param("cm") Map<String, Object> params);

    List<HCommissionDetail> queryCommissionReportTwo(@Param("cm") Map<String, Object> params);

    List<HCommissionDetail> selectPage(@Param("cm") Map<String, Object> params);



    //获取用户的所有利润
    public BigDecimal getPowProfit(@Param(value = "userId") Long userId);
}