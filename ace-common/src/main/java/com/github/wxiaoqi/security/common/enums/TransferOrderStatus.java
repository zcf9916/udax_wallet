package com.github.wxiaoqi.security.common.enums;

//状态
public enum TransferOrderStatus {
	/**
	 * 未付款
	 */
	UNPAY(1),
	/**
	 * 已转账
	 **/
	PAYED(2);

	private final Integer value;

	private TransferOrderStatus(Integer value) {
		this.value = value;
	}

	public Integer value() {
		return this.value;
	}
}

