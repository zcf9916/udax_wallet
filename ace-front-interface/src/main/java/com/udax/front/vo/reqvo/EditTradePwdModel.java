package com.udax.front.vo.reqvo;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

@SuppressWarnings("serial")
@Data
public class EditTradePwdModel implements Serializable{


	@NotBlank(message="{PASSWORD_IS_NULL}")
	@Pattern(regexp="^[0-9]*[1-9][0-9]*{6}$",message = "{TRADE_PASSWORD_LENGTH}")
	private String password;

	private String mobileCode;//手机验证码
	
	private String emailCode;//邮箱验证码

}
