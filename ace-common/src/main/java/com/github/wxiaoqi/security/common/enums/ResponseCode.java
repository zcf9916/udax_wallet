package com.github.wxiaoqi.security.common.enums;

import com.github.wxiaoqi.security.common.config.Resources;

public enum ResponseCode {
	/** 200请求成功 */
	OK(200),

	/** --------------------资产----------------------- **/
	/**
	 * 资产校验不通过，资金存在异常串改
	 */
	ASSERT_30001(30001),
	/**
	 * 账户余额不足以提现，系统被串改违规炒作
	 */
	ASSERT_ACOUNT_30002(30002),

	/** --------------------充值----------------------- **/
	RECHARGE_40001(40001),

	/** -------------------用户相关---------------------- **/
	/**
	 * 用户审核未通过
	 */
	USER_CERT_NOPASS(10001),
	/** 频繁操作 */
	MULTI_STATUS(10000),

	/** 账户密码错误 */
	ACCOUNT_PWD_ERROR(10001),
	/** 请求参数出错 */
	PARAM_ERROR(10002),
	/** 没有登录 */
	UNAUTHORIZED(10003),
	/** 用户未激活 */
	UNACTIVE(10004),
	/** 用户未认证 */
	UNAUTH(10005),
	/** 用户已被锁定 */
	LOCKED(10006),
	/** 用户不存在 */
	USER_NOT_EXIST(10007),
    /**短信发送类型错误**/
	SMS_SEND_TYPE_ERROR(10008),
	/**手机已被绑定**/
	MOBILE_BINDED(10009),
	/**邮箱已被绑定**/
	EMAIL_BINDED(10010),
	/**验证码错误**/
	VERIFICATION_CODE_ERROR(10011),
	/**验证码错误**/
	OLD_PWD_ERROR(10012),
	/** 用户存在 */
	USER_EXIST(10013),
	/** 支付密码错误 */
	TRADE_PWD_ERROR(10014),
	/** token失效 */
	TOKEN_VALID(10015),
    /**用户重复认证*/
	USER_AUTH_DUPLICATE(10016),
	/**手机hi号非法**/
	MOBILE_ILLEGAL(10017),
	/*邮箱非法**/
	EMAIL_ILLEGAL(10018),
	/**验证码错误**/
	SYS_VERIFICATION_CODE(10019),
	//短信/邮箱验证码不存在,请重新发送
	MSGINFO_IS_NULL(10020),
	//注册类型不能为空
	REGTYPE_NOTNULL(10021),
	//手机国家码非法
	COUNTRYCODE_VALID(10022),
	NAME_IS_NULL(10023),
	SMS_IS_ILLEGAL(10024),
	ACCOUNT_LENGTH(10025),
	PASSWORD_LENGTH(10026),
	EXINFO_LENGTH(10027),
	DATEFORMAT_ERROR(10028),
	FUND_NAME_LENGTH(10029),
	NAME_LENGTH(10030),
	IDCARD_LENGTH(10031),
	IDCARDIMG_IS_NULL(10032),
	MODIFY_EMAIL_NOT_SUPPORT(10033),
	ORDER_NOT_EXIST(10034),
	ID_IS_NULL(10035),
	PAY_PASSWORD_WRONG(10036),
	VISITCODE_IS_ILLEGAL(10037),
	BILLTYPE_ERROR(10038),
	ONE_MUST_OPENED(10039),
	TICKT_IS_NULL(10040),
	SMS_LIMIT(10041),
	ADD_DUP(10042),
	WITHDRAWADD_REMARK_TOOLONG(10043),
	WITHDRAW_NOT_NULL(10044),
	VISITCODE_LENGTH(10055),
	INVESTMENT_LIMIT(10056),
	PLAN_CLOSE(10057),
	DONOT_NEED_UNLOCK(10058),
	PLAN_NOT_MATCH(10059),
	IFR_AMOUNT_LIMIT(10060),
	CAMPAIGNID_ERROR(10061),
	PLAN_ERROR(10062),
	CURRENCY_ERROR(10063),
	FREEZE_USER_ISNULL(10064),
     //已经抢过红包了
	RP_OPENED(10065),
	REMARK_TOOLONG(10066),
	/**
	 * 上传相关
	 *
	 * **/
	UPLOAD_FILE_IS_NULL(11000),
	UPLOAD_FILE_SIZE(11001),
	//上传类型错误
	UPLOAD_TYPE_ERROR(11002),


	//版本控制类型异常
	VERSION_TYPE_ERROR(11003),


	/**
	 * 转账相关
	 *
	 * **/
	/** 币币账户余额不足 */
	BALANCE_NOT_ENOUGH(12015),
	/** 转账失败 **/
	TRANSFER_FAIL(12016),
	/**二维码类型错误**/
	QRCODE_TYPE_ERROR(12017),
	/**  订单已处理 **/
	TRANSFER_DEALED(12018),
	//非法代币
	DC_INVALID(12019),
	FUND_BALANCE_NOT_ENOUGH(12020),
	MIN_TRANSFER(12021),//小于最小转账数量

	/**
	 * 基金相关
	 *
	 * **/
	FUND_NOT_EXIST(13000),
	FUND_GREATER_THAN_BALANCE(13001),
	FUND_LESS_THAN_MINNUM(13002),
	FUND_NOT_BEGIN(13003),
	FUND_SELLOUT(13004),
	FUND_END(13005),
	FUND_RATE_ERROR(13006),
	FUND_TRADETYPE_ERROR(13007),
	//未达到结算条件
	NO_SATISFY_SETTLEING(13008),
	//产品冻结数量小于认购数量,数据异常错误
	FUND_GREATER_THAN_FREEZE(13009),
	//已结算的产品不能设置实际收益率了
	SETTLED_CANNOT_SETRATE(13010),




	/*
	* * --------------------提现----------------------- **/
	/**
	 * 50001 已限制用户提币功能
	 */
	WITHDRAW_50001(50001),
	/**
	 * 提币功能暂时关闭
	 */
	WITHDRAW_50002(50002),
	/**
	 * 您在前%s小时内修改过密码或手机号,需在修改%s小时后才允许提币
	 */
	WITHDRAW_50003(50003),

	//获取提现信息失败
	WITHDRAW_50004(50004),

	//小于最小提币量
	WITHDRAW_50005(50005),
	//大于最大提币量
	WITHDRAW_50006(50006),
	//大于当日最大提币量
	WITHDRAW_50007(50007),
	//手续费数据不匹配
	WITHDRAW_50008(50008),
	//提现手续费模板错误
	WITHDRAW_50009(50009),
	//提现手续费不能大于提现数量
	WITHDRAW_50010(50010),
	/** --------------------币币交易----------------------- **/
	/**
	 * 交易转换交易币种不能小于最小交易量
	 */
	TRANSFER_60001(60001),
	/**
	 * 交易转换交易币种不能大于最大交易量
	 */
	TRANSFER_60002(60002),

	/**
	 * 交易转换交易币种暂停交易
	 */
	TRANSFER_60003(60003),

	/**
	 * 获取交易对最新价失败
	 */
	TRANSFER_60004(60004),

	/**
	 * 获取配置信息失败
	 */
	TRANSFER_60005(60005),
	//手续费不能大于转换数量
	TRANSFER_60006(60006),
	//两个币种之间不能转换
	TRANSFER_60007(60007),

	TRANSFER_60008(60008),

	/**
	 * UD社区相关
	 *
	 * **/
	//重复排队
	DUP_QUEUE(70000),
	//当前订单未达到结算条件
	NOT_SETTLE_CONDITION(70001),
	//当前订单未达到结算条件
	NOT_RELASE_CONDITION(70002),
	/**
	 * 冻结操作功能
	 *
	 * **/
	NO_OPERATION_AUTHORITY(80000),


	/**
	 * 未知异常
	 */
	UNKNOW_ERROR(500);

	private final int value;

	private ResponseCode(int value) {
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
		for(ResponseCode code : ResponseCode.values()){
			if(code.name().equals(name)){
				return code.value;
			}
		}
		return ResponseCode.UNKNOW_ERROR.value;
	}

	public static ResponseCode getEnumByname(String name){
		for(ResponseCode code : ResponseCode.values()){
			if(code.name().equals(name)){
				return code;
			}
		}
		return ResponseCode.UNKNOW_ERROR;
	}
}
