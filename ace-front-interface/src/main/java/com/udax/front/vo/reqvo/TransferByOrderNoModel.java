package com.udax.front.vo.reqvo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@SuppressWarnings("serial")
@Data
public class TransferByOrderNoModel implements Serializable{

	@NotBlank(message="{TRADE_PWD_ERROR}")
	@Length(min=6, max=6, message="{TRADE_PWD_ERROR}")
	private String password;//支付密码

	@NotBlank(message="{ORDER_NOT_EXIST}")
	@Length(min=1, max=32, message="{ORDER_NOT_EXIST}")
	private String orderNo;//订单号


}
