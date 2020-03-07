package com.github.wxiaoqi.security.common.enums;

//用户类型
public enum FrontUserType {
	/** 用户*/
	USER (1),
	/** 商户 */
	MERCHANT(2);

	private final Integer value;

	private FrontUserType(Integer value) {
		this.value = value;
	}

	public Integer value() {
		return this.value;
	}

	public String toString() {
		return this.value.toString();
	}


}
