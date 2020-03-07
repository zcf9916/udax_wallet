package com.github.wxiaoqi.security.common.enums;


import com.github.wxiaoqi.security.common.config.Resources;

public enum CurrencyType {
	/** 人民币*/
	CHINA("CNY"),
	/** 韩币 */
	KOREA("KRW"),
	/** 美元 */
	USA("USD"),
	/** 印尼币 */
	IDR("IDR");
	/** 马来西亚币 */
//	MYR("MYR");

	private final String value;

	private CurrencyType(String value) {
		this.value = value;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public String value() {
		return this.value;
	}


}
