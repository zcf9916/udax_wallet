package com.udax.front.vo.reqvo.merchant;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;


@Getter
@Setter
public class MchCheckPreIdModel implements Serializable {

//
//	@NotBlank(message="{MERCHANT_ID_NULL}")
//	@Length(min=10, max=10, message="{MERCHANT_ID_NULL}")
//	private String mchNo;//商户号

	@NotBlank(message="{ORDER_NOT_EXIST}")
	@Length(min=1, max=32, message="{ORDER_NOT_EXIST}")
	private String prepayId;//预支付id




}
