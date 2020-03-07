package com.github.wxiaoqi.security.common.enums;


public enum DirectionType {
	/** 支出*/
	PAY(1),
	/** 收入 */
	INCOME(0);

	private final Integer value;

	private DirectionType(Integer value) {
		this.value = value;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}



	public static  String getDirectionSymbol(Integer value){
        if(value.equals(PAY.value)){
			return "-";
		} else {
			return "+";
		}
	}

}
