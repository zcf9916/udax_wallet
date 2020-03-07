package com.github.wxiaoqi.security.common.entity.admin;

import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
@Data
@Table(name = "hedge_detail")
public class HedgeDetail {
    @Id
    private Long id;

    /**
     * 成交时间
     */
    @Column(name = "trade_time")
    private Date tradeTime;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 订单号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 源代币
     */
    @Column(name = "src_symbol")
    private String srcSymbol;

    /**
     * 目标币
     */
    @Column(name = "dst_symbol")
    private String dstSymbol;

    /**
     * 源数量
     */
    @Column(name = "src_amount")
    private BigDecimal srcAmount;

    /**
     * 转换得到目标币的数量
     */
    @Column(name = "dst_amount")
    private BigDecimal dstAmount;

    /**
     * 交易价格
     */
    @Column(name = "trans_price")
    private BigDecimal transPrice;

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 外部交易对
     */
    @Column(name = "out_symbol")
    private String outSymbol;

    /**
     * 成交方向
     */
    private Integer direction;

    /**
     * 初次对冲时间
     */
    @Column(name = "hedge_time")
    private Date hedgeTime;

    /**
     * 外部委托单号
     */
    @Column(name = "hedge_order_id")
    private String hedgeOrderId;

    /**
     * 对冲委托价格
     */
    @Column(name = "hedge_price")
    private BigDecimal hedgePrice;

    /**
     * 对冲成交价格
     */
    @Column(name = "hedge_trade_price")
    private BigDecimal hedgeTradePrice;

    /**
     * 自动对冲状态
     */
    @Column(name = "hedge_status")
    private Integer hedgeStatus;

    /**
     * 对冲状态描述
     */
    @Column(name = "hedge_status_text")
    private String hedgeStatusText;

    /**
     * 对冲状态更新时间
     */
    @Column(name = "hedge_update_time")
    private Date hedgeUpdateTime;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 是否已人工处理
     */
    @Column(name = "admin_dealed")
    private Integer adminDealed;


    @Column
    private String proxyCode;

    @Transient
    private FrontUser frontUser;

}