package com.github.wxiaoqi.security.common.base;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/*自定义共用mapper,用于新增自定义的sql操作*/
public interface WalletBaseMapper<T> extends Mapper<T> {

    /*自定义sql查询列表数据*/
    List<T> selectCustomPage(@Param("cm") Map<String, Object> params);

    /**
     * 锁住某个账户
     */
    T selectForUpdate(@Param("cm") T param);

    /**
     * 批量插入
     */
    int insertList(List<T> list);

}
