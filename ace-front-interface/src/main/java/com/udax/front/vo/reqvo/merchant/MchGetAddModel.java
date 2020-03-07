package com.udax.front.vo.reqvo.merchant;

import com.udax.front.annotation.DcCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;


@Getter
@Setter
public class MchGetAddModel extends OrderBaseModel {

	@NotBlank(message="{MERCHANT_SYMBOL_IS_NULL}")
	@Length(min = 1,max = 32 , message="{MERCHANT_SYMBOL_IS_NULL}")
	private String userId;//用户标示

    @DcCode
	private String symbol;
}
