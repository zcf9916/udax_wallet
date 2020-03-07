package com.github.wxiaoqi.security.common.enums;

public enum AccountSignType {
	/** 充值 */
	ACCOUNT_RECHARGE("recharge"),
	/** 提现冻结 */
	ACCOUNT_WITHDRAW_FREEZE("withdraw_freeze"),
	/** 解冻资产 */
	ACCOUNT_WITHDRAW_DEDUTION("withdraw_dedution"),
	/**
	 * 扣减资产
	 */
	ACCOUNT_WITHDRAW("withdraw"),
	/**
	 * 商户收款(变动waitConfirm字段)
	 */
	MERCHANT_ADD_ASSERT("merchant_assert"),

	/**
	 * 扣减可用
	 */
	ACCOUNT_PAY_AVAILABLE("pay_available"),

	/**
	 * 商户结算(把waitconfirm变动到available)
	 */
	MERCHANT_SETTLE("merchant_settle"),
	/**
	 * 商户退款,扣减结算账户资金
	 */
	MERCHANT_REFUND_SETTLE("merchant_refund"),
	/** 充值 */
	ACCOUNT_RECHARGE_FREEZE("rechargeFreeze"),
	;

	private final String value;

	private AccountSignType(String value) {
		this.value = value;
	}

	public static AccountSignType valueOfMsgType(String value) {
		AccountSignType[] accountSignTypes = AccountSignType.values();
		for (AccountSignType accountSignType : accountSignTypes) {
			if (accountSignType.value.equals(value)) {
				return accountSignType;
			}
		}
		return null;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public String value() {
		return this.value;
	}

}
