package com.udax.front.vo.reqvo;

import com.udax.front.annotation.DcCode;
import com.udax.front.vo.reqvo.merchant.OrderBaseModel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
public class AddWithdrawModel implements Serializable {

	@NotBlank(message="{WITHDRAW_NOT_NULL}")
	@Length( min = 1,max = 256 , message="{WITHDRAW_NOT_NULL}")
	private String address;//提现地址

    @DcCode
	private String symbol;//代币

	private String remark;//
}
