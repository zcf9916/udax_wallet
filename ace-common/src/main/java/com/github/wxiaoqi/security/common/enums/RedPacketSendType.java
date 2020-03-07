package com.github.wxiaoqi.security.common.enums;

/**
 * 自增Id类型
 */

public enum RedPacketSendType {
	/** 单人红包 */
	SINGLE(0),
	/**群红包**/
	MULTI(1);
	private final Integer value;

	private RedPacketSendType(Integer value) {
		this.value = value;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}

	public static boolean isType(Integer value){
		for(RedPacketSendType type: RedPacketSendType.values()){
			if(type.value.equals(value)){
				return true;
			}
		}
		return false;
	}


}   

