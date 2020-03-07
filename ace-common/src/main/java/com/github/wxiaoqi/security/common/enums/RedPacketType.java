package com.github.wxiaoqi.security.common.enums;

/**
 * 自增Id类型
 */

public enum RedPacketType {
	/**普通红包 */
	COMMON(0),
	/**随机红包**/
	RANDOM(1);
	private final Integer value;

	private RedPacketType(Integer value) {
		this.value = value;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}

	public static boolean isType(Integer value){
		for(RedPacketType type: RedPacketType.values()){
			if(type.value.equals(value)){
				return true;
			}
		}
		return false;
	}



}   

