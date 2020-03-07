package com.github.wxiaoqi.security.common.entity.casino;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "casino_role")
@Data
public class CasinoRole extends BaseEntity {

    public enum CasinoRoleType {
        /** 副总经理 */
            DEPUTY_GENERAL_MANAGER(1),
            /** 副总裁 */
            VICE_PRESIDENT(2);


        private final Integer value;

        private CasinoRoleType(Integer value) {
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
     * 额外利润分成比例
     */
    @Column(name = "cms_rate")
    private BigDecimal cmsRate;

    /**
     * 类型 1.副总经理   2 副总裁
     */
    private Integer type;

    /**
     * 副总经理直推达标人数
     */
    @Column(name = "direct_child")
    private Integer directChild;

    /**
     * 副总经理有效达标人数
     */
    @Column(name = "all_child")
    private Integer allChild;

    private Long exchId;

    private Date createTime;

    /**
     * 级别描述
     */
    private String remark;

    /**
     * 0 禁用  1正常
     */
    private Integer enable;

    @Transient
    private String exchName;

}