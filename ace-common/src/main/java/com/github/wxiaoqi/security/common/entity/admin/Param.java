package com.github.wxiaoqi.security.common.entity.admin;

import javax.persistence.Column;
import javax.persistence.Table;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
@Table(name = "base_param")
public class Param extends BaseAdminEntity {

    /**
     * 参数键名
     */
    @Column(name = "param_key")
    private String paramKey;

    /**
     * 参数键值
     */
    @Column(name = "param_value")
    private String paramValue;


    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    @Column(name = "status")
    private Integer status;


}