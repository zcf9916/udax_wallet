package com.github.wxiaoqi.security.common.entity.ud;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DateUtils;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

@Table(name = "h_queue")
@Getter
@Setter
public class HQueue extends BaseEntity {

    /**
     * 冻结资产
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 订单号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 方案名称
     */
    @Column(name = "level_name")
    private String levelName;

    /**
     * 申购的方案Id
     */
    @Column(name = "level_id")
    private Long levelId;

    /**
     * 锁定金额
     */
    @Column(name = "lock_amount")
    private BigDecimal lockAmount;

    private String symbol;

    /**
     * 用户上一轮排队的订单号
     */
    @Column(name = "last_order_no")
    private String lastOrderNo;

    /**
     * 排队时间
     */
    @Column(name = "create_time")
    private Date createTime;
    /**
     * 0.不在队列中 1.排队中
     */
    private Integer status;
    /**
     * 申购人
     */
    @Transient
    private String userName;
    /**
     * 客户排单最长等待期限(天)
     */
    @Transient
    private Integer waitTime;


    @Transient
    private HOrderDetail orderDetail;

    @Transient
    private String exchName;//白标名称


    /**
     * 剩余还需等待时长
     */
    @Transient
    private HashMap<String,Long> remainWaitingTime;

    public HashMap<String,Long> getRemainWaitingTime() {

        long day=0l,hour=0l,minute=0l,second=0l;//剩余天数，小时，分，秒
        remainWaitingTime = InstanceUtil.newHashMap();

        if(this.waitTime != null && this.createTime != null){
            Long  between = (DateUtils.addDays(createTime,waitTime).getTime() - new Date().getTime())/1000;//除以1000是为了转换成秒
            if(between < 0 || this.status == 0){ //status为0,表示已经匹配，无需等待
                between = 0l;
            }
             day=between/(24*3600);

             hour=between%(24*3600)/3600;

             minute=between%3600/60;

             second=between%60/60;
        }
        remainWaitingTime.put("day",day);
        remainWaitingTime.put("hour",hour);
        remainWaitingTime.put("minute",minute);
        remainWaitingTime.put("second",second);

        return remainWaitingTime;
    }

    public void setRemainWaitingTime(HashMap<String,Long> remainWaitingTime) {
        this.remainWaitingTime = remainWaitingTime;
    }



}