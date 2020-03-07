package com.udax.front.vo.reqvo.fund;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 申购基金
 */
@SuppressWarnings("serial")
@Getter
@Setter
public class BuyFundModel{

	@NotNull(message = "{ID_IS_NULL}")
	@Min(value = 1, message = "{ID_IS_NULL}")
	private Long fundId;//基金Id

//    private Long userId;

	@NotNull(message = "{BUY_AMOUNT}")
	@DecimalMin(value = "0.00000001", message = "{MIN_AMOUNT}")
	@Digits(integer = 10,fraction = 8, message = "{FRACTIONFRACTION}")
    private BigDecimal orderVolume;//申购数量

	@NotNull(message = "{TRADE_PWD_ERROR}")
	@Length( min =6 ,max = 6,message = "{TRADE_PWD_ERROR}")
	private String payPassword;//支付密碼




}
