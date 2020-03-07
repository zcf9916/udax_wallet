package com.github.wxiaoqi.security.common.entity.ud;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import javax.persistence.*;

@Table(name = "h_bonus_config")
@Getter
@Setter
public class HBonusConfig extends BaseAdminEntity {

    /**
     * 直接间隔代数
     */
    private Integer level;

    @Transient
    private String exchName;


    @Column(name = "exch_id")
    private Long exchId;

    /**
     * 收取利润的%  
     */
    @Column(name = "profit_rate")
    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal profitRate;

    private String remark;
}