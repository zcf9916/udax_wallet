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
public class UDOrderRspVo implements Serializable {

	private String orderNo;

	/**
	 * 方案名称
	 */
	private String levelName;

	/**
	 * 申购金额
	 */
	@JsonSerialize( using = BigDecimalCoinSerializer.class)
	private BigDecimal lockAmount;



	private String symbol;



	/**
	 * 订单生成(匹配)时间
	 */
	@JsonSerialize( using = DateToTimeStampSerializer.class)
	private Date createTime;

	/**
	 * 申购时间
	 */
	@JsonSerialize( using = DateToTimeStampSerializer.class)
	private Date purchaseTime;


	@JsonSerialize( using = BigDecimalCoinSerializer.class)
	private BigDecimal profit;//匹配的收益

	//匹配截止时间
	@JsonSerialize( using = DateToTimeStampSerializer.class)
	private Date endTime;

}
