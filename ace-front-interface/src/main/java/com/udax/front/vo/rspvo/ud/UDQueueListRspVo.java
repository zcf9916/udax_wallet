package com.udax.front.vo.rspvo.ud;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Getter
@Setter
public class UDQueueListRspVo implements Serializable {


	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 方案名称
	 */
	private String levelName;

	/**
	 * 申购金额
	 */
	private BigDecimal lockAmount;


	//利润
	@JsonSerialize( using = BigDecimalCoinSerializer.class)
    private BigDecimal profit;


	private String symbol;

	/**
	 * 申购时间
	 */
	@JsonSerialize(using = DateToTimeStampSerializer.class)
	private Date createTime;

	//手续费
	@JsonSerialize( using = BigDecimalCoinSerializer.class)
    private BigDecimal charge;

	/**
	 * 0.已结算  1.排队中  2.匹配中
	 */
	private Integer status;




}
