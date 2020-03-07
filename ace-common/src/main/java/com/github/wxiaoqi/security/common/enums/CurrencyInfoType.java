package com.github.wxiaoqi.security.common.enums;

//资产转移状态
public enum CurrencyInfoType {
	/**
	 * 数字货币
	 */
	COIN_TRANSFER(1),
	/**
	 * 法币
	 **/
	LEGAL_CURRENCY(2);

	private final Integer value;

	private CurrencyInfoType(Integer value) {
		this.value = value;
	}

	public Integer value() {
		return this.value;
	}
}

