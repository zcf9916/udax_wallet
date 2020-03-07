package com.github.wxiaoqi.security.common.enums.merchant;

//商家接口,传输数据签名类型
public enum MchSignType {

	HMAC_SHA256 ("HMAC-SHA256");

	private final String value;

	private MchSignType(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

	public String toString() {
		return this.value;
	}

	public static boolean isType(String value){
		for(MchSignType type: MchSignType.values()){
			if(type.value.equals(value)){
				return true;
			}
		}
		return false;
	}


}
