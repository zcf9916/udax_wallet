package com.github.wxiaoqi.security.common.enums.ud;

public enum UdCommissionType {
	/**
	 0.用户分成  1.平台分成 2.节点奖分成 3.全球利润分成
	 */

	USER_CMS(0),
	PLAT_CMS(1),
	NODE_AWARD(2),
	GLOBAL_AWARD(3);
	private final Integer value;

	private UdCommissionType(Integer value) {
		this.value = value;
	}
	
	public static UdCommissionType valueOfMsgType(String value) {
		UdCommissionType[] sendTypes = UdCommissionType.values();
		for (UdCommissionType sendMsgType : sendTypes) {
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
