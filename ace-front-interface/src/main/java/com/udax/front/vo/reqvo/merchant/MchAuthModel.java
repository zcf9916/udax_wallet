package com.udax.front.vo.reqvo.merchant;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;


@Getter
@Setter
public class MchAuthModel implements Serializable {

//	@NotBlank(message="{COUNTRY_IS_NULL}")
//	private String countryName;
	@NotBlank(message="{MERCHANT_NAME_IS_NULL}")
	@Length(min=1, max=30, message="{MERCHANT_NAME_IS_NULL}")
	private String mchName;
	@NotBlank(message="{MERCHANT_LISENCE_IS_NULL}")
	@Length(min=1, max=1024, message="{MERCHANT_LISENCE_IS_NULL}")
	private String licenseZm;

	@Length(min=0, max=1024, message="{MERCHANT_LISENCE_IS_NULL}")
	private String licenseFm;
//	@NotBlank(message="{MERCHANT_PASSWORD_LENGTH}")
//	@Pattern(regexp="^(?=.*[a-zA-Z])(?=.*\\d)[\\s\\S]{8,20}$",message = "{MERCHANT_PASSWORD_LENGTH}")
//	private String loginPwd;

//
//	@NotBlank(message="{TRADE_PWD_ERROR}")
//	@Length(min=6, max=6, message="{TRADE_PWD_ERROR}")
//	private String password;//支付密码


}
