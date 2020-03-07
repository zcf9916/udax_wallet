package com.github.wxiaoqi.security.common.enums.merchant;

//用户状态
public enum MchNotifyStatus {

	//1.预下单标示  2.下单成功通知标示  3.充值成功通知标示  4.提现成功通知标示 5.退款成功通知标示

	PREORDER_NOTICE(1),
	PAY_SUCCESS_NOTICE(2),
	RECHARGE_SUCCESS_NOTICE(3),
	WITHDRAW_SUCCESS_NOTICE(4),
	REFUND_SUCCESS_NOTICE(5);

	private final Integer value;

	private MchNotifyStatus(Integer value) {
		this.value = value;
	}

	public Integer value() {
		return this.value;
	}

	public String toString() {
		return this.value.toString();
	}


}
