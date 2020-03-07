package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.CfgDcRechargeWithdraw;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface CfgDcRechargeWithdrawMapper extends WalletBaseMapper<CfgDcRechargeWithdraw> {
    CfgDcRechargeWithdraw getConfig(@Param("symbol")String symbol,@Param("systemConfig")Integer systemConfig,@Param("protocolType")String protocolTyp);

    void deleteAll(@Param("symbol") String symbol,@Param("protocolType")String protocolType);
}