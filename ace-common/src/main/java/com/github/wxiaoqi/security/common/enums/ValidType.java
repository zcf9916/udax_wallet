package com.github.wxiaoqi.security.common.enums;


import com.github.wxiaoqi.security.common.config.Resources;

public enum ValidType {
	/**
	 * 未认证
	 * 
	 */
	NO_AUTH(0),
	/**
	 * 已提交待审核
	 */
	SUBMIT(1),
	/**
	 * 审核已通过
	 */
	AUTH(2);

	private final Integer value;

	private ValidType(Integer value) {
		this.value = value;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}

	public String msg() {
		return Resources.getMessage("ValidType_" + this.value);
	}

	public String toString() {
		return this.value.toString();
	}

}
