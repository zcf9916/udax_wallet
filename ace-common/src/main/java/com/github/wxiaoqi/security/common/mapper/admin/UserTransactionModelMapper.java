package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.UserTransactionModel;
import com.github.wxiaoqi.security.common.util.model.HomeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserTransactionModelMapper extends WalletBaseMapper<UserTransactionModel> {
    /**
     * 用户充值汇总
     * @param param
     * @return
     */
    List<UserTransactionModel> queryRechargeTotal(@Param("cm") Map<String,Object> param);

    /**
     * 用户个人转账
     * @param param
     * @return
     */

    List<UserTransactionModel> queryTransferOrder(@Param("cm") Map<String,Object> param);
    /**
     * 充值明细
     */
    List<UserTransactionModel> queryRechargeDetail(@Param("cm") Map<String,Object> param);

    /**
     * 用户提币汇总
     */
    List<UserTransactionModel> queryWithdrawTotal(@Param("cm")Map<String,Object> param);

    /**
     * 首页展示提币汇总
     * @param param
     * @return
     */
    List<UserTransactionModel> selectUserWithdraw(@Param("cm")Map<String,Object> param);

    /**
     *  首页展示充值汇总
     */
    List<HomeVo> selectUserRecharge(@Param("cm")Map<String,Object> param);

    /**
     *  首页展示总资产
     */

    public List<HomeVo> selectTotalAccount(@Param("exchangeId")Long exchangeId);

    /**
     * 首页展示用户转账汇总
     */
    public List<UserTransactionModel> selectTotalTransfer(@Param("cm")Map<String,Object> param);

    /**
     * 首页展示商家交易汇总
     */
    public List<UserTransactionModel> selectTotalMchTrade(@Param("cm")Map<String,Object> param);

    /**
     * 数据报表
     * 提币汇总
     */
    public List<UserTransactionModel> selectTotalWithdraw(@Param("cm")Map<String,Object> param);

    /**
     * 数据报表
     * 入金汇总
     */
    public List<UserTransactionModel> selectTotalRecharge(@Param("cm")Map<String,Object> param);

    /**
     * 数据报表
     * 转账汇总
     */
    public List<UserTransactionModel> selectTransferTotal(@Param("cm")Map<String,Object> param);
    /**
     * 数据报表
     * 收支汇总之总收益汇总
     */
    public List<UserTransactionModel> selectTotalIncome(@Param("cm")Map<String,Object> param);

    /**
     * 数据报表
     * 收支汇总之充币区块链汇聚支出汇总
     */
    public List<UserTransactionModel> selectRechargeExpendTotal(@Param("cm")Map<String,Object> param);
    /**
     * 数据报表
     * 收支汇总之提币区块链汇聚支出汇总
     */
    public List<UserTransactionModel> selectWithdrawExpendTotal(@Param("cm")Map<String,Object> param);
}