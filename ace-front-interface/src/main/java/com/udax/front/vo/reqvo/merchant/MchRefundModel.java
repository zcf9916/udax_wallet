package com.udax.front.vo.reqvo.merchant;

import com.udax.front.annotation.DcCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.math.BigDecimal;


@Getter
@Setter
public class MchRefundModel extends OrderBaseModel {

	private String transNo;//原平台流水号

	private String mchOrderNo;//原商户订单号



	//从哪个账户类型退款
	private Integer refundAccountType;

	@NotNull(message = "{MERCHANT_ORDER_NO}")
	@Pattern(regexp="^[a-zA-Z0-9]{1,32}$",message = "{MERCHANT_ORDER_NO}")
	private String refundMchOrderNo;//商户退款订单号


	@DecimalMin(value = "0.00000001", message = "{MERCHANT_AMOUNT_LIMIT}")
	@Digits(integer = 10,fraction = 8, message = "{MERCHANT_AMOUNT_LIMIT}")
	private BigDecimal refundAmount;//退款代币数量

	@NotNull(message = "{MERCHANT_AMOUNT_LIMIT}")
	@Length(min = 1,max = 256  ,message = "{MERCHANT_AMOUNT_LIMIT}")
	private String refundNotifyUrl;//退款结果通知地址

	@Length(min = 0,max = 128  ,message = "{MERCHANT_REFUND_REMARK}")
	private String refundRemark;//退款备注

}
