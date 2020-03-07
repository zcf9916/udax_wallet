package com.github.wxiaoqi.security.common.mapper.front;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.front.GeneratorId;
import org.springframework.data.repository.query.Param;
import tk.mybatis.mapper.common.Mapper;


public interface GeneratorIdMapper extends WalletBaseMapper<GeneratorId> {


    public  GeneratorId selectForUpdateByKey(@Param("key") String key);
}