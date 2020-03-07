package com.github.wxiaoqi.security.common.enums.ud;

public enum QueueStatus {
	/**
	 0.失效   1.等待匹配中 2.已匹配
	 */

	INVALID(0),
	WAIT_MATCH(1),
	MATCH(2);
	private final Integer value;

	private QueueStatus(Integer value) {
		this.value = value;
	}
	
	public static QueueStatus valueOfMsgType(String value) {
		QueueStatus[] sendTypes = QueueStatus.values();
		for (QueueStatus sendMsgType : sendTypes) {
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
