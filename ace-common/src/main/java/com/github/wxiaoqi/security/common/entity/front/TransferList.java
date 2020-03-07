package com.github.wxiaoqi.security.common.entity.front;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.github.wxiaoqi.security.common.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Table(name = "transfer_list")
@Getter
@Setter
public class TransferList extends BaseEntity {

    /**
     * 转账用户Id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 收款用户Id
     */
    @Column(name = "receive_user_id")
    private Long receiveUserId;

    /**
     * 收款用户名
     */
    @Column(name = "receive_user_name")
    private String receiveUserName;

    /**
     * 记录生成时间
     */
    @Column(name = "create_time")
    private Date createTime;

    @Transient
    private FrontUserInfo frontUserInfo;

}