package com.udax.front.vo.reqvo.merchant;


import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.udax.front.annotation.DcCode;

import lombok.Getter;
import lombok.Setter;

//预下单接口
@Getter
@Setter
public class MchPreOrderModel extends OrderBaseModel {

	@NotBlank(message = "{MERCHANT_ORDER_NO}")
	@Pattern(regexp="^[a-zA-Z0-9]{1,32}$",message = "{MERCHANT_ORDER_NO}")
	private String mchOrderNo;//商户订单号
//
//	@NotBlank(message = "{MERCHANT_IP_ERROR}")
//	@Length( min=1,max=64,message = "{MERCHANT_IP_ERROR}")
//	private String ip;//调用支付接口的ip

//	@DecimalMin(value = "0.00000001", message = "{MERCHANT_AMOUNT_LIMIT}")
//	@Digits(integer = 10,fraction = 8, message = "{MERCHANT_AMOUNT_LIMIT}")
//	private BigDecimal totalAmount;//订单代币数量

//	@NotBlank(message="{MERCHANT_CURRENCYTYPE_ERROR}")
//	@DcCode
//	private String symbol;//代币代码
	
	private List<MchPayTokenModel> tokenList;

	@NotBlank(message="{MERCHANT_CALLBACK_ERROR}")
	@Length( min=1,max=256,message = "{MERCHANT_CALLBACK_ERROR}")
	private String notifyUrl;//通知回调地址


	@NotBlank(message="{MERCHANT_TRADETYPE_ERROR}")
	@Length( min=1,max=8,message = "{MERCHANT_TRADETYPE_ERROR}")
	private String tradeType;//交易类型   详见MchTradeType

	@NotBlank(message = "{MERCHANT_ORDER_BODY}")
	@Length( min=1,max=128,message = "{MERCHANT_ORDER_BODY}")
	private String body;//商品描述


}
