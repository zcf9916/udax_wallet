package com.github.wxiaoqi.security.common.enums;

//用户状态
public enum FrontUserStatus {
	/** 未激活*/
	UNACTIVE (1),
	/** 已激活 */
	ACTIVE(2),
	/** 冻结 */
	FREEZE(3);

	private final Integer value;

	private FrontUserStatus(Integer value) {
		this.value = value;
	}

	public Integer value() {
		return this.value;
	}

	public String toString() {
		return this.value.toString();
	}


}
