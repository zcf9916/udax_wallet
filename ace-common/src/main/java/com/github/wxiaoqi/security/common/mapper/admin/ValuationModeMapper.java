package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.ValuationMode;
import com.github.wxiaoqi.security.common.vo.ValuationModeVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface ValuationModeMapper extends WalletBaseMapper<ValuationMode> {

   List<ValuationModeVo> cacheReturn();
   List<ValuationModeVo> selectValuationMode(@Param("cm")Map<String,Object> params);
}