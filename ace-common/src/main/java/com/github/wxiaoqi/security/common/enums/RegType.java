package com.github.wxiaoqi.security.common.enums;


public enum RegType {
	/**
	 * 手机
	 *
	 */
	PHONE(1),
	/**
	 * 邮箱
	 */
	EMAIL(2);

	private final Integer value;

	private RegType(Integer value) {
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


	public static boolean isType(Integer value){
		for(RegType type: RegType.values()){
			if(type.value.equals(value)){
				return true;
			}
		}
		return false;
	}



}
