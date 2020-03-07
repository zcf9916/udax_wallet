package com.github.wxiaoqi.security.common.entity.admin;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import lombok.Data;

import java.util.Date;
import javax.persistence.*;

@Data
@Table(name = "front_advert")
public class FrontAdvert extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 标题头
     */
    private String title;

    @Column(name = "exchange_id")
    private Long exchangeId;

    private Integer status;

    /**
     * 扩展链接
     */
    @Column(name = "extend_url")
    private String extendUrl;

    /**
     * 客户端类型(如pc端、app端等)
     */
    @Column(name = "client_type")
    private String clientType;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 备注信息
     */
    private String remark;
    /**
     * 广告图片地址
     */
    private String url;

    @Transient
    private WhiteExchInfo exchInfo;
    /**
     *  语言
     */
    private String languageType;

}