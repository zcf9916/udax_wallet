package com.github.wxiaoqi.security.common.enums;

/**
 * 冻结交易功能
 */
public enum FreezeFunctionType {
	/** 币币交易 */
    TRANSFER_COIN(1),
    /** 用户转账 */
    USER_TRANSFER(2),
    /** 提币 */
    WITHDRAW_COIN(3),
    /** 充币 */
	RECHARGE_COIN(4),
    /** 申购UD方案 */
    UD_QUEUE(5),
	/** 跟单交易*/
    FUND_TRADE(6);

    private final Integer value;

    private FreezeFunctionType(Integer value) {
        this.value = value;
    }
    
    public static FreezeFunctionType valueToFunctionType(Integer value) {
        FreezeFunctionType[] functionTypes = FreezeFunctionType.values();
        for (FreezeFunctionType functionType : functionTypes) {
            if (functionType.value==value) {
                return functionType;
            }
        }
        return null;
    }

    public static Integer functionTypeToValue(String paramType) {
        FreezeFunctionType[] functionTypes = FreezeFunctionType.values();
        for (FreezeFunctionType functionType : functionTypes) {
            if (paramType.equals(functionType.name())) {
                return functionType.value;
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

    public String toString() {
        return this.value.toString();
    }
}
