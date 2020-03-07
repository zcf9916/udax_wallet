package com.github.wxiaoqi.security.common.entity.ud;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import com.github.wxiaoqi.security.common.constant.Constants;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
@Data
@Table(name = "h_node_award")
public class HNodeAward extends BaseAdminEntity {


    /**
     * 按利润的百分比作为分成基数
     */
    @Column(name = "base_profit_rate")
    private BigDecimal baseProfitRate;

    /**
     * 达到节点等级需要的有效用户数量
     */
    @Column(name = "child_num")
    private Integer childNum;

    /**
     * 节点奖励抽取的比例
     */
    private BigDecimal rate;

    /**
     * 全球利润抽取的比例
     */
    @Column(name = "global_rate")
    private BigDecimal globalRate;

    /**
     *  子节点的总投资额
     */

    private BigDecimal childInvest;

    /**
     * 交易所id
     */
    private Long exchId;

    @Transient
    private String exchName;

    //币种
    private String symbol ;

    /**
     * 节点描述
     */
    private String remark;


}