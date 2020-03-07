package com.github.wxiaoqi.security.common.entity.ud;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "h_user_info")
@Getter
@Setter
public class HUserInfo extends BaseEntity {

    @Column(name = "user_id")
    private Long userId;

    /**
     * 用户状态(1.可用  0锁定)
     */
    private Integer status;

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


    //子节点的总投资额
    @Column(name = "child_invest")
    private BigDecimal childInvest;


    @Column(name = "add_amount")
    private BigDecimal addAmount;


    @Column(name = "add_node_amount")
    private BigDecimal addNodeAmount;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    //0.非有效用户   1.有效用户(参与队列锁定币  才能算有效用户)
    @Column(name = "is_valid")
    private Integer isValid;

    @Transient
    private FrontUser parentUser;

    /**
     * 用户信息
     */
    @Transient
    private FrontUser user;


    /**
     * 顶级用户
     */
    @Transient
    private FrontUser topUser;

    //0.不复投   1.复投
    @Column(name = "auto_repeat")
    private Integer autoRepeat;


    @Column(name = "user_level")
    private Integer userLevel;
}