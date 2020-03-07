package com.github.wxiaoqi.security.common.mapper.front;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.front.DcAssetAccount;
import com.github.wxiaoqi.security.common.entity.front.TransferOrder;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface TransferOrderMapper extends WalletBaseMapper<TransferOrder> {



    /**
     * 锁住某条记录
     */
    public TransferOrder selectForUpdate(@Param("cm") TransferOrder param);

}