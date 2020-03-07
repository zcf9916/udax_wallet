package com.github.wxiaoqi.security.common.entity.admin;

import javax.persistence.Column;
import javax.persistence.Table;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;

import lombok.Data;
@Data
@Table(name = "base_dict_data")
public class DictData extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 字典类型ID
     */
    @Column(name = "dict_id")
    private String dictId;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 字典标签
     */
    @Column(name = "dict_label")
    private String dictLabel;

    /**
     * 字典键值
     */
    @Column(name = "dict_value")
    private String dictValue;

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
     * 语言类型
     */
    private String languageType;

    /**
     * 备注信息
     */
    private String remark;

}