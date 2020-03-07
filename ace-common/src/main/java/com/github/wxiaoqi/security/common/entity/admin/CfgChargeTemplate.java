package com.github.wxiaoqi.security.common.entity.admin;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.base.BaseAdminEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "cfg_charge_template")
public class CfgChargeTemplate extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 1. 固定值;2.比例;3.固定+比例; 4.固定点差值
     */
    @Column(name = "charge_type")
    private Integer chargeType;

    /**
     * 手续费value
     */
    @Column(name = "charge_value")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal chargeValue;


    /**
     * 1.正常;2.停用
     */
    private Integer status;

    private String remark;


}