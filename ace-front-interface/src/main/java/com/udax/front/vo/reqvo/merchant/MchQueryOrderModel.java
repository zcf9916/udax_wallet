package com.udax.front.vo.reqvo.merchant;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;


@Getter
@Setter
public class MchQueryOrderModel extends OrderBaseModel {

	@Length(min= 0,max=32, message="{MERCHANT_ORDER_NO}")
	private String mchOrderNo;//商户订单号

	@Length(min= 0,max=32, message="{ORDER_NOT_EXIST}")
	private String transNo;//钱包订单号

}
