package com.github.wxiaoqi.security.common.entity.admin;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import lombok.Data;

import java.util.Date;
import javax.persistence.*;
@Data
@Table(name = "base_version")
public class BaseVersion extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 版本号
     */
    @Column(name = "version_code")
    private String versionCode;

    /**
     * 版本下载url
     */
    private String url;

    /**
     * 版本改动内容
     */
    private String content;

    /**
     * 版本标题
     */
    private String title;

    /**
     * 是否强制更新
     */
    @Column(name = "update_install")
    private Integer updateInstall;

    /**
     * 版本渠道（系统枚举 android or ios）
     */
    @Column(name = "version_channel")
    private Integer versionChannel;

    private Long exchId;

    @Transient
    private WhiteExchInfo exchInfo;

}