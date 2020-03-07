package com.github.wxiaoqi.security.common.mapper.front;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.front.DcAssetAccount;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface DcAssetAccountMapper extends WalletBaseMapper<DcAssetAccount> {

    /**
     * 锁住某个账户
     */
    @Override
    public DcAssetAccount selectForUpdate(@Param("cm") DcAssetAccount param);


    /**
     * 锁住某个账户
     */
    public DcAssetAccount selectForUpdateById(Long id);

    /**
     * 数据报表-> 资产汇总
     */
    public List<DcAssetAccount> selectTotalAccount(@Param("cm")Map<String,Object> params);

}