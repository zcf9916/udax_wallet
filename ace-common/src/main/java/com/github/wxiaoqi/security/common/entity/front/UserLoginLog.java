package com.github.wxiaoqi.security.common.entity.front;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "user_login_log")
public class UserLoginLog {
    @Id
    private Long id;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 登陆方式(1:web、2:android、3:ios)
     */
    @Column(name = "login_type")
    private Integer loginType;

    /**
     * 登陆地址
     */
    @Column(name = "login_ip")
    private String loginIp;

    /**
     * 登陆状态
     */
    @Column(name = "login_status")
    private Integer loginStatus;

    @Column(name = "create_time")
    private Date createTime;


}