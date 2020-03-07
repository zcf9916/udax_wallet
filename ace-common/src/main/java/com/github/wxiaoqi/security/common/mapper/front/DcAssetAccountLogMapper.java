package com.github.wxiaoqi.security.common.mapper.front;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.front.DcAssetAccountLog;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DcAssetAccountLogMapper extends WalletBaseMapper<DcAssetAccountLog> {

    /**
     * 级联查询fronguserInfo表
     * @param param
     * @return
     */
    public List<Map<String,Object>> getAssertCount(@Param("cm") Map<String,Object> param);


    /**
     * 获取最新流水
     * @param log
     * @return
     */
    public DcAssetAccountLog getLatestLog(@Param("cm")DcAssetAccountLog log);
}