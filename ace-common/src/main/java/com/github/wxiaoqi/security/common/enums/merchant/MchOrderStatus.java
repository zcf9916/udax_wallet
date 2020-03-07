package com.github.wxiaoqi.security.common.enums.merchant;

import com.github.wxiaoqi.security.common.config.Resources;

//1.待付款;2.已付款;3. 已付款,部分退款中;4.已付款,全额退款
public enum MchOrderStatus {
	/** 待付款 */
	UNPAY(1),
	/** 已付款 */
	PAY_SUCCESS(2),
	/**
	 * 已付款,部分退款中
	 */
	REFUND_PART(3),
	/**
	 * 已付款,全额退款
	 */
	REFUND_ALL(4);
//	/**
//	 * 超时
//	 */
//	OVERTIME(5),
//	/**
//	 * 已结算划转
//	 */
//	SETTLE(6);

	private final Integer value;

	private MchOrderStatus(Integer value) {
		this.value = value;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}

	public String msg() {
		return Resources.getMessage("OrderStatus_" + this.value);
	}

	public String toString() {
		return this.value.toString();
	}

}
