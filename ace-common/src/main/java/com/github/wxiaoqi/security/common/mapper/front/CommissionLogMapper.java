package com.github.wxiaoqi.security.common.mapper.front;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.front.CommissionLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CommissionLogMapper extends WalletBaseMapper<CommissionLog> {

    /**
     * 获取用户每日分成的数据汇总  按代币,group
     * @param beginTime
     * @param endTime
     * @param settleStatus
     * @param type
     * @return
     */
    @Select("SELECT sum(settle_amount) as amount ,receive_user_id as userId,settle_symbol as symbol FROM commission_log  WHERE order_time between #{beginTime} and #{endTime} and settle_status=#{settleStatus} and type != #{type} group by receive_user_id,settle_symbol")
    @ResultType(value = java.util.List.class)
    public List<Map<String,Object>> selectSettleData(@Param("beginTime") Long beginTime,@Param("endTime") Long endTime, @Param("settleStatus") Integer settleStatus,@Param("type")Integer type);


    /**
     * 获取用户总收益
     * @return
     */
    @Select("SELECT sum(settle_amount) as amount  FROM commission_log  WHERE receive_user_id =  #{userId} and settle_status=1")
    @ResultType(value = java.math.BigDecimal.class)
    public BigDecimal getUserTotalCms(@Param("userId") Long userId);

}