package com.github.wxiaoqi.security.common.entity.admin;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = "front_country")
@Getter
@Setter
public class FrontCountry extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 国家编码(china)
     */
    private String code;

    /**
     * 国家名称
     */
    private String name;

    /**
     * 国家编号(0086)
     */
    @Column(name = "country_code")
    private String countryCode;


    @Column(name = "country_image")
    private String countryImage;
    /**
     * 1:正常;2禁用
     */

    private Integer status;


    /**
     *
     */
    private Integer sort;

}