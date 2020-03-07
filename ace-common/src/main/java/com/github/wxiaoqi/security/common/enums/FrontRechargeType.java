package com.github.wxiaoqi.security.common.enums;

public enum FrontRechargeType {
	/** 普通用户*/
	NORMAL(1),
	/** 商户 */
	MERCHANT(2);

	private final Integer value;

	private FrontRechargeType(Integer value) {
		this.value = value;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}


	public String toString() {
		return this.value.toString();
	}
}
