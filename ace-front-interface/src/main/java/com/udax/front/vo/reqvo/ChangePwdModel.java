package com.udax.front.vo.reqvo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 修改密码model
 */
@Data
public class ChangePwdModel implements Serializable{

	@NotBlank(message="{ACCOUNT_LENGTH}")
	@Length(min=8, max=30, message="{ACCOUNT_LENGTH}")
	private String username;//手机或者邮箱
	@NotBlank(message="{PASSWORD_IS_NULL}")
	@Pattern(regexp="^(?=.*[a-zA-Z])(?=.*\\d)[\\s\\S]{8,20}$",message = "{PASSWORD_LENGTH}")
    private String password;//登录密码


	private String mobileCode;// 验证码

	private String emailCode;// 邮箱验证码


}
