package com.github.wxiaoqi.security.common.enums;


public enum QRCodeType {
	/**
	 * 转账收款码
	 *
	 */
	RECEIVE_CODE(1),
;

	private final Integer value;

	private QRCodeType(Integer value) {
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
		for(QRCodeType type: QRCodeType.values()){
			if(type.value.equals(value)){
				return true;
			}
		}
		return false;
	}


}
