package com.github.wxiaoqi.security.common.entity.admin;

import com.github.wxiaoqi.security.common.entity.lock.UserSymbolLock;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Transient;
import java.util.List;

/**
 * 前台用户信息管理
 */
@Getter
@Setter
public class FrontUserManager {
    private Long id;

    /**
     * 用户名
     */
    private String username;
    /**
     * 用户姓名
     */
    private String userRealName;
    /**
     * 用户手机号
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 国家
     */
    private String country;
    /**
     * 国家编号
     */
    private String countryCode;
    /**
     * 用户类型
     */
    private String userType;

    /**
     * 实名认证状态
     */
    private String isValid;
    /**
     * 用户名
     */
    private String createTime;
    /**
     * 商户状态
     */
    private String mchStatus ="-1";//默认0

    @Transient
    private List<String> freezeTypeList;//被冻结的用户功能值集合

    private String freezeTypeName;//被冻结的用户功能名字

    //用户充值冻结集合
    @Transient
    private List<UserSymbolLock> userLocks;
}