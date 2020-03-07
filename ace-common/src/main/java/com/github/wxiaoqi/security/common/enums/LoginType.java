package com.github.wxiaoqi.security.common.enums;

import com.github.wxiaoqi.security.common.config.Resources;

public enum LoginType {

	/** pc */
	WEB(1),
	/** android */
	ANDROID(2),
	/** ios */
	IOS(3);

	private final Integer value;

	private LoginType(Integer value) {
		this.value = value;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}

	public String msg() {
		return Resources.getMessage("LoginType_" + this.value);
	}

	public String toString() {
		return this.value.toString();
	}
}
