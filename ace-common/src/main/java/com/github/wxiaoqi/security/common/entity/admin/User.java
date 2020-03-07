package com.github.wxiaoqi.security.common.entity.admin;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;

import lombok.Getter;
import lombok.Setter;

@Table(name = "base_user")
@Setter
@Getter
public class User extends BaseAdminEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    private String name;

    private String birthday;

    private String address;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column(name = "tel_phone")
    private String telPhone;

    private String email;

    private String sex;

    private String type;

    private String status;

    private String description;



    /**
     * 所属区域ID
     */
    @Column(name = "dept_id")
    private Long deptId;

    /**
     * 部门名称
     */
    @Column(name = "dept_name")
    private String deptName;

    /**
     * 关联角色主键
     */
    private Long groupId;
    /**
     * 关联交易所
     */
    private Long topParentId;
    /**
     * 用户关联的角色
     */
    @Transient
    private Group group;
}