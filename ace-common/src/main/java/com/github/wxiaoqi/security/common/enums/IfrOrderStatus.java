package com.github.wxiaoqi.security.common.enums;

public enum IfrOrderStatus {

	INIT(0),
	SUCCESS(1),
	FAILED(2);
	private final Integer value;

	private IfrOrderStatus(Integer value) {
		this.value = value;
	}
	
	public static IfrOrderStatus valueOfMsgType(String value) {
		IfrOrderStatus[] sendTypes = IfrOrderStatus.values();
		for (IfrOrderStatus sendMsgType : sendTypes) {
			if (sendMsgType.value.equals(value)) {
				return sendMsgType;
			}
		}
		return null;
	}

	public int value() {
		return this.value;
	}

}

