package com.github.wxiaoqi.security.common.enums;

/*
  货币转换类型
 */
public enum CurrencyTransferType {
	/**
	 * 平台对冲
	 */
	PLATFORM_HEDGE_FLAG(1),
	/**
	 * 用户报价
	 **/
	USER_OFFER(2);

	private final Integer value;

	private CurrencyTransferType(Integer value) {
		this.value = value;
	}

	public Integer value() {
		return this.value;
	}
}

