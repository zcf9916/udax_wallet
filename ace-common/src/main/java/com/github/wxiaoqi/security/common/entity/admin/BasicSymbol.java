package com.github.wxiaoqi.security.common.entity.admin;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;

import lombok.Data;

@Table(name = "basic_symbol")
@Data
public class BasicSymbol extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 1.数字货币;2.法定货币
     */
    @Column(name = "currency_type")
    private Integer currencyType;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "image_url")
    private String imageUrl;

    /**
     * 1.正常;2.停用
     */
    private Integer status;

    private String remark;

    //换算保留小数位
    private Integer decimalPlaces;


    private String protocolType;

    private Integer isShow;

    private Integer sort;
    /**
     * 币种是否支持用户报价: 0 不支持 ,1支持
     */
    @Column(name = "is_quote")
    private Integer isQuote;
    /**
     * 手续费配置
     */
    @Transient
    private CfgCurrencyCharge cfgCurrencyCharge;

    @Transient
    private Long exchId;

}