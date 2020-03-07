package com.github.wxiaoqi.security.common.entity.fund;

import com.github.wxiaoqi.security.common.annotation.Sign;
import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "fund_account_assert")
@Getter
@Setter
public class FundAccountAssert extends BaseEntity {


    /**
     * 用户ID
     */
    @Column(name = "user_id")
    @Sign
    private Long userId;

    /**
     * 代币代码
     */
    @Column(name = "dc_code")
    @Sign
    private String dcCode;

    /**
     * 代币总数量
     */
    @Column(name = "total_amount")
    @Sign
    private BigDecimal totalAmount;

    /**
     * 代币可用数量
     */
    @Column(name = "available_amount")
    @Sign
    private BigDecimal availableAmount;

    /**
     * 代币冻结数量
     */
    @Column(name = "freeze_amount")
    @Sign
    private BigDecimal freezeAmount;

    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 加密检验信息
     */
    private String umac;

}