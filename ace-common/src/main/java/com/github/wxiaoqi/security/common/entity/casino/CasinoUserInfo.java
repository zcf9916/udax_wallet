package com.github.wxiaoqi.security.common.entity.casino;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
@Data
@Table(name = "casino_user_info")
public class CasinoUserInfo extends BaseEntity {

    //结算类型
    public static enum SettleType{
        RATIO(0),//比例分成
        BET(1);//对赌
        private Integer value;
        public Integer value(){return value;}
        SettleType(Integer value){
            this.value = value;
        }
    }

    //角色类型
    public static enum CasinoUserInfoType{
        NORMAL(0),//普通会员
        CREAM(1),//精英会员
        GM(2),//副总经理
        VP(3);//副总裁
        private Integer value;
        public Integer value(){return value;}
        CasinoUserInfoType(Integer value){
            this.value = value;
        }
    }



    /**
     * 赌场账户
     */
    @Column(name = "casino_name")
    private String casinoName;

    /**
     * 结算方式  0 与平台按比例分成 1 会员与客户对赌
     */
    @Column(name = "settle_type")
    private Integer settleType;

    @Column(name = "user_id")
    private Long userId;

    /**
     * 邀请人推荐码
     */
    @Column(name = "recommond_code")
    private String recommondCode;

    /**
     * 邀请人ID
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 最上级的用户ID
     */
    @Column(name = "top_id")
    private Long topId;

    /**
     * 邀请码
     */
    @Column(name = "visit_code")
    private String visitCode;

    /**
     * 白标ID
     */
    @Column(name = "exchange_id")
    private Long exchangeId;

    /**
     * 属于团队中的第几代
     */
    private Integer level;

    /**
     * 直推有效用户
     */
    @Column(name = "direct_child")
    private Integer directChild;

    /**
     * 所有有效用户
     */
    @Column(name = "all_child")
    private Integer allChild;

    /**
     * 总结算资金量(冻结的不算  一定要结算了之后才算有效)
     */
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * ValidType
     */
    @Column(name = "is_valid")
    private Integer isValid;

    /**
     * 0 未激活用户   1 精英用户   2 副总经理  3  副总裁
     */
    private Integer type;

    /**
     * 0 不能享有收益   1 可以享有收益
     */
    private Integer status;


    @Transient
    private FrontUser frontUser;

}