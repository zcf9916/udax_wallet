package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.SymbolExch;
import com.github.wxiaoqi.security.common.vo.SymbolCurrencyCharge;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SymbolExchMapper extends WalletBaseMapper<SymbolExch> {

    List<SymbolCurrencyCharge> selectSymbolAndChange(@Param("cm")Map<String,Object> param);

    public List<SymbolExch> getLockSymbolList(@Param("exchId") Long exchId,@Param("hasLock")Integer hasLock);
}