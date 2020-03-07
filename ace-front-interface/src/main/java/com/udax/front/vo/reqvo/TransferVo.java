package com.udax.front.vo.reqvo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.udax.front.annotation.DcCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;

@Getter
@Setter
public class TransferVo implements Serializable {
	/**
	 * 转换币种
	 */
	@DcCode
	private String symbol;
	
	/**
	 * 目标币种
	 */
	@DcCode
	private String targetSymbol;
	
//	/**
//	 * 成交的价格
//	 */
//	private BigDecimal tradePrice;
//
	/**
	 * 兑换数量（需兑换的币种）
	 */
	@DecimalMin(value = "0.00000001", message = "{MERCHANT_AMOUNT_LIMIT}")
	@Digits(integer = 10,fraction = 8, message = "{MERCHANT_AMOUNT_LIMIT}")
	private BigDecimal transferAmount;

//	/**
//	 * 兑换所产生的手续费
//	 */
//	private BigDecimal chargeAmount;
//
//	/**
//	 * 得到的目标币数量
//	 */
//	private BigDecimal exchangeAmount;
}
