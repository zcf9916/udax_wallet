package com.github.wxiaoqi.security.common.entity.admin;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.base.BaseAdminEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 转换配置表
 */
@Getter@Setter
@Table(name = "cfg_currency_transfer")
public class CfgCurrencyTransfer extends BaseAdminEntity {

    private static final long serialVersionUID = 12L;



    /**
     * 1.平台接收; 2.用户报价接收
     * CurrencyTransferType类
     */
    @Column(name = "transfer_type")
    private Integer transferType;

    /**
     * 例: BTC
     */
    @Column(name = "src_symbol")
    private String srcSymbol;

    /**
     * 例: USDT
     */
    @Column(name = "dst_symbol")
    private String dstSymbol;
    
    /**
	 * 可兑换的目标币种集合
	 */
    @Transient
	private List<CfgCurrencyTransfer> dstList;

    /**
     * 例: BTC/USDT,不需对冲的可不填
     */
    private String symbol;

    /**
     * 1.需要对冲;2.不需对冲
     */
    @Column(name = "hedge_flag")
    private Integer hedgeFlag;

    /**
     * 原货币单次最小转币量
     */
    @Column(name = "min_trans_amount")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal minTransAmount;

    /**
     * 原货币单次最大转币量
     */
    @Column(name = "max_trans_amount")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal maxTransAmount;

    /**
     * 成交偏差最大值
     */
    @Column(name = "offset_price")
    private BigDecimal offsetPrice;

    @Column(name = "charge_dc_code")
    private String chargeDcCode;

    /**
     * 1.正常;2.停用
     */
    private Integer status;


    private String remark;

    //手续费
    @Transient
    private CfgChargeTemplate cfgChargeTemplate;
    //排序
    @Column(name = "sort")
    private Integer sort;

    @Transient
    private Long exchId;
}