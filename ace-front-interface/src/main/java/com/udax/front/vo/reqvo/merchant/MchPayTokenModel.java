package com.udax.front.vo.reqvo.merchant;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;

import com.udax.front.annotation.DcCode;

import lombok.Data;

@Data
public class MchPayTokenModel {
	/**
	 * 支付币种
	 */
	@DcCode
	private String symbol;

	/**
	 * 支付数量
	 */
	@DecimalMin(value = "0.00000001", message = "{MERCHANT_AMOUNT_LIMIT}")
	@Digits(integer = 10,fraction = 8, message = "{MERCHANT_AMOUNT_LIMIT}")
	private BigDecimal amount;
}
