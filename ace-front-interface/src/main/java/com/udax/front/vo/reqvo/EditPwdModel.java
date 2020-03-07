package com.udax.front.vo.reqvo;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.io.Serializable;


@Data
public class EditPwdModel implements Serializable{

	@javax.validation.constraints.NotBlank(message="{PASSWORD_IS_NULL}")
	@Pattern(regexp="^(?=.*[a-zA-Z])(?=.*\\d)[\\s\\S]{8,20}$",message = "{PASSWORD_LENGTH}")
	private String password;

	//@NotBlank(message="{OLDPASSWORD_IS_NULL}")
	@javax.validation.constraints.NotBlank(message="{PASSWORD_IS_NULL}")
	private String oldPassword;

//	private String mobileCode;// 验证码
//
//	private String emailCode;// 邮箱验证码

}
