package com.github.wxiaoqi.security.common.enums;

public enum VersionType {
	/** 安卓 */
	ANDROID(1),
	/** 失败 */
	IOS(2);

	private final Integer value;

	private VersionType(Integer value) {
		this.value = value;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}
	public static boolean isType(String value){
		for(VersionType type: VersionType.values()){
			if(type.value == Integer.valueOf(value)){
				return true;
			}
		}
		return false;
	}
}
