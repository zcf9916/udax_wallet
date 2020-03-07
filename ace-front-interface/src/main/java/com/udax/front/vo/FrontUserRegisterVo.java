package com.udax.front.vo;

import lombok.Data;

import java.io.Serializable;


@Data
public class FrontUserRegisterVo implements Serializable {


	/**
	 * 盐值
	 */
	private String salt;
	private String userPwd;

	private String uid;

	private String loginIp;

	private String bindDomain;

	private String userName;


	private String countryCode;

	private String recommondCode;//推荐人的推荐码


	private Long exId;//白标id

}
