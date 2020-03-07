package com.github.wxiaoqi.security.common.entity.admin;

import javax.persistence.Column;
import javax.persistence.Table;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;

import lombok.Data;
@Data
@Table(name = "base_dict_type")
public class DictType extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 字典名称
     */
    @Column(name = "dict_name")
    private String dictName;

    /**
     * 字典类型
     */
    @Column(name = "dict_type")
    private String dictType;

    /**
     * 1.正常;2.停用
     */
    private Integer status;


    /**
     * 备注信息
     */
    private String remark;


}