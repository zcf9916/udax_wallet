package com.github.wxiaoqi.security.common.enums.ud;

import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.entity.front.DcAssetAccount;
import com.github.wxiaoqi.security.common.entity.front.DcAssetAccountLog;
import com.github.wxiaoqi.security.common.enums.BillType;
import com.github.wxiaoqi.security.common.enums.DirectionType;
import com.github.wxiaoqi.security.common.exception.BusinessException;

import java.util.Arrays;
import java.util.List;

public enum SettledProfitStatus {
	/**
	 0.未汇总完数据  1.已汇总完数据待结算  2.已分配用户利润并记录其它用户的分成记录
	 */

	INIT(0),
	WAIT_SETTLE(1),
	SETTLED(2);
	private final Integer value;

	private SettledProfitStatus(Integer value) {
		this.value = value;
	}
	
	public static SettledProfitStatus valueOfMsgType(String value) {
		SettledProfitStatus[] sendTypes = SettledProfitStatus.values();
		for (SettledProfitStatus sendMsgType : sendTypes) {
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
