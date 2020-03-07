package com.github.wxiaoqi.security.common.entity.admin;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import lombok.Data;

import java.util.Date;
import javax.persistence.*;
@Data
@Table(name = "front_notice")
public class FrontNotice  extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 公告标题
     */
    @Column(name = "notice_title")
    private String noticeTitle;

    /**
     * 公告级别（1:普通，2:重要，3:紧急）
     */
    @Column(name = "notice_level")
    private Integer noticeLevel;

    /**
     * 发布状态(0:发布，1:隐藏)
     */
    private Integer status;

    /**
     * 关联交易所
     */
    @Column(name = "exchange_id")
    private Long exchangeId;


    /**
     * 客户端类型(如pc端、app端等)
     */
    @Column(name = "client_type")
    private Integer clientType;

    /**
     * 发布的内容信息（以文本编辑器编辑）
     */
    private String content;

    private String remark;

    @Transient
    private WhiteExchInfo exchInfo;

    private String languageType;
}