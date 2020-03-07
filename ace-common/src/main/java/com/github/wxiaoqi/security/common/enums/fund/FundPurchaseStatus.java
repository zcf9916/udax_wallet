package com.github.wxiaoqi.security.common.enums.fund;

//基金申購状态
public enum FundPurchaseStatus {
	/** 已認購*/
	SUBSCRIBE (1),
	/** 運行中 */
	RUNNING(2),
	/** 结算中 */
	SETTLEING(3),
	/** 已结算 */
	SETTLED(4);

	private final Integer value;

	private FundPurchaseStatus(Integer value) {
		this.value = value;
	}

	public Integer value() {
		return this.value;
	}

	public String toString() {
		return this.value.toString();
	}


}
