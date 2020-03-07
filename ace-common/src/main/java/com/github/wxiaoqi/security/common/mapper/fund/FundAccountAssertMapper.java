package com.github.wxiaoqi.security.common.mapper.fund;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.fund.FundAccountAssert;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface FundAccountAssertMapper extends WalletBaseMapper<FundAccountAssert> {


    /**
     * 锁住某个账户
     */
    public FundAccountAssert selectForUpdate(@Param("cm")FundAccountAssert param);

//    /**
//     * 更新可用余额
//     * @param param
//     * @return
//     */
//    public int updateAssert(@Param("cm")Map<String,Object> param);
//
//    /**
//     * 更新可用余额
//     * @param param
//     * @return
//     */
//    public int updateAssertDirect(@Param("cm")Map<String,Object> param);
}