package com.github.wxiaoqi.security.common.entity.merchant;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.wxiaoqi.security.common.base.BaseEntity;
import com.github.wxiaoqi.security.common.entity.admin.CfgChargeTemplate;
import com.github.wxiaoqi.security.common.entity.admin.DictData;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.util.Date;

@Getter
@Setter
public class Merchant extends BaseEntity {

    @Column(name = "user_id")
    private Long userId;
    /**
     * 商户名
     */
    @Column(name = "mch_name")
    private String mchName;

    /**
     * 1.未激活;2,正常;3,冻结;
     */
    @Column(name = "mch_status")
    private Integer mchStatus;

//    /**
//     * 预留字段
//     */
//    @Column(name = "mch_level")
//    private Integer mchLevel;
//
//    /**
//     * 法人名
//     */
//    @Column(name = "legal_person")
//    private String legalPerson;
//
//    /**
//     * 法人证件号
//     */
//    @Column(name = "id_card")
//    private String idCard;
//
//    private String email;
//
//    private String telephone;

    /**
     * 商家营业执照正面
     */
    @Column(name = "mch_license_zm")
    private String mchLicenseZm;

    /**
     * 商家营业执照反面
     */
    @Column(name = "mch_license_fm")
    private String mchLicenseFm;



//    /**
//     * 商家后台账号
//     */
//    @Column(name = "mch_manager_id")
//    private Long mchManagerId;

//    /**
//     * 商家后台密码
//     */
//    @Column(name = "mch_manager_pwd")
//    private String mchManagerPwd;

    /**
     * 商家接入API分配的商户号
     */
    @Column(name = "mch_no")
    @JsonSerialize(using= ToStringSerializer.class)
    private Long mchNo;

//    /**
//     * 密码盐值
//     */
//
//    private String salt;


    /**
     * 秘钥
     */
    @Column(name = "secret_key")
    private String secretKey;


//    /**
//     * 商户签名私钥
//     */
//    @Column(name = "private_key")
//    private String privateKey;
//
//    /**
//     * 验证签名公钥
//     */
//    @Column(name = "public_key")
//    private String publicKey;

    /**
     * 以逗号分隔的多个ip
     */
    @Column(name = "bind_address")
    private String bindAddress;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;



    //提筆結果回调地址
    @Column(name = "withdraw_callback")
    private String withdrawCallback;

    //充币結果回调地址
    @Column(name = "recharge_callback")
    private String rechargeCallback;


    @Column(name = "charge_id")
    private Long chargeId;

    @Transient
    private CfgChargeTemplate cfgChargeTemplate;

    //charge_id

    @Transient
    private DictData dictData;

    @Transient
    private FrontUser frontUser;

    /**
     * 商家待结算资产(结算时间)
     */
    private  Integer settleTime;

}