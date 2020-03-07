package com.github.wxiaoqi.security.common.entity.front;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.github.wxiaoqi.security.common.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Table(name = "front_transfer_detail")
public class FrontTransferDetail extends BaseEntity {


    /**
     * 订单号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 转出人userid
     */
    @Column(name = "user_id")
    private Long userId;
    /**
     *转出人信息
     */
    @Transient
    private FrontUser frontUser;

    /**
     * 1.平台; 2.个人;
     */
    @Column(name = "trans_target_type")
    private Integer transTargetType;

//    private String symbol;

    /**
     * 1.需要对冲;2.不需要对冲
     */
    @Column(name = "hedge_flag")
    private Integer hedgeFlag;

    /**
     * 买方user_id（吃单方）
     */
    @Column(name = "receive_user_id")
    private Long receiveUserId;
    /**
     * 报价用户信息-->平台对冲则为null
     */
    @Transient
    private FrontUser receiveUser;

    /**
     * 1.数字货币;2.法币
     */
    @Column(name = "src_currency_type")
    private Integer srcCurrencyType;


    /**
     * 转币用户持有代币/接收用户接收币种
     */
    @Column(name = "src_symbol")
    private String srcSymbol;

    /**
     * 转出币的数量
     */
    @Column(name = "src_amount")
    private BigDecimal srcAmount;

    /**
     * 1.数字货币;2.法币
     */
    @Column(name = "dst_currency_type")
    private Integer dstCurrencyType;
    /**
     * 转币用户待转代币/接收用户持有币种
     */
    @Column(name = "dst_symbol")
    private String dstSymbol;

    /**
     * 转换得到目标币的数量
     */
    @Column(name = "dst_amount")
    private BigDecimal dstAmount;

    /**
     * 收取的手续费币种
     */
    @Column(name = "charge_currency_code")
    private String chargeCurrencyCode;

    /**
     * 收取的手续费金额
     */
    @Column(name = "charge_amount")
    private BigDecimal chargeAmount;

    /**
     * 交易价格
     */
    @Column(name = "trans_price")
    private BigDecimal transPrice;

//    /**
//     * 手续费类型
//     */
//    @Column(name = "charge_type")
//    private BigDecimal chargeType;
//
//    /**
//     * 手续费类型对应数值
//     */
//    @Column(name = "charge_type_amount")
//    private BigDecimal chargeTypeAmount;

    /**
     * 记录生成时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 订单失效时间
     */
    @Column(name = "expire_time")
    private Date expireTime;

    /**
     * 成交时间
     */
    @Column(name = "update_time")
    private Date updateTime;


    /**
     * 1:待转换   2:转换成功
     */
    private Integer status;
    /**
     * 0:未生成结算记录   1:已生成结算记录
     */
    @Column(name = "settle_status")
    private Integer settleStatus;

    private String remark;
}