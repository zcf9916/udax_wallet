package com.github.wxiaoqi.security.common.mapper.casino;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.casino.CasinoCommissionLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CasinoCommissionLogMapper extends WalletBaseMapper<CasinoCommissionLog> {


    /**
     * 分红
     * @return
     */
    @Select("SELECT sum(amount) as amount  FROM casino_commission_log  WHERE receive_user_id =  #{userId}  and cms_type = #{cmsType}")
    @ResultType(value = java.math.BigDecimal.class)
    public BigDecimal getUserTotalCms(@Param("userId") Long userId,@Param("cmsType") Integer cmsType);




}