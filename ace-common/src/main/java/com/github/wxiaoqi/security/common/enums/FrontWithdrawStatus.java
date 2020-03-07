package com.github.wxiaoqi.security.common.enums;

import com.github.wxiaoqi.security.common.config.Resources;

public enum FrontWithdrawStatus {
	/** 待审核 */
	WaitAuto(0),
	/** 已审核 */
	Audited(1),
	/** 区块链已扫描提现信息 */
	WITHDRAWSEND(2),
	/**
	 * 转账处理中
	 */
	TransIng(3),
	/**
	 * 提现审核未通过转账失败
	 */
	TransError(4),
	/**
	 * 转账成功
	 */
	TransSuccess(5),
	/**
	 * 区块链返回提现失败
	 */
	BLOCKCHAIN_ERROR(6);

	private final Integer value;

	private FrontWithdrawStatus(Integer value) {
		this.value = value;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}

	public String msg() {
		return Resources.getMessage("FrontWithdrawStatus_" + this.value);
	}

	public String toString() {
		return this.value.toString();
	}
}
