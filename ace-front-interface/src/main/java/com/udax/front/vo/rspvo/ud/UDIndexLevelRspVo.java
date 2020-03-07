package com.udax.front.vo.rspvo.ud;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
public class UDIndexLevelRspVo implements Serializable {

	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	/**
	 * 申购名称
	 */
	private String name;
	/**
	 * 申购金额
	 */
	@JsonSerialize( using = BigDecimalCoinSerializer.class)
	private BigDecimal amount;
	/**
	 * 方案是否开放 0:关闭,1:开放
	 */
	private Integer isOpen;

	@JsonSerialize( using = BigDecimalCoinSerializer.class)
	private BigDecimal amountLimit;


	private String symbol;

}
