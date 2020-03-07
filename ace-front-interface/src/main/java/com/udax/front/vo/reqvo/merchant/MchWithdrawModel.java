package com.udax.front.vo.reqvo.merchant;

import com.udax.front.annotation.DcCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.math.BigDecimal;


@Getter
@Setter
public class MchWithdrawModel extends OrderBaseModel {

	@NotBlank(message="{MERCHANT_WITHDRAW_ADD_IS_NULL}")
	@Length( min = 1,max = 256 , message="{MERCHANT_WITHDRAW_ADD_IS_NULL}")
	private String address;//商家提现地址

    @DcCode
	private String symbol;//代币

	@DecimalMin(value = "0.00000001", message = "{MERCHANT_AMOUNT_LIMIT}")
	@Digits(integer = 10,fraction = 8, message = "{MERCHANT_AMOUNT_LIMIT}")
	private BigDecimal amount;//数量


	@NotNull(message = "{MERCHANT_ORDER_NO}")
	@Pattern(regexp="^[a-zA-Z0-9]{1,32}$",message = "{MERCHANT_ORDER_NO}")
	private String mchOrderNo;//商户订单号


	@Length(max = 10 , message="{MERCHANT_TAG_LENGTH}")
	private String tag;

	@Length(max = 10 )
	private String protocolType;//公链类型
}
