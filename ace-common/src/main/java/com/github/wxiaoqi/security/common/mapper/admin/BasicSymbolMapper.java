package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.BasicSymbol;
import com.github.wxiaoqi.security.common.entity.front.FrontRecharge;
import com.github.wxiaoqi.security.common.util.model.IconModel;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface BasicSymbolMapper extends WalletBaseMapper<BasicSymbol> {

    List<BasicSymbol> selectBasicSymbol(@Param("symbol") String symbol);

    List<BasicSymbol> selectBasicSrcSymbol(@Param("symbol") List<String> symbol);

    List<BasicSymbol> selectSymbolByExchId(@Param("cm")Map<String,Object> param);

    public List<IconModel> selectListIcon(@Param("status") Integer status);
    //关联查询缓存
    List<BasicSymbol> cacheReturn();
    //根据代币币种和交易id关联查询
    Integer selectSymbolCount(@Param("symbol") String symbol,@Param("exchId")Long exchId);
}
