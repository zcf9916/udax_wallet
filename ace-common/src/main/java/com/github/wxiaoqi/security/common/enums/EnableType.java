package com.github.wxiaoqi.security.common.enums;


import com.github.wxiaoqi.security.common.config.Resources;

public enum EnableType {
	/** 禁用/禁止/关闭 */
	DISABLE(0),
	/**
	 * 启用/允许/开启
	 */
	ENABLE(1);

	private final Integer value;

	private EnableType(Integer value) {
		this.value = value;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}

	public String msg() {
		return Resources.getMessage("AccountType_" + this.value);
	}

	public String toString() {
		return this.value.toString();
	}
}
