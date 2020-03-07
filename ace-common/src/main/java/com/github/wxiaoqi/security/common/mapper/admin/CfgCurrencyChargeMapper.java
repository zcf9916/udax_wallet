package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.CfgCurrencyCharge;
import com.jcraft.jsch.UserInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface CfgCurrencyChargeMapper extends WalletBaseMapper<CfgCurrencyCharge> {
    /**
     * 新增
     * @param charge
     * @return
     */
    int insertCharge(CfgCurrencyCharge charge);

    /**
     * 更新
     * @param charge
     * @return
     */
    int updateCharge(CfgCurrencyCharge charge);
    /**
     * 根据手续模板id查询
     * @param id
     * @return
     */
    List<CfgCurrencyCharge> selectByChargeTemplate(@Param("id") Long id);

    /**
     *根据基础货币id or exchId查询 手续费
     * @return
     */
    CfgCurrencyCharge selectChargeByBasisId(@Param("cm")Map<String,Object> param);
        /**
         *  根据币种获取手续费
         */
     CfgCurrencyCharge selectChargeBySymbol(@Param("symbol") String symbol);

    /**
     * 根据基础货币id删除货币配置
     * @param id
     */

    void deleteByBasicId(Object id);

    /**
     * 查询所有
     */
    List<CfgCurrencyCharge> selectChargeList();

    CfgCurrencyCharge selectByBasisId();
}