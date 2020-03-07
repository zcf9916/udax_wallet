package com.github.wxiaoqi.security.common.enums;

import com.github.wxiaoqi.security.common.config.Resources;

public enum MchResponseCode {
	/** 200请求成功 */
	OK(200),

	/**
	 * 商户相关
	 *
	 * **/
	//重复认证
	MERCHANT_AUTH_DUPLICATE(20000),
	//商户ID为空
    MERCHANT_ID_NULL(20001),
	//签名算法错误
	MERCHANT_SIGNTYPE_ERROR(20002),
	MERCHANT_SIGNT_NOTNULL(20003),
	MERCHANT_STR_NOTNULL(20004),
	MERCHANT_ORDER_NO(20005),
	//金额限制
	MERCHANT_AMOUNT_LIMIT(20006),
	MERCHANT_CURRENCYTYPE_ERROR(20007),
	MERCHANT_CALLBACK_ERROR(20008),
	//交易类型错误
	MERCHANT_TRADETYPE_ERROR(20009),
	//签名失败
	MERCHANT_SIGN_ERROR(20010),
	//商户状态异常
	MERCHANT_STATUS_UNNORMAL(20011),
	MERCHANT_IP_ERROR(20012),
	//必须先上传正面的图像
	MERCHANT_UPLOAD_ZM(20013),
	MERCHANT_SYMBOL_IS_NULL(20014),
	MERCHANT_NAME_IS_NULL(20015),
	MERCHANT_LISENCE_IS_NULL(20016),
	MERCHANT_PWD_IS_NULL(20017),
	MERCHANT_PASSWORD_LENGTH(20018),
	MERCHANT_NOAUTH(20019),
	MERCHANT_TAG_LENGTH(20020),
	MERCHANT_TRANS_NO(20021),
	MERCHANT_ORDER_BODY(20022),
	MERCHANT_PREID_ERROR(20023),
	MERCHANT_WITHDRAW_ADD_IS_NULL(20024),
	MERCHANT_REFUND_ORDER_NO(20025),
	MERCHANT_NOTICE_URL(20026),
	MERCHANT_REFUND_REMARK(20027),
	MERCHANT_REFUND_ACCOUNT_TYPE(20028),
	MERCHANT_ORDER_REFUND_BALANCE(20029),
	MERCHANT_NONCE_STR(20030),
	MERCHANT_REFUND_ORDER_NO_DUP(20031),
	MERCHANT_WITHDRAW_ORDER_NO_DUP(20032),
	MERCHANT_PRE_ORDER_NO_DUP(20033),
	MERCHANT_IPLIST_ERROR(20034),
	MERCHANT_NAME_NULL(20035),
	MERCHANT_RECHARGE_ERROR(20036),
	MERCHANT_WITHDRAW_ERROR(20037),
	MERCHANT_INVALID_IP(20038),
	SECRETKEY_IS_NULL(20039),
	/**
	 * 未知异常
	 */
	UNKNOW_ERROR(500);

	private final int value;

	private MchResponseCode(int value) {
		this.value = value;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public int value() {
		return this.value;
	}

	public String msg(Object... params) {
		return Resources.getMessage(this.name(), params);
	}


	public static int getValueByname(String name){
		for(MchResponseCode code : MchResponseCode.values()){
			if(code.name().equals(name)){
				return code.value;
			}
		}
		return MchResponseCode.UNKNOW_ERROR.value;
	}

	public static MchResponseCode getEnumByname(String name){
		for(MchResponseCode code : MchResponseCode.values()){
			if(code.name().equals(name)){
				return code;
			}
		}
		return MchResponseCode.UNKNOW_ERROR;
	}
}
