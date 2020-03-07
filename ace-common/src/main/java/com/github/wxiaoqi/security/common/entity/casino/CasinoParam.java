package com.github.wxiaoqi.security.common.entity.casino;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Data;

import javax.persistence.*;
@Data
@Table(name = "casino_param")
public class CasinoParam extends BaseEntity {

    /**
     * 参数key
     */
    private String casinoKey;

    /**
     * 参数value
     */
    private String casinoValue;


    private String remark;

    /**
     * 白标ID
     */
    @Column(name = "exch_id")
    private Long exchId;

    @Transient
    private String exchName;

}