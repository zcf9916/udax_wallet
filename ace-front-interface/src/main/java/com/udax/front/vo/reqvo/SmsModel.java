package com.udax.front.vo.reqvo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 发送短信model
 */
@Data
public class SmsModel implements Serializable {

	@javax.validation.constraints.NotBlank(message = "{SENDTYPE_IS_NULL}")
	private String sendType;// 发送类型，参考ValidCodeType
	@javax.validation.constraints.NotBlank(message = "{SENDMSGTYPE_IS_NULL}")
	private String sendMsgType;// 短信类型,参考SendMsgType
	@javax.validation.constraints.NotBlank(message = "{ACCOUT_IS_NULL}")
	private String userName;// 手机或者邮箱

	private String locationCode;// 手机发送前缀

//	@NotBlank(message = "{VERIFY_IS_NULL}")
//	private String verifyCode;// 验证码

//	@javax.validation.constraints.NotBlank(message = "{TICKT_IS_NULL}")
//	private String ticket;//服务器返回的票据
//
//	@javax.validation.constraints.NotBlank(message = "{TICKT_IS_NULL}")
//	private String randstr;//服务器返回的随机字符串

//	@NotBlank(message="{EXINFO_LENGTH}")
//	private String exInfo;//所属白标

}