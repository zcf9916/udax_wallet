package com.github.wxiaoqi.security.common.entity.casino;

import com.github.wxiaoqi.security.common.base.BaseEntity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;


/**
 * 会员开工模式表
 */


@Data
@Table(name = "casino_method_config")
public class CasinoMethodConfig extends BaseEntity {

    public enum RoleType {
        DIRECT(1),//直接推荐人
        INDIRECT(2),//间接推荐人
        WHITE(3),//白标
        VP(4),//富总经理
        GM(5),//副总裁
        SELF(6);//自身比例


        private final Integer value;

        private RoleType(Integer value) {
            this.value = value;
        }

        public Integer value() {
            return this.value;
        }

        public String toString() {
            return this.value.toString();
        }
    }




    public enum CasinoMethodConfigType {
        /** 与平台按比例分成 */
        PROPORTIONATE(1),
        /** 会员与客户对赌 */
        ON_GAMBLING(2);

        private final Integer value;

        private CasinoMethodConfigType(Integer value) {
            this.value = value;
        }

        public Integer value() {
            return this.value;
        }

        public String toString() {
            return this.value.toString();
        }
    }

    /**
     * 会员分成比例
     */
    @Column(name = "user_cms_rate")
        private BigDecimal userCmsRate;

    /**
     * 类型 1:与平台按比例分 2会员与客户对赌
     */
    private Integer type;

    /**
     * 平台分成比例
     */
    @Column(name = "platform_cms_rate")
    private BigDecimal platformCmsRate;

    /**
     * 类型为2时,收取的固定服务费
     */
    @Column(name = "fixed_value")
    private BigDecimal fixedValue;

    /**
     * 白标ID
     */
    @Column(name = "exch_id")
    private Long exchId;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 0 禁用  1正常
     */
    private Integer enable;

    /**
     * 级别描述
     */
    private String remark;


    //直推用户享受的比例
    private BigDecimal directUserRate;

    //间接用户享受的比例
    private BigDecimal indirectUserRate;

    @Transient
    private String exchName;

}