package com.github.wxiaoqi.security.common.mapper.front;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.front.TransferList;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TransferListMapper extends WalletBaseMapper<TransferList> {


    public List<TransferList> selectUnionUserInfo(@Param("cm")TransferList param);
}