package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.DictData;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface DictDataMapper extends WalletBaseMapper<DictData> {

    List<DictData> GroupDictData();

    List<DictData> selectListData(@Param("type")String type,@Param("language")String language);
}