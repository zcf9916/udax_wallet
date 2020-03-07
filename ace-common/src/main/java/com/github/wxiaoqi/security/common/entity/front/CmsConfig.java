package com.github.wxiaoqi.security.common.entity.front;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import javax.persistence.*;

@Data
@Table(name = "cms_config")
public class CmsConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 分成比例
     */
    @Column(name = "cms_rate")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal cmsRate;

    /**
     * 类型 1:直推用户  2:二代用户  3:三代用户(1+2+3 =100%)
     * 4:推荐用户手续费分成  5:白标分成比例(4+5=100%)
     */
    private Integer type;

    /**
     * 白标ID
     */
    @Column(name = "exch_id")
    private Long exchId;

    /**
     * 级别描述
     */
    private String remark;


    private Long parentId;

    /**
     * 0 不可用  1 可用
     */
    private Integer enable;

    @Column(name = "create_time")
    private Date createTime;

    @Transient
    private String exchName;


    @Transient
    private ArrayList children;



}