package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.BasicSymbolImage;
import com.github.wxiaoqi.security.common.util.model.IconModel;

import java.util.List;

public interface BasicSymbolImageMapper extends WalletBaseMapper<BasicSymbolImage> {

    public List<IconModel> selectExchAndImage();
}