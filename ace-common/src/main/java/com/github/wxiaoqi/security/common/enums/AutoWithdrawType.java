package com.github.wxiaoqi.security.common.enums;

public enum AutoWithdrawType {
	/** 非自动提币*/
	UNAUTO(0),
	/** 自动提币 */
	AUTO(1);

	private final Integer value;

	private AutoWithdrawType(Integer value) {
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
