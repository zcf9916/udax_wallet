package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.CfgCurrencyTransfer;
import com.github.wxiaoqi.security.common.entity.admin.User;
import com.github.wxiaoqi.security.common.entity.admin.UserOfferInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface CfgCurrencyTransferMapper extends WalletBaseMapper<CfgCurrencyTransfer> {

    List<CfgCurrencyTransfer> selectQuery(@Param("cm") Map<String,Object> params);

    /**
     * 新增
     */
    int insertTransfer(CfgCurrencyTransfer transfer);

    /**
     * 更新
     */
    int updateTransfer(CfgCurrencyTransfer transfer);

    /**
     * 根据主键查询
     */
    CfgCurrencyTransfer selectTransferById(@Param("id") Long id);

    Integer selectTransferBySymbol(@Param("cm") Map<String,Object> param);

    List<CfgCurrencyTransfer> selectTransferBySrcSymbol(@Param("srcSymbol") String srcSymbol,@Param("exchId")Long exchId);

    List<CfgCurrencyTransfer> cacheReturn();

}