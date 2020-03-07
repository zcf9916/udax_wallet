package com.github.wxiaoqi.security.common.entity.merchant;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "mch_refund_detail")
@Getter
@Setter
public class MchRefundDetail extends BaseEntity {

    /**
     * 钱包退款订单号
     */
    @Column(name = "wallet_order_no")
    private String walletOrderNo;

    /**
     * 商户退款订单号
     */
    @Column(name = "mch_order_no")
    private String mchOrderNo;

    /**
     * 钱包支付订单号(退款源订单号)
     */
    @Column(name = "ori_wallet_order_no")
    private String oriWalletOrderNo;

    /**
     * 商户支付订单号(退款源商户订单号)
     */
    @Column(name = "ori_mch_order_no")
    private String oriMchOrderNo;

    /**退款账户类型**/
    @Column(name = "refund_account_type" )
    private Integer refundAccountType;

    /**
     * 商户号
     */
    @Column(name = "mch_no")
    private Long mchNo;

    /**
     * 商户id
     */
    @Column(name = "mch_id")
    private Long mchId;

    /**
     * 商户对应用户ID
     */
    @Column(name = "mch_user_id")
    private Long mchUserId;

    /**
     * 退款的用户ID
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 代币代码
     */
    private String symbol;

    /**
     * 订单代币数量
     */
    private BigDecimal amount;

    /**
     * 退款原因
     */
    @Column(name = "refund_remark")
    private String refundRemark;

    private String ip;

//    @Column(name = "trade_type")
//    private String tradeType;


    /**
     * 退款结果通知url
     */
    @Column(name = "notify_url")
    private String notifyUrl;

    /**
     * 生成订单时间
     */
    @Column(name = "create_time")
    private Date createTime;

    //原订单总金额
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    //随机字符串
    @Column(name = "nonce_str")
    private String nonceStr;
    /**
     * 退款用户信息
     */
    @Transient
    private FrontUser frontUser;

    /**
     * 商家用户基础信息
     */
    @Transient
    private FrontUser mchFrontUser;

}