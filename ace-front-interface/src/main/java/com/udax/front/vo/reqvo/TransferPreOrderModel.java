package com.udax.front.vo.reqvo;

import com.udax.front.annotation.DcCode;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@SuppressWarnings("serial")
@Data
public class TransferPreOrderModel implements Serializable{

	@NotBlank(message="{ACCOUNT_LENGTH}")
	@Length(min=8, max=30, message="{ACCOUNT_LENGTH}")
	private String username;//用户名

	@NotNull(message = "{MERCHANT_AMOUNT_LIMIT}")
	@DecimalMin(value = "0.00000001", message = "{MERCHANT_AMOUNT_LIMIT}")
	@Digits(integer = 10,fraction = 8, message = "{MERCHANT_AMOUNT_LIMIT}")
	private BigDecimal transferAmount;//转账金额

	@DcCode
	private String dcCode;//代币

	@Length( min= 0, max=50, message="{REMARK_TOOLONG}")
	private String remark;

}
