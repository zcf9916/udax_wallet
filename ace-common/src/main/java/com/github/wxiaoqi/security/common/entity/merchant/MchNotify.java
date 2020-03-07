package com.github.wxiaoqi.security.common.entity.merchant;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Table(name = "mch_notify")
@Getter
@Setter
public class MchNotify extends BaseEntity {


    /**
     * 商户id
     */
    @Column(name = "mch_id")
    private Long mchId;

    /**
     * 校验id
     */
    @Column(name = "notify_id")
    private Long notifyId;

    /**
     * 1.预下单标示  2.下单成功通知标示  3.充值成功通知标示  4.提现成功通知标示 5.退款成功通知标示
     */
    @Column(name = "type")
    private Integer type;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "create_time")
    private Date createTime;

    //回调次数
    private Integer count;

    //0 失败  1 成功
    private Integer status;

    @Column(name = "callback_url")
    private String callbackUrl;

    @Column(name = "notify_str")
    private String notifyStr;

}