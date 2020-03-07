package com.github.wxiaoqi.security.common.entity.common;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import lombok.Data;

@Table(name = "base_sms")
@Data
public class BaseSms extends BaseAdminEntity {
    @Id
    private Long id;

    /**
     * 平台编号
     */
    @Column(name = "biz_id")
    private String bizId;

    /**
     * 类型
     */
    private String type;

    /**
     * 接收短信号码
     */
    private String phone;

    /**
     * 短信内容
     */
    private String content;

    /**
     * 发送状态0-未成功 1-成功
     */
    @Column(name = "send_state")
    private String sendState;
}