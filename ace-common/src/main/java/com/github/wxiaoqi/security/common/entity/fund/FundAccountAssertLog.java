package com.github.wxiaoqi.security.common.entity.fund;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "fund_account_assert_log")
@Getter
@Setter
public class FundAccountAssertLog  extends BaseEntity {

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 基金产品id
     */
    @Column(name = "fund_id")
    private Long fundId;

    /**
     * 代币代码
     */
    @Column(name = "dc_code")
    private String dcCode;

    /**
     * 0 未知 .1 申购冻结 2.申购解冻 3. 申购手续费 4. 清盘收益增减 6.转出到币币账户 6.从币币账户转入
     */
    @Column(name = "change_type")
    private Integer changeType;


    /**
     * 变更数量
     */
    @Column(name = "change_amount")
    private BigDecimal changeAmount;

    @Column(name = "update_time")
    private Date updateTime;


    @Column(name = "trans_no")
    private String transNo;


    private String remark;
}