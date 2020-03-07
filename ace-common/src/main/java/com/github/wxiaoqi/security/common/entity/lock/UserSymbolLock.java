package com.github.wxiaoqi.security.common.entity.lock;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
@Data
@Table(name = "user_symbol_lock")
public class UserSymbolLock extends BaseEntity {

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 币种
     */
    private String symbol;

    /**
     * 总数量
     */
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 已释放数量
     */
    @Column(name = "freed_amount")
    private BigDecimal freedAmount;

    /**
     * 已释放次数
     */
    @Column(name = "freed_time")
    private Integer freedTime;

    /**
     * 是否释放完成: 0:否 1:是
     */
    @Column(name = "is_freed")
    private Integer isFreed;

    /**
     * 释放比例
     */
    @Column(name = "freed_scale")
    private BigDecimal freedScale;

    /**
     * 总释放次数
     */
    @Column(name = "total_time")
    private Integer totalTime;

    /**
     * 是否生成释放明细 0:否 1:是
     */
    @Column(name = "has_detail")
    private Integer hasDetail;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    private Integer version;

    private Integer freedCycle; //释放周期

    @Transient
    private FrontUser frontUser;

}