package com.github.wxiaoqi.security.common.enums;



public enum ValidCodeType {
	/** 短信验证码 */
	PHONE_CODE(1),
	/** 邮箱验证码 */
	EMAIL_CODE(2);
   
	private final Integer value;

	private ValidCodeType(Integer value) {
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
	
	
	public static  ValidCodeType   transferFromValue(Integer value){
		 switch(value){    
	       case  1:
		     return   PHONE_CODE;   
		   case  2:
		     return  EMAIL_CODE;   
		   default:   
		     return null;
		 }
	}



	public static boolean isType(Integer value){
		for(ValidCodeType type: ValidCodeType.values()){
			if(type.value.equals(value)){
				return true;
			}
		}
		return false;
	}
}   

