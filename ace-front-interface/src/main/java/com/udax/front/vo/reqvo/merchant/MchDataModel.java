package com.udax.front.vo.reqvo.merchant;

import com.udax.front.annotation.DateTimeFormat;
import com.udax.front.annotation.DcCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


@Getter
@Setter
public class MchDataModel implements Serializable {

//	@NotBlank(message="{COUNTRY_IS_NULL}")
//	private String countryName;
	@NotBlank(message="{MERCHANT_NAME_IS_NULL}")
	@Length(min=1, max=30, message="{MERCHANT_NAME_IS_NULL}")
	private String mchName;//商户名

	@NotBlank(message="{MERCHANT_IPLIST_ERROR}")
	@Length(min=1, max=2560, message="{MERCHANT_IPLIST_ERROR}")
	private String bindAddress;//ip地址集合

	@NotBlank(message="{MERCHANT_RECHARGE_ERROR}")
	@Length(min=1, max=256, message="{MERCHANT_RECHARGE_ERROR}")
	private String rechargeCallback;//充值回调地址

	@NotBlank(message="{MERCHANT_WITHDRAW_ERROR}")
	@Length(min=1, max=256, message="{MERCHANT_WITHDRAW_ERROR}")
	private String withdrawCallback;//提现回调地址

	@NotBlank(message="{SECRETKEY_IS_NULL}")
	@Length(min=32, max=32, message="{SECRETKEY_IS_NULL}")
    private String secretKey;//秘钥


	private String mobileCode;

	private String emailCode;
}
