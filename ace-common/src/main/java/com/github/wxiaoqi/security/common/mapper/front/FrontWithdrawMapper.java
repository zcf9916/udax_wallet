package com.github.wxiaoqi.security.common.mapper.front;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.front.FrontWithdraw;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FrontWithdrawMapper extends WalletBaseMapper<FrontWithdraw> {


    /**
     *
     * @param userId 用户
     * @param dcCode
     * @return
     */
    public BigDecimal queryWithdrawDaily(@Param("userId")Long userId, @Param("dcCode")String dcCode);

    void updateById(FrontWithdraw frontWithdraw);

    FrontWithdraw selectForUpdate2(@Param("id")Long id);


    public List<FrontWithdraw> selectUnionExId(@Param("cm")FrontWithdraw param);
}