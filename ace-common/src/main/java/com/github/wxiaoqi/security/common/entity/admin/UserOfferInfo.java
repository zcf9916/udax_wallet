package com.github.wxiaoqi.security.common.entity.admin;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;

import lombok.Data;
@Data
@Table(name = "user_offer_info")
public class UserOfferInfo extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 关联前端用户
     */
    @Transient
    private FrontUser frontUser;

    /**
     * 原货币
     */
    @Column(name = "src_symbol")
    private String srcSymbol;

    /**
     * 目的货币
     */
    @Column(name = "dst_symbol")
    private String dstSymbol;

    /**
     * 报价价格
     */
    @Column(name = "order_price")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal orderPrice;

    /**
     * 报价数量
     */
    @Column(name = "order_volume")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal orderVolume;

    /**
     * 剩余数量
     */
    @Column(name = "remain_volume")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal remainVolume;



    /**
     * 可兑换的目标币种集合
     */
    @Transient
    private List<String> dstList;

    /**
     * 1.正常;2.已撤销;
     */
    private Integer status;

    private String remark;

    @Transient
    private String symbol;
    /**
     * 后台用户id
     */
    @Column(name = "admin_id")
    private Long adminId;
}