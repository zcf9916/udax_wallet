package com.udax.front.bean;

import com.udax.front.annotation.DcCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class WithdrawModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * token地址
	 */
	@NotBlank(message="{USER_ADD_NULL}")
	@Length(min=1, max=256, message="{USER_ADD_NULL}")
	private String userAddress;

	/**
	 * 货币编码
	 */
	@NotBlank(message="{DCCODE_LENGTH}")
	@Length(min=1, max=20, message="{DCCODE_LENGTH}")
	@DcCode
	private String symbol;


	/**
	 * 提现金额
	 */
	@DecimalMin(value = "0.00000001", message = "{MERCHANT_AMOUNT_LIMIT}")
	@Digits(integer = 10,fraction = 8, message = "{MERCHANT_AMOUNT_LIMIT}")
	private BigDecimal tradeAmount;

	/**
	 * 提现手续费
	 */
	@DecimalMin(value = "0.00000001", message = "{MERCHANT_AMOUNT_LIMIT}")
	@Digits(integer = 10,fraction = 8, message = "{MERCHANT_AMOUNT_LIMIT}")
	private BigDecimal chargeAmount;

    private String mobileCode;//手机验证码


	private String emailCode;//邮箱验证码

	@Length(max = 10 , message="{MERCHANT_TAG_LENGTH}")
	private String tag; //针对XRP需要提供标签


	@Length(max = 10)
	private String protocolType;//公链类型

}
