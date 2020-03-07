package com.github.wxiaoqi.security.common.entity.front;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Table(name = "front_withdraw_add")
@Getter
@Setter
public class FrontWithdrawAdd extends BaseEntity {


    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 提币地址
     */
    @Column(name = "withdraw_add")
    private String withdrawAdd;

    /**
     * 币种名称
     */
    private String symbol;

    /**
     * 1.普通提现  2.商户提现
     */
    private Integer type;

    /**
     * 地址描述
     */
    private String remark;

    /**
     * 0.失效 1。有效
     */
    private Integer enable;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;
}