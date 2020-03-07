package com.udax.front.vo.reqvo.ud;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class UDRegisterModel implements Serializable{

	@NotBlank(message="{ACCOUNT_LENGTH}")
	@Length(min=8, max=30, message="{ACCOUNT_LENGTH}")
	private String username;

	@NotBlank(message = "{PASSWORD_LENGTH}")
	@Pattern(regexp="^(?=.*[a-zA-Z])(?=.*\\d)[\\s\\S]{8,20}$",message = "{PASSWORD_LENGTH}")
	private String password;
////	@NotBlank(message="{COUNTRYNAME_IS_NULL}")
//	private String countryName;

	private String countryCode;

	@Pattern(regexp = "^[0-9]{6}$", message = "{SMS_IS_ILLEGAL}")
	private String smsCode;
//	@Email(message = "{EMAIL_IS_ILLEGAL}")
//	private String email;

//	@NotBlank(message="{EXINFO_LENGTH}")
	private String exInfo;//所属白标

	@NotBlank(message = "{VISITCODE_LENGTH}")
	private String visitCode;//邀请码


	private Integer regType;//注册方式 1手机注册，2邮箱注册


}
