package com.github.wxiaoqi.security.common.enums;

public enum ClientType {
	/** PC */
	PC(1),
	/** APP */
	APP(2);

	private final Integer value;

	private ClientType(Integer value) {
		this.value = value;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}
	public static boolean isType(String value){
		for(ClientType type: ClientType.values()){
			if(type.value == Integer.valueOf(value)){
				return true;
			}
		}
		return false;
	}
}
