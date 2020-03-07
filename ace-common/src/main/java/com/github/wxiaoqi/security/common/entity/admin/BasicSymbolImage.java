package com.github.wxiaoqi.security.common.entity.admin;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import lombok.Data;

import java.util.Date;
import javax.persistence.*;

@Table(name = "basic_symbol_image")
@Data
public class BasicSymbolImage extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 交易所id
     */
    @Column(name = "exchange_id")
    private Long exchangeId;

    /**
     * 白标币种图标地址
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * 币种主键id
     */
    @Column(name = "basic_symbol_id")
    private Long basicSymbolId;

    /**
     * 币种名称
     */
    private String symbol;

    private String remark;

    @Transient
    private WhiteExchInfo exchInfo;

}