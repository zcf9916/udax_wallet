package com.github.wxiaoqi.security.common.enums;

public enum AccountType {
	/** 用户注册验证码 */
	EMAIL(1),
	/** 登录确认验证码 */
	MOBILE(2);
	private final Integer value;

	private AccountType(Integer value) {
		this.value = value;
	}
	
	public static AccountType valueOfMsgType(String value) {
		AccountType[] sendTypes = AccountType.values();
		for (AccountType sendMsgType : sendTypes) {
			if (sendMsgType.value.equals(value)) {
				return sendMsgType;
			}
		}
		return null;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}

}
