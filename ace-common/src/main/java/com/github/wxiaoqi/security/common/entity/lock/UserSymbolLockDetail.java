package com.github.wxiaoqi.security.common.entity.lock;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Data
@Table(name = "user_symbol_lock_detail")
public class UserSymbolLockDetail extends BaseEntity {


    @Column(name = "user_id")
    private Long userId;

    @Column(name = "lock_id")
    private Long lockId;

    /**
     * 释放数量
     */
    @Column(name = "free_amount")
    private BigDecimal freeAmount;

    private String symbol;

    /**
     * 释放时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "create_time")
    private Date createTime;

    /**
     * 是否已释放  0 否  1是
     */
    @Column(name = "is_free")
    private Integer isFree;

    /**
     * 1 自动释放   2  手动释放
     */
    @Column(name = "free_type")
    private Integer freeType;

    /**
     * 手动释放人
     */
    @Column(name = "free_by")
    private String freeBy;

    private Integer version;

    @Transient
    private FrontUser frontUser;

}