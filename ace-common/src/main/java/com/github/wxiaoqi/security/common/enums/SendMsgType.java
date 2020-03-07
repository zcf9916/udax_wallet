package com.github.wxiaoqi.security.common.enums;


import com.github.wxiaoqi.security.common.config.Resources;

public enum SendMsgType {
	/** 用户注册验证码 */
	USER_REG("1"),
	/** 登录确认验证码 */
	USER_LOGIN("2"),
	/** 修改密码验证码 */
	CHANGE_PWD("3"),
//	/** 身份认证验证码 */
//	KYC_AUTH("4"),
	/** 信息变更验证码 */
	CHANGE_INFO("5"),
	/**
	 * 充值到账
	 */
	RECHARGE_COIN_IN("6"),
	/**
	 * 提币到账
	 */
	WITHDRAW_COIN_OUT("7"),
	/**
	 * 忘记密码
	 */
	FORGOT_PASSWORD("8"),
	/**
	 * 用户审核身份认证通知
	 */
	IDENTITY_CERTIFY("9"),
	/**
	 * 开启或关闭手机/邮箱验证
	 */
	CLOSE_VERIFICATION("10"),
	/**
	 * 提币地址管理-添加地址
	 */
	WITHDRAW_ADDRESS_MANAGE("11"),
	/**
	 * 提币请求
	 */
	WITHDRAW_REQUEST("12"),

	/**
	 * 提币审核失败，发送提币失败原因
	 */
	WITHDRAW_FAIL("13"),
	/**
	 * 修改支付密碼
	 */
	CHANGE_PAY_PASSWORD("14"),
	/**
	 * 确认支付购买法币
	 */
	CONFIRM_THE_PAYMENT("15"),
	/**
	 * 确认收到法币购买金额
	 */
	CONFIRM_RECEIPT_REMITTANCE("16"),
	/**
	 * 用户主动取消委托单
	 */
	CANCEL_FB_TAKER_ORDER("17"),

	/**
	 * 付款超时
	 */
	PAYMENT_TIMEOUT("18"),
	/**
	 * 出售订单通知购买方付款
	 */
    SELLER_NOTICE_PAY("19"),
	/**
	 * 商家审核通知
	 */
	MERCHANT_REVIEW("20"),

	//更新用户信息
	MERCHANT_UPDATE_INFO("21"),

	//UD社区锁定用户
	UD_COMMUNITY_LOCK_USER("22"),

	//UD社区通知用户充钱
	UD_COMMUNITY_CHARGE_NOTICE("23");

	private final String value;

	private SendMsgType(String value) {
		this.value = value;
	}

	public static SendMsgType valueOfMsgType(String value) {
		SendMsgType[] sendTypes = SendMsgType.values();
		for (SendMsgType sendMsgType : sendTypes) {
			if (sendMsgType.value.equals(value)) {
				return sendMsgType;
			}
		}
		return null;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public String value() {
		return this.value;
	}

	public String msg() {
		return Resources.getMessage("SendMsgType_" + this.value);
	}

	public String toString() {
		return this.value.toString();
	}


}
