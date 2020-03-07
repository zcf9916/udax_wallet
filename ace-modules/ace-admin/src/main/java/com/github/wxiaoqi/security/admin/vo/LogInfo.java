package com.github.wxiaoqi.security.admin.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class LogInfo implements Serializable{
    private String menu;

    private String opt;

    private String uri;


    private Date crtTime;

    private Long  userId;

    private String crtName;

    private String crtHost;

    private Long exchangeId;

    public LogInfo(String menu, String option, String uri,  Date crtTime, Long userId, String crtName, String crtHost,Long exchangeId) {
        this.menu = menu;
        this.opt = option;
        this.uri = uri;
        this.crtTime = crtTime;
        this.userId = userId;
        this.crtName = crtName;
        this.crtHost = crtHost;
        this.exchangeId=exchangeId;
    }

    public LogInfo() {
    }

}
