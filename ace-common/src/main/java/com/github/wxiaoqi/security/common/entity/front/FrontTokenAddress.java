package com.github.wxiaoqi.security.common.entity.front;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.github.wxiaoqi.security.common.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Table(name = "front_token_address")
public class FrontTokenAddress extends BaseEntity {

    /**
     * token地址
     */
    @Column(name = "user_address")
    private String userAddress;

    /**
     * 用户ID，用户发起充值时分配
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 货币编码
     */
    private String symbol;

    @Column(name = "create_time")
    private Date createTime;
    
    /**
     * 是否启用0:未启用 1：已启用
     */
    private Integer enable;
    
    /**
     * 商户用户ID
     */
    @Column(name = "merchant_user")
    private String merchantUser;

    /**
     * 地址标签，针对XRP进行处理
     */
    @Transient
    private String tag;

    //1.普通用戶 2.商户用戶充值
    private Integer type;

    //白标标识
    private String proxyCode;
}