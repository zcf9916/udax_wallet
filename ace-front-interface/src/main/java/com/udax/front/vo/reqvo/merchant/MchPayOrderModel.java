package com.udax.front.vo.reqvo.merchant;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


@Getter
@Setter
public class MchPayOrderModel implements Serializable {

//
//	@NotBlank(message="{MERCHANT_ID_NULL}")
//	@Length(min=10, max=10, message="{MERCHANT_ID_NULL}")
//	private String mchNo;//商户号

	@NotBlank(message="{ORDER_NOT_EXIST}")
	@Length(min=1, max=32, message="{ORDER_NOT_EXIST}")
	private String prepayId;//预支付id

	@NotBlank(message="{TRADE_PWD_ERROR}")
	@Length(min=6, max=6, message="{TRADE_PWD_ERROR}")
	private String password;//支付密码




}
