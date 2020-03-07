package com.udax.front.vo.rspvo.merchant;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class MchSymbolInfoRspModel implements Serializable {


	/**
	 * 代币编码
	 */
	private String symbol;


    //	是否允许充币，0允许，1不允许
	private Integer canRecharge;

	//是否允许提币，0允许，1不允许
	private Integer canWithdraw;


	/**
	 * 最小提币数量
	 */
	@JsonSerialize(using= BigDecimalCoinSerializer.class)
	private BigDecimal minWithdrawAmount;

	/**
	 * 最大提币数量
	 */
	@JsonSerialize(using= BigDecimalCoinSerializer.class)
	private BigDecimal maxWithdrawAmount;

	/**
	 * 最小充币数量
	 */
	@JsonSerialize(using= BigDecimalCoinSerializer.class)
	private BigDecimal minRechargeAmount;
	/**
	 * 当日最大提币数量
	 */
	@JsonSerialize(using= BigDecimalCoinSerializer.class)
	private BigDecimal maxWithdrawDay;

}
