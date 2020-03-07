package com.github.wxiaoqi.security.common.mapper.ud;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.front.DcAssetAccount;
import com.github.wxiaoqi.security.common.entity.ud.HOrderDetail;
import org.apache.ibatis.annotations.Param;

public interface HOrderDetailMapper extends WalletBaseMapper<HOrderDetail> {


    /**
     * 锁住订单
     */
    public HOrderDetail selectForUpdate(@Param("cm") HOrderDetail param);
}