package com.github.wxiaoqi.security.common.entity.ud;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.base.BaseEntity;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "h_unlock_detail")
@Data
public class HUnLockDetail extends BaseEntity {

    @Column(name = "user_id")
    private Long userId;

    /**
     * 解锁金额
     */
    @JsonSerialize(using = BigDecimalCoinSerializer.class)

    private BigDecimal amount;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "order_no")
    private String orderNo;

    private Integer type;

    @Transient
    private FrontUser frontUser;
}