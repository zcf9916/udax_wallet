package com.github.wxiaoqi.security.common.enums;

import com.github.wxiaoqi.security.common.config.Resources;

public enum FrontWithdrawType {
	/** 普通用户*/
	NORMAL(1),
	/** 商户 */
	MERCHANT(2);

	private final Integer value;

	private FrontWithdrawType(Integer value) {
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
