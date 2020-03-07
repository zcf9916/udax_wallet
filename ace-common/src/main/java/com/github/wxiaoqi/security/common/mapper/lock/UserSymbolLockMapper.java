package com.github.wxiaoqi.security.common.mapper.lock;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.lock.UserSymbolLock;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface UserSymbolLockMapper extends WalletBaseMapper<UserSymbolLock> {


    /**
     * 后台查询  根据币种,后台登录人员交易所id,是否生成明细
     * 查询锁定记录
     * @param symbol
     * @param exchId
     * @param hasDetail
     * @return
     */
    public List<UserSymbolLock> getLockList(@Param("symbol") String symbol,@Param("exchId") Long exchId,@Param("hasDetail") Integer hasDetail );
}