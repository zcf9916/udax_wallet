package com.github.wxiaoqi.security.common.entity.front;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.github.wxiaoqi.security.common.base.BaseEntity;

import com.github.wxiaoqi.security.common.entity.casino.CasinoUserInfo;
import com.github.wxiaoqi.security.common.entity.ud.HUserInfo;
import com.jcraft.jsch.UserInfo;
import lombok.Data;

@Table(name = "front_user")
@Data
public class FrontUser extends BaseEntity {

    @Column(name = "user_name")
    private String userName;

    /**
     * 1,普通用户;2,商家;
     */
    @Column(name = "user_type")
    private Integer userType;

    @Column(name = "user_level")
    private Integer userLevel;

    /**
     * 1.未激活;2,正常;3,冻结;
     */
    @Column(name = "user_status")
    private Integer userStatus;

    @Column(name = "user_pwd")
    private String userPwd;

    @Column(name = "trade_pwd")
    private String tradePwd;

    /**
     * 盐值
     */
    private String salt;

    /**
     * 用户唯一ID
     */
    private String uid;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户手机号
     */
    private String mobile;

    @Column(name = "login_err_times")
    private Integer loginErrTimes;

    /**
     * 登录IP
     */
    @Column(name = "login_ip")
    private String loginIp;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    private String remark;


    @Transient
    private FrontUserInfo userInfo;

    @Transient
    private HUserInfo hUserInfo;


    @Transient
    private CasinoUserInfo casinoUserInfo;
}