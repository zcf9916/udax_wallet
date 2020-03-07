package com.github.wxiaoqi.security.common.enums;

//状态
public enum RedPacketLogOrderStatus {
	/**
	 * 未入账
	 */
	UNSETTLE(0),
	/**
	 * 已入账
	 **/
	SETTLE(1);

	private final Integer value;

	private RedPacketLogOrderStatus(Integer value) {
		this.value = value;
	}

	public Integer value() {
		return this.value;
	}
}

