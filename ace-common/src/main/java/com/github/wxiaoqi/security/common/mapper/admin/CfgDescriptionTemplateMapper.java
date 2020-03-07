package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.CfgDescriptionTemplate;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface CfgDescriptionTemplateMapper extends WalletBaseMapper<CfgDescriptionTemplate> {

    CfgDescriptionTemplate selectDespByLanguage(@Param("language")String language);
}