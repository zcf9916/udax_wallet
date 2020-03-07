package com.github.wxiaoqi.security.common.enums.merchant;

import com.github.wxiaoqi.security.common.enums.RegType;

//退款来源
public enum MchRefundAccountType {

	//1.未结算资金退款 2.可用余额退款

	REFUND_SOURCE_WAITCONFIRM(1),
	REFUND_SOURCE_AVAILABLE(2),;

	private final Integer value;

	private MchRefundAccountType(Integer value) {
		this.value = value;
	}

	public Integer value() {
		return this.value;
	}

	public String toString() {
		return this.value.toString();
	}

	public static boolean isType(Integer value){
		for(MchRefundAccountType type: MchRefundAccountType.values()){
			if(type.value.equals(value)){
				return true;
			}
		}
		return false;
	}



}
