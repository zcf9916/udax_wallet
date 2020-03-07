package com.udax.front.vo.rspvo.fund;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class FundProductInfoVo implements Serializable {


    @JsonSerialize(using=ToStringSerializer.class)
    private Long fundId;  //基金产品id

    private String fundName;  //基金名称



    private String strategyType; //1.稳健型; 2.进取型



    /**
     * 管理人信息
     */
    private String manageInfo;

    /**
     * 其它相关信息
     */
    private String idea;

    /**
     * 团队介绍
     */
    private String teamInfo;

    private BigDecimal subscripeRate;  //'认购费率'

   // private BigDecimal currOneWorth;//当前净值


//    private Integer investTarget;  //投资标的 1.现货;2.期货;3.期权

    private String dcCode;

    private BigDecimal expectScale;  //'预期规模'

    private BigDecimal expectProfit; //'预期收益'

    private BigDecimal minBuyNum; //最小申购数量

    private BigDecimal actualScale; //实际规模

    private BigDecimal proportion;//投资者分成比例
    private Integer overRange;//1.可以超额认购; 2.不允许超额认购
    private Integer status;//1.已发布;2.募集中;3.申购结束;4.已启动;5.清盘中;6.已清盘

    @JsonSerialize(using = DateToTimeStampSerializer.class)
    private Date publishTime;//发布时间
    @JsonSerialize(using = DateToTimeStampSerializer.class)
    private Date buyStarttime;//申购开始时间
    @JsonSerialize(using = DateToTimeStampSerializer.class)
    private Date buyEndtime;//申购结束时间
    @JsonSerialize(using = DateToTimeStampSerializer.class)
    private Date cycleStarttime;//锁定开始时间

    @JsonSerialize(using = DateToTimeStampSerializer.class)
    private Date cycleEndtime;//锁定结束时间

    @JsonSerialize(using = DateToTimeStampSerializer.class)
    private Date clearTime;//清盘时间

    private long lockDate;//锁定时间

    private BigDecimal rate;//规模比例




}
