package com.github.wxiaoqi.security.common.enums.ud;

public enum UDOrderDetailStatus {
	/**
	 利润结算状态: 0-未结算完成,1-利润已汇总完成  2.利润已结算完成
	 */

	INIT(0),
	COLLECT(1),
	FINISH(2);
	private final Integer value;

	private UDOrderDetailStatus(Integer value) {
		this.value = value;
	}
	
	public static UDOrderDetailStatus valueOfMsgType(String value) {
		UDOrderDetailStatus[] sendTypes = UDOrderDetailStatus.values();
		for (UDOrderDetailStatus sendMsgType : sendTypes) {
			if (sendMsgType.value.equals(value)) {
				return sendMsgType;
			}
		}
		return null;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}

}
