package com.github.wxiaoqi.security.common.enums;

/**
 * 自增Id类型
 */

public enum GeneratorIdType {
	/** 邀请码 */
	VISIT_CODE("VISIT_CODE"),
	/**商户号**/
	MERCHNAT_ACCOUNT("MERCHNAT_ACCOUNT");
	private final String value;

	private GeneratorIdType(String value) {
		this.value = value;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public String value() {
		return this.value;
	}



}   

