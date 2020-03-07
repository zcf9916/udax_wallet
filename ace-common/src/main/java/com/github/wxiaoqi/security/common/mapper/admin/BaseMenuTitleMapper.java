package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.BaseMenuTitle;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Map;

public interface BaseMenuTitleMapper extends WalletBaseMapper<BaseMenuTitle> {
    /**
     * 根据菜单title和语言-->查询对应的菜单名称
     * 用于记录操作日志
     * @param map
     * @return
     */
    String selectTitleByMenuCode(@Param("cm")Map<String,Object> map);
}