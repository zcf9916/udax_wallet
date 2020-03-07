package com.udax.front.vo.reqvo.ifr;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class IfrPayOrderModel implements Serializable{
	
	/**
	 * 活动id
	 */
	@NotBlank(message = "{CAMPAIGNID_ERROR}")
	private String campaignId;
	
	/**
	 * 邀请人
	 */
	@NotBlank(message = "{VISITCODE_LENGTH}")
	private String referralId;
	
	/**
	 * 方案id
	 */
	@NotBlank(message = "{PLAN_ERROR}")
	private String planId;

	@NotNull(message = "{IFR_AMOUNT_LIMIT}")
	@DecimalMin(value = "1", message = "{IFR_AMOUNT_LIMIT}")
	@Digits(integer = 10,fraction = 0, message = "{IFR_AMOUNT_LIMIT}")
	private BigDecimal amount;

	/**
	 * 币种符号
	 */
	@NotBlank(message = "{CURRENCY_ERROR}")
	private String currency;

//	/**
//	 * 所属国家
//	 */
//	@NotBlank(message="{ACCOUNT_LENGTH}")
//	private String bankCode;

	
}
