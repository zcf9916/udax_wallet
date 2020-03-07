package com.github.wxiaoqi.security.common.entity.admin;

import lombok.Data;

import javax.persistence.*;

/**
 * 计价方式中间表
 */
@Data
@Table(name = "valuation_mode")
public class ValuationMode {
    @Id
    private Long id;

    /**
     * 数据字典主键
     */
    @Column(name = "dict_data_id")
    private Long dictDataId;

    /**
     * 交易所id
     */
    @Column(name = "exch_id")
    private Long exchId;

    @Column(name = "default_symbol")
    private String defaultSymbol;
}