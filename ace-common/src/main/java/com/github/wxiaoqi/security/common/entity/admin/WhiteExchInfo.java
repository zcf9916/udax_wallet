package com.github.wxiaoqi.security.common.entity.admin;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;

import lombok.Getter;
import lombok.Setter;

@Table(name = "white_exch_info")
@Getter
@Setter
public class WhiteExchInfo extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 白标名称
     */
    @Column(name = "white_name")
    private String whiteName;

    /**
     * 语言
     */
    private String language;

    /**
     * 结算币种
     */
    @Column(name = "lt_code")
    private String ltCode;

    /**
     * 备注
     */
    @Column(name = "remark_")
    private String remark;

    /**
     * 域名
     */
    @Column(name = "domain_name")
    private String domainName;

    /**
     * 白标所属时区
     */
    @Column(name = "own_time_zone")
    private String ownTimeZone;

    /**
     * 状态 0: 默认启用 1 :禁用
     */
    private Integer status;

    @Column(name = "group_id")
    private Long groupId;
    /**
     * 报价源货币 如:BTC,BCH,LTC,UDT'
     */
    @Column(name = "src_symbol_id")
    private String srcSymbolId;

    @Transient
    private String[] srcSymbol;
    /***
     * 报价目标货币货币 如:CCASH,等
     */
    @Column(name = "dst_symbol")
    private String dstSymbol;

    private Integer settleTime;


    //注册类型:1:全部 2:只允许手机注册 3:只允许邮箱注册
    private Integer registerType;
}
