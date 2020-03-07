package com.github.wxiaoqi.security.common.entity.admin;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

@Data
@Table(name = "front_freeze_info")
public class FrontFreezeInfo extends BaseAdminEntity {


    @Column(name = "user_id")
    private Long userId;

    /**
     * 冻结功能 1币币交易,2-用户与用户交易,3-社区排单申购
     */
    @Column(name = "freeze_type")
    private Integer freezeType;

    /**
     * 是否冻结 0-冻结(禁用),1-启用
     */
    private Integer enable;

    /**
     * 备注
     */
    private String remark;

    /**
     * 页面返回来的参数
     */
    @Transient
    private String freezeTypeStr;

}