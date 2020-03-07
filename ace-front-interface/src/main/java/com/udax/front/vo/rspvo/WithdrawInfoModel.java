package com.udax.front.vo.rspvo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class WithdrawInfoModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 最小提币额
	 */
	@JsonSerialize( using = BigDecimalCoinSerializer.class)
	private BigDecimal limitWithdraw = BigDecimal.ZERO;

	
	/**
	 * 1:按固定金额,2:按比例
	 */
	private Integer chargeType;

	/**
	 * 可用余额
	 */
	@JsonSerialize( using = BigDecimalCoinSerializer.class)
	private BigDecimal availableAmount = BigDecimal.ZERO;



	/**
	 * 限制提币量
	 */
	@JsonSerialize( using = BigDecimalCoinSerializer.class)
	private BigDecimal withdrawQuota = BigDecimal.ZERO;

	/**
	 * 提现手续费
	 */
	@JsonSerialize( using = BigDecimalCoinSerializer.class)
	private BigDecimal chargeAmount = BigDecimal.ZERO;


	private Integer needTag;//是否需要標簽  0  不用   1要

	private String addressSplitParam;//地址分割参数

	private String withdrawDesp;//提现描述


}
