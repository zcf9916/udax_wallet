package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.TransferExch;
import com.github.wxiaoqi.security.common.vo.SymbolTransfer;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface TransferExchMapper extends WalletBaseMapper<TransferExch> {

    //根据主键查询
    TransferExch selectTransferByExchId(@Param(("transferId")) Long transferId,@Param("exchId") Long exchId);

    List<TransferExch> selectChargeAll();

    List<SymbolTransfer> selectTransfer(@Param("cm")Map<String,Object> params);
}