package com.github.wxiaoqi.security.common.enums;

public enum CommonStatus {
	/** 成功 */
	SUCC(0),
	/** 失败 */
	FAIL(1);

	private final Integer value;

	private CommonStatus(Integer value) {
		this.value = value;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}

}
