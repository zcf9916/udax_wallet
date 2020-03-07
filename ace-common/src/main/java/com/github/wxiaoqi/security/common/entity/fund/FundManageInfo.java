package com.github.wxiaoqi.security.common.entity.fund;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Table(name = "fund_manage_info")
@Getter
@Setter
public class FundManageInfo extends BaseEntity {

    @Column(name = "exchange_id")
    //白标ID
    private Long exchangeId;


    /**
     * 管理人信息
     */
    @Column(name = "manage_info")
    private String manageInfo;

    /**
     * 其它相关信息
     */
    private String idea;

    /**
     * 团队介绍
     */
    @Column(name = "team_info")
    private String teamInfo;


    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 0.禁用;1启用
     */
    private Integer enable;

}