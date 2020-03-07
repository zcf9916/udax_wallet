package com.github.wxiaoqi.security.common.mapper.fund;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.fund.FundPurchaseInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface FundPurchaseInfoMapper extends WalletBaseMapper<FundPurchaseInfo> {

    public FundPurchaseInfo selectIdForUpdate(Long id);
    /**
     * 联合基金收益表查询
     * @param params
     * @return
     */
    public List<FundPurchaseInfo> selectProdLinkProPage( @Param(value = "cm") Map<String,Object> params);

    /**
     *查询总投资人数和基金明细总数
     */
    public Map<String,Long> getInvestors(Long fundId);

}