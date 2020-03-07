package com.github.wxiaoqi.security.common.enums;


import com.github.wxiaoqi.security.common.config.Resources;

//账单类型
public enum BillType {
	/**
	 1.转账
	 2.转币
	 3.交易
	 4.提现
	 5.充值
	 */

	TRANSFER(1),
	TRANS_COIN(2),
	TRADE(3),
	WITHDRAW(4),
	RECHARGE(5);
	private final Integer value;

	private BillType(Integer value) {
		this.value = value;
	}
	


	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}

	public static boolean isType(Integer value){
		for(BillType type: BillType.values()){
			if(type.value.equals(value)){
				return true;
			}
		}
		return false;
	}


    public static String getName(BillType type){
		return Resources.getMessage(type.name());
	}

}
