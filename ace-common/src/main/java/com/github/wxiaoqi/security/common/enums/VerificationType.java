package com.github.wxiaoqi.security.common.enums;


import com.github.wxiaoqi.security.common.config.Resources;

public enum VerificationType {
	/**
	 * 修改密码、手机号等信息时是否需要验证
	 * 0-关闭 1-开启 
	 */
	CLOSE(0),
	/**
	 * 开启
	 */
	OPEN(1);

	private final Integer value;

	private VerificationType(Integer value) {
		this.value = value;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}

	public String msg() {
		return Resources.getMessage("VerificationType_" + this.value);
	}

	public String toString() {
		return this.value.toString();
	}



	public static boolean isType(Integer value){
		for(VerificationType type: VerificationType.values()){
			if(type.value.equals(value)){
				return true;
			}
		}
		return false;
	}

}