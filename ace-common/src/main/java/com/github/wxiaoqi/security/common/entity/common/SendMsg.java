package com.github.wxiaoqi.security.common.entity.common;

import java.io.Serializable;

import lombok.Data;

@SuppressWarnings("serial")
@Data
public class SendMsg implements Serializable {

    //@ApiModelProperty(value = "信息类型：1短信2语音", required = true)
    private Integer msgType;
	//@ApiModelProperty(value = "短信类型:1.用户注册验证码2.登录确认验证码3.修改密码验证码4.身份验证验证码5.信息变更验证码6.活动确认验证码", required = true)
	private String bizType;
	//@ApiModelProperty(value = "手机号", required = true)
	private String phone;
	//@ApiModelProperty(value = "国家区号", required = true)
	private String locationCode;
	//@ApiModelProperty(value = "短信内容", required = false)
	private String content;
	//@ApiModelProperty(value = "发送人", required = false)
	private String sender;
	//@ApiModelProperty(value = "短信验证码", required = false)
	private String smsCode;

}
