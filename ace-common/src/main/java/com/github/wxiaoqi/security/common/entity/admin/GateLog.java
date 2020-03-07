package com.github.wxiaoqi.security.common.entity.admin;

import javax.persistence.Table;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Table(name = "gate_log")
@Getter
@Setter
public class GateLog  implements Serializable {
    private Long id;

    private String menu;

    private String opt;

    private String uri;


    private Date crtTime;

    private Long  userId;

    private String crtName;

    private String crtHost;

    private Long exchangeId;
}