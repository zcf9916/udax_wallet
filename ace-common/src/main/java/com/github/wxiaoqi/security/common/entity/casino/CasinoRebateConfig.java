package com.github.wxiaoqi.security.common.entity.casino;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
@Data
@Table(name = "casino_rebate_config")
public class CasinoRebateConfig extends BaseEntity {

    public enum CasinoRebateConfigType {
        /** 直推用户 */
        Direct_push_user(1),
        /** 间接推荐人 */
        INDIRECT_RECOMMENDER(2),
        /** 白标分成 */
        WHITE_LABEL_DIVISION(3);

        private final Integer value;

        private CasinoRebateConfigType(Integer value) {
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
     * 分成比例
     */
    @Column(name = "cms_rate")
    private BigDecimal cmsRate;

    /**
     * 类型 1:直推用户  2:间接推荐人  3:白标分成
     */
    private Integer type;

    /**
     * 白标ID
     */
    @Column(name = "exch_id")
    private Long exchId;



    /**
     * 级别描述
     */
    private String remark;

    /**
     * 0 禁用  1正常
     */
    private Integer enable;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;


    @Transient
    private String exchName;


}