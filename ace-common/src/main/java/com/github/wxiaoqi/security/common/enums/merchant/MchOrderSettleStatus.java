package com.github.wxiaoqi.security.common.enums.merchant;

import com.github.wxiaoqi.security.common.config.Resources;

//0 未结算  1 已结算
public enum MchOrderSettleStatus {
	/** 未结算 */
	UNSETTLE(0),
	/** 已结算 */
	SETTLE(1);
//	/**
//	 * 超时
//	 */
//	OVERTIME(5),
//	/**
//	 * 已结算划转
//	 */
//	SETTLE(6);

	private final Integer value;

	private MchOrderSettleStatus(Integer value) {
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
