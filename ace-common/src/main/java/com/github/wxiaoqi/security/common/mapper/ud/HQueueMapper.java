package com.github.wxiaoqi.security.common.mapper.ud;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.ud.HQueue;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Queue;

public interface HQueueMapper extends WalletBaseMapper<HQueue> {



    //获取上一个队列号/订单号
    public HQueue selectForUpdate(@Param(value ="orderNo") String orderNo);

    //获取匹配的队列数据
    public List<HQueue> getMatchList(@Param(value ="cm") Map<String,Object> param);


    public List<HQueue> selectQueueList(@Param(value ="cm") Map<String,Object> param);
}