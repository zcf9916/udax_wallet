package com.github.wxiaoqi.security.common.enums.merchant;

//商家接口,传输数据签名类型
public enum MchTradeType {

	JSAPI("JSAPI"),
	NATIVE("NATIVE"),
	APP("APP"),
	MWEB("MWEB");
	private final String value;

	private MchTradeType(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

	public String toString() {
		return this.value;
	}

	public static boolean isType(String value){
		for(MchTradeType type: MchTradeType.values()){
			if(type.value.equals(value)){
				return true;
			}
		}
		return false;
	}
}
