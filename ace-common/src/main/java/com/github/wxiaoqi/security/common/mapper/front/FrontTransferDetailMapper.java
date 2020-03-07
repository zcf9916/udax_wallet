package com.github.wxiaoqi.security.common.mapper.front;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.front.FrontTransferDetail;
import com.github.wxiaoqi.security.common.entity.front.TransferOrder;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface FrontTransferDetailMapper extends WalletBaseMapper<FrontTransferDetail> {

    /**
     * 首页展示 币币互转
     */
    List<FrontTransferDetail> selectTotalTransferDetail(@Param("cm")Map<String,Object> params);

    /**
     * 锁住某条记录
     */
    public FrontTransferDetail selectForUpdate(@Param("cm") FrontTransferDetail param);
}