package com.github.wxiaoqi.security.common.entity.ifr;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "ifr_plan")
@Data
public class IfrPlan implements Serializable {
    @Id
    private Long id;

    /**
     * 方案名称
     */
    private String name;

    /**
     * 时间周期
     */
    @Column(name = "time_period")
    private Integer timePeriod;

    /**
     * 利息
     */
    private BigDecimal interest;

    /**
     * 描述信息
     */
    private String desp;

    private Integer status;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

}