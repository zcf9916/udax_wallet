package com.github.wxiaoqi.security.common.enums;

//状态
public enum RedPacketOrderStatus {
	/**
	 * 初始化
	 */
	INIT(0),
	/**
	 * 已抢完
	 **/
	ALLDONE(1),
	/**
	 * 退款中
	 */
	RETURNING(2),
	/**
	 * 已退还
	 */
	RETURN(3),
	/**
	 * 部分退还
	 *
	 */
	PART_RETURN(4);

	private final Integer value;

	private RedPacketOrderStatus(Integer value) {
		this.value = value;
	}

	public Integer value() {
		return this.value;
	}
}

