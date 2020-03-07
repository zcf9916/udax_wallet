package com.udax.front.vo.rspvo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@SuppressWarnings("serial")
@Getter
@Setter
public class TransRecordVo implements Serializable{

    /**
     * 订单号
     */
    private String orderNo;



    /**
     * 转账用户名
     */
    private String userName;


    /**
     * 收款用户名
     */
    private String receiveUserName;

    /**
     * 代币
     */
    private String symbol;

    /**
     * 代币数量
     */
    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal amount;


    /**
     *  手续费数量
     */
    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal chargeAmount;



    /**
     *  实际到账数量
     */
    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal arrivalAmount;


    /**
     * 记录生成时间
     */
    @JsonSerialize(using = DateToTimeStampSerializer.class)
    private Date createTime;

    /**
     * 完成时间
     */
    @JsonSerialize(using = DateToTimeStampSerializer.class)
    private Date updateTime;
    /**
     * 转账备注
     */
    private String remark;

}
