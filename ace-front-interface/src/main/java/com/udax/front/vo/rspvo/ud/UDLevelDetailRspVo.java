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
public class UDLevelDetailRspVo implements Serializable {

	/**
	 * 申购名称
	 */
	private String name;

	/**
	 * 申购金额
	 */
	@JsonSerialize(using = BigDecimalCoinSerializer.class)
	private BigDecimal amount;


	/**
	 * 是否开放 0:关闭,1:开放
	 */
	private Integer isOpen;


	private String symbol;

	/**
	 * 下一轮的最早开始时间
	 */
	@JsonSerialize(using = DateToTimeStampSerializer.class)
	private Date earliestStartTime;

	//描述
	private String desp;


	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	//0 否  1是
	private Integer ifCurrentLevel = 0;

	@JsonSerialize(using = BigDecimalCoinSerializer.class)
	private BigDecimal amountLimit;

}
