package com.github.wxiaoqi.security.common.base;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Data;

@Data
public class BaseAdminEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private Long id;
    @Column(name = "crt_time")
    private Date crtTime;
    @Column(name = "crt_name")
    private String crtName;
    @Column(name = "upd_time")
    private Date updTime;
    @Column(name = "upd_name")
    private String updName;
}
