package com.github.wxiaoqi.security.common.entity.front;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
@Data
@Table(name = "commission_log")
public class CommissionLog extends BaseEntity {

    private static final long serialVersionUID = 1L;


    //分成订单类型
    public  enum CmsType{
        TRANSFER_COIN(2),//转币
        TRANSFER(1);//转账
        private Integer value;
        CmsType(Integer value){
            this.value = value;
        }

        public Integer value(){return this.value;}
    }



    /**
     * 编号
     */
    @Id
    private Long id;

    /**
     * 成交单号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 交易用户ID
     */
    @Column(name = "tradeuser_id")
    private Long tradeuserId;

    /**
     * 分成类型  1 转账  2 转币
     */
    @Column(name = "order_type")
    private Integer orderType;

    /**
     * 交易用户名
     */
    @Column(name = "tradeuser_name")
    private String tradeuserName;

    /**
     * 收取佣金的用户/白标ID
     */
    @Column(name = "receive_user_id")
    private Long receiveUserId;

    /**
     * 交易用户ID
     */
    @Column(name = "receive_user_name")
    private String receiveUserName;

    /**
     * 白标ID
     */
    @Column(name = "exchange_id")
    private Long exchangeId;

    /**
     * 1:第一级用户  2:第二级用户  3:第三级用户 4:白标
     */
    private Integer type;

    /**
     * 收取的手续费对应的币种
     */
    private String symbol;

    /**
     * 结算的币种 
     */
    @Column(name = "settle_symbol")
    private String settleSymbol;

    /**
     * 总的手续费
     */
    @Column(name = "total_cms")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal totalCms;

    /**
     * 手续费分成比例
     */
    @Column(name = "cms_rate")
    private BigDecimal cmsRate;

    /**
     * 得到的手续费分成
     * 原始数量
     */
    private BigDecimal amount;

    /**
     * 结算的币种手续费分成
     * 结算币种换算数量
     */
    @Column(name = "settle_amount")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal settleAmount;

    /**
     * 手续费币种和结算币种的转换比例 即一个手续费币种=n个结算币种
     */
    private BigDecimal rate;

    /**
     * 结算状态: 0:未结算.1:结算入金成功.
     */
    @Column(name = "settle_status")
    private Integer settleStatus;

    @Column(name = "create_time")
    private Date createTime;

    @Transient
    private String exchName;

    @Column(name = "order_time")
    private Long orderTime;

}