package com.udax.front.vo.rspvo.ud;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Getter
@Setter
public class UDCurrentQueueRspVo implements Serializable {


	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 方案名称
	 */
	private String levelName;

	/**
	 * 锁定金额
	 */
	private BigDecimal lockAmount;

	private String symbol;

	/**
	 * 排队时间
	 */
	@JsonSerialize(using = DateToTimeStampSerializer.class)
	private Date createTime;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long queueNum;//队列排队人数


	private int currentPosition;//队列中的位置





}
