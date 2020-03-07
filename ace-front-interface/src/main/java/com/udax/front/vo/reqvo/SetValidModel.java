package com.udax.front.vo.reqvo;

import lombok.Data;

import java.io.Serializable;

/**
 * 开启邮箱验证、手机验证model
 */
@SuppressWarnings("serial")
@Data
public class SetValidModel implements Serializable{

	//private String sendType;//	1-手机验证  2-邮箱认证

   // private Integer validType;//是否开启验证1-开启 0-关闭
	
	private String mobileCode;//手机短信验证码
	
	private String emailCode;//邮箱验证码
	
	private String phone;//手机号

	private String email;//邮箱

	private String countryCode;//国际电话区号


	
}
