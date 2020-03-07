package com.github.wxiaoqi.security.common.entity.ud;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "h_param")
@Getter
@Setter
public class HParam extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;

    private String udKey;

    private String udValue;

    /**
     * 描述
     */
    private String remark;

    @Transient
    private String exchName;

    private Long exchId;
}