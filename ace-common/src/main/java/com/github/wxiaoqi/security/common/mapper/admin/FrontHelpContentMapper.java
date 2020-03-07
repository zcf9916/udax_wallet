package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.FrontHelpContent;

import java.util.List;

public interface FrontHelpContentMapper extends WalletBaseMapper<FrontHelpContent> {

    List<FrontHelpContent> queryFrontHelpForTypeName();
}