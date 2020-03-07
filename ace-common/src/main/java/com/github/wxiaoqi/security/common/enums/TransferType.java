package com.github.wxiaoqi.security.common.enums;

public enum TransferType {
	/** 普通转账 */
	COMMON(0),
	/** 红包转账 */
	REDPACKET(1);

	private final Integer value;

	private TransferType(Integer value) {
		this.value = value;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}
	public static boolean isType(String value){
		for(TransferType type: TransferType.values()){
			if(type.value == Integer.valueOf(value)){
				return true;
			}
		}
		return false;
	}
}
