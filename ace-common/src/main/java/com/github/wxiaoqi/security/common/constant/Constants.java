package com.github.wxiaoqi.security.common.constant;

/**
 * 常量表
 *
 * @author ShenHuaJie
 * @version $Id: Constants.java, v 0.1 2014-2-28 上午11:18:28 ShenHuaJie Exp $
 */
public interface Constants {
	/**
	 * 缓存命名空间
	 */
	static final String CACHE_NAMESPACE = "UDAX-WALLET:";
	/**
	 * 缓存命名空间
	 */
	static final String SYSTEM_CACHE_NAMESPACE = "S:WALLET:";

	static final String USER_LEVELCODE_SPILT = "A";

	// 批量更新限制
	static final Integer BATCHUPDATE_LIMIT = 200;

	public static final int DEFAULT_NUMBER_PERPAGE = 10;

	// redis 大数据量key 做的hash
	static final Integer REDIS_MAP_BATCH = 100;

	// 充值
	static final String ACCOUNT_RECHARGE = "recharge";

	// 提现冻结
	static final String ACCOUNT_WITHDRAW_FREEZE = "withdraw_freeze";

	// 提现扣减
	static final String ACCOUNT_WITHDRAW_DEDUTION = "withdraw_dedution";

	// 提现扣减
	static final String ACCOUNT_WITHDRAW = "withdraw";

	// 提现锁定时长
	static final String LOCK_WITHDRAW_TIME = "withdraw_lock_basic";

	// 公链代币ETH
	static final String SYMBOL_ETH = "ETH";

	// 公链代币ETH
	static final String QUOTATION_DCCODE = "USDT";

	static final String TENCENT_GROUP_NAME = "tc:";

	// UD社区的代币
	static final String UD_SYMBOL = "UD";

	static final String MCH_CALLBACK_TASK = "MCH_CALLBACK_TASK";

	static final String SMS_LIMIT=CACHE_NAMESPACE + "sms";

	/*区块链查询币种请求参数*/
	static final String GET_WALLET_TYPE="getWalletType?";
	/*区块链提币地址请求参数*/
	static final String GET_WITHDRAW_WALLET ="getWithdrawWallet?";
	/*区块链待提币请求参数*/
	static final String GET_WALLET_USER_WITHDRAW ="getWalletUserWithdraw?";
	/*区块链提币请求[POST]参数*/
	static final String USER_TRANSACTION ="userTransaction";
	/*区块链添加提币地址请求[POST]参数*/
	static final String IMPORT_ADDRESS ="importAddress";
	/*区块链出入金记录查询参数*/
	static final String GET_WALLET_TRANSACTION="getWalletTransaction?";
	/*区块链汇聚地址列表*/
	static final String GET_WALLET_PUBLIC_CONF="getWalletPublicConf?";
	/*区块链汇聚地址新增 更新 删除*/
	static final String SET_WALLET_PUBLIC_CONF="setWalletPublicConf?";
	/*区块链手续费地址查询*/
	static final String GET_WALLET_FEE_CONF="getWalletFeeConf?";
	/*区块链手续费地址 新增 更新 删除*/
	static final  String SET_WALLET_FEE_CONF="setWalletFeeConf?";
	/**
	 * 统一编码
	 */
	static final String CHAR_SET = "UTF-8";

	/**
	 * 支付对接版本号
	 */
	static final String VERSION = "1.0";

	//每次调用间隔时间
	public Integer[] callBackInterval = {1,15,60,120,300,600,1800,3600};

	// 后台数据表缓存的前缀
	public interface OrderPreFix {

		public static final String MCH = "mch";// 商户订单

		public static final String TRANSFER = "tr";// 转账订单

		public static final String TRANSFER_COIN = "tc";// 转账订单

	}


	// 红包相关的前缀
	public interface REDPACKETS {

		public static final String RPORDER = CACHE_NAMESPACE +  "RPORDER:";// 剩余可抢红包个数
		public static final String RPLOG = CACHE_NAMESPACE +  "RPLOG:";// 抢红包记录
		public static final String RP_USERID = CACHE_NAMESPACE +  "RPUSERID:";// 抢红包记录对应的用户id列表


	}

	// 后台数据表缓存的前缀
	public interface CacheServiceType {

		public static final String FRONTUSER = CACHE_NAMESPACE + "fu";
		public static final String ADMIN_USER = CACHE_NAMESPACE + "admin:user";
		public static final String ADMIN_MENU_ALL = CACHE_NAMESPACE + "admin:menuAll";
		public static final String ADMIN_ELEMENT_ALL = CACHE_NAMESPACE + "admin:elementAll";
		public static final String EmailConfig = CACHE_NAMESPACE + "emailConfig";// 邮件配置
		public static final String EmailTemplate = CACHE_NAMESPACE + "emailTemplate";// 邮件模板
		public static final String EmailAuditor = CACHE_NAMESPACE + "emailAuditor";// 邮件审核人员
		public static final String SmsConfig = CACHE_NAMESPACE + "smsConfig";// 短信配置
		public static final String FrontHelpContent = CACHE_NAMESPACE + "frontHelpContent:";// 帮助内容
		public static final String FrontHelpType = CACHE_NAMESPACE + "frontHelpType:";// 帮助类型
		public static final String BASIC_SYMBOL = CACHE_NAMESPACE + "basicSymbol:list";
		public static final String BASIC_SYMBOL_REPEAT = CACHE_NAMESPACE + "basicSymbol:list:repeat";
		public static final String BASIC_SYMBOL_EXCH = CACHE_NAMESPACE + "basicSymbol:list:exch";
		public static final String FRONT_COUNTRY = CACHE_NAMESPACE + "frontCountry:list"; // 国家表
		// 数据字典--> 根据DirtTypeConstant中常量获取集合(字典数据中languageType属性为null)
		// -->根据DirtTypeConstant中常量+:languageType (字典数据中languageType不为null)
		public static final String DICT_DATA = CACHE_NAMESPACE + "dictData:list:";
		// 数据字典--> 根据DirtTypeConstant中常量获取集合(字典数据中languageType属性为null)
		// -->根据DirtTypeConstant中常量+:languageType (字典数据中languageType不为null)
		public static final String DICT_DATA_MAP = CACHE_NAMESPACE + "dictData:map:";
		public static final String DICT_DATA_DATA = CACHE_NAMESPACE + "dictData:";
		// 数字货币转换配置 --->去重后的源货币集合
		public static final String CFG_CURRENCY_TRANSFER = CACHE_NAMESPACE + "cfgCurrencyTransfer:list";
		public static final String CFG_CURRENCY_TRANSFER_EXCH = CACHE_NAMESPACE + "cfgCurrencyTransfer:list:exch";
		// 从缓存中获取目标货币集合List<CfgCurrencyTransfer> 参数:srcSymbol
		public static final String CFG_CURRENCY_TRANSFER_STRING = CACHE_NAMESPACE + "cfgCurrencyTransfer:";
		public static final String TRANSFER_EXCH =CACHE_NAMESPACE+"transferExch:";
		public static final String VALUATION_MODE =CACHE_NAMESPACE+"ValuationMode:";
		public static final String VALUATION=CACHE_NAMESPACE+"Valuation:";
		public static final String TRANSFER_EXCH_LIST =CACHE_NAMESPACE+"transferExch:list:";
		// 从缓存中获取单个CfgCurrencyTransfer对象 参数: srcSymbol+:+dstSymbol
		public static final String CFG_CURRENCY_TRANSFER_SYMBOL = CACHE_NAMESPACE + "cfgCurrencyTransfer:";
		// 从缓存中获取单个CfgDcRechargeWithdraw对象 参数: symbol + exchId
		public static final String CFG_DCRECHARGE_WITHDRAW_EXCH = CACHE_NAMESPACE + "CfgDcRechargeWithdraw:exch:";
		public static final String CFG_DCRECHARGE_WITHDRAW = CACHE_NAMESPACE + "CfgDcRechargeWithdraw:exch:isShow";
		// 从缓存中获取所有货币配置信息(MAP集合 key:symbol value: CfgDcRechargeWithdraw)
		public static final String CFG_DCRECHARGE_WITHDRAW_MAP = CACHE_NAMESPACE + "cfgDcRechargeWithdraw:map";
		// 从缓存中获取货币配置信息 参数(symbol)
		public static final String CFG_CURRENCY_CHARGE = CACHE_NAMESPACE + "cfgCurrencyCharge:";
		public static final String CURRENCY_CHARGE = CACHE_NAMESPACE + "cfgCurrencyCharge:isShow";
		// 从缓存中交易所id
		public static final String WHITE_EXCH_INFO = CACHE_NAMESPACE + "whiteExchInfo";
		public static final String WHITE_EXCH_INFO_LIST = CACHE_NAMESPACE + "whiteExchInfo:list";
		//缓存广告
		public static final String FRONT_ADVERT =CACHE_NAMESPACE+"frontAdvert:";
		//缓存公告
		public static final String FRONT_NOTICE =CACHE_NAMESPACE+"frontNotice:";
		//缓存版本控制
		public static final String BASE_VERSION=CACHE_NAMESPACE+"baseVersion:";
		//币种图标
		public static final String BASIC_SYMBOL_IMAGE=CACHE_NAMESPACE+"basicSymbolImageAll:";
		//描述模板
		public static final String CFG_DESCRIPTION_TEMPLATE =CACHE_NAMESPACE+"cfgDescriptionTemplate:";
		//交易所模板
		public static final String CFG_SYMBOL_DESCRIPTION =CACHE_NAMESPACE+"CfgSymbolDescription:";

		public static final String SYMBOL_DESCRIPTION =CACHE_NAMESPACE+"symbolDescription:";

		public static final String CMS_CONFIG_BIZ =CACHE_NAMESPACE+"cmsConfigBiz:";
		// 缓存list后缀
		public static final String LIST = ":list";
		//用户操作功能冻结
		public static final String FRONT_FREEZE_INFO = CACHE_NAMESPACE + "frontFreezeInfo";

		public static final String CASINO_REBATE_CONFIG = CACHE_NAMESPACE + "casinoRebateConfig";
	}

	public interface CommonType {

		/**
		 * 短信
		 */
		public static final String SMSCODE = CACHE_NAMESPACE + "SMSCODE";

		/**
		 * token
		 */
		public static final String TOKEN = CACHE_NAMESPACE + "TOKEN";

		/**
		 * 公链代币列表 取字典表
		 */
		public static final String SYMBOL_TOKEN_LIST = CACHE_NAMESPACE + "SYMBOL_LIST";

		/**
		 * 行情
		 */
		public static final String QUOTATION = CACHE_NAMESPACE + "QUOTATION";



		public static final String IF_TEST_ENV = CACHE_NAMESPACE + "ifTestEnv";

	}

	// 数据字典类型 dict_type
	public interface DirtTypeConstant {
		public final static String LANGUAGE = "language";
		public final static String TITLE = "_title";
		public final static String CHARGE_VALUE = "charge_value";
		public final static String SYMBOL_LIST = "symbol_list";
		public final static String USER_VALID = "User_Audit";// 用户审核不通过原因
		public final static String MERCHANT_VALID = "Merchant_Review";// 商户审核不通过原因
		public final static String WITHDRAW_VERIFY = "Withdraw_Backauth";// 提现审核不通过原因
		public final static String FB_SYMBOL ="fb_symbol" ;// 计价方式
//		public final static String COUNTING_SYMBOL ="Counting_symbol" ;// 计价方式
		public final static String PROTOCOL_TYPE_LIST ="protocol_type_list" ;// 多个链名称
		public final static String COMMISSION_LEVEL ="commission_level" ;// 手续费分成等级
		public final static String IFR_PLAN_CYCLE ="IFR_PLAN_CYCLE" ;// 方案周期
		public final static String IFR_FB_LIST ="IFR_FB_LIST" ;// IFR汇率币种列表
		public final static String UD_LEVEL ="UD_LEVEL" ;// UD申购等级配置
		public final static String USER_FEATURES_FREEZE ="User_Features_freeze" ;// UD申购等级配置
		public final static String NOTICE_MANAGER ="noticeManager" ;// UD申购等级配置
		public final static String RECHARGE_PROTOCOL ="recharge_protocol" ;//充值地址与链类型的关系
	}

	// 系统参数 base_param
	public interface BaseParam {
		public static final String SYSPARAM = CACHE_NAMESPACE + "baseParam:";
		public final static String EXCHANGE_LOCK_PASSWORD_BASIC = SYSPARAM + "exchange_lock_password_basic"; // 用户登录次数锁定时长，单位为小时
		public final static String EXCHANGE_LOCK_BASIC = SYSPARAM + "exchange_lock_basic"; // 用户修改信息后的锁定时间
		public final static String TOKEN_PARAM = SYSPARAM + "token_expire"; // 用户修改信息后的锁定时间
		public final static String TOKEN_ADMIN = SYSPARAM + "admin_token"; // 用户修改信息后的锁定时间
		public final static String TRANS_ORDER_EXPIRE = SYSPARAM + "trans_order_expire"; // 转账订单失效时间
		public final static String MCH_ORDER_EXPIRE = SYSPARAM + "mch_order_expire"; // 商户订单失效时间
		public final static String TRANS_COIN_ORDER_EXPIRE = SYSPARAM + "trans_coin_order_expire"; // 转币订单失效时间
		public final static String WITHDRAW_LIMIT = SYSPARAM + "withdraw_limit"; // 自动提币不能小于当前余额的倍数
		public final static String WITHDRAW_BALANCE_LIMIT = SYSPARAM + "withdraw_balance_limit"; // 自动提币限额(USDT)


	}


	// 星光分销 CasinoParam
	public interface CasinoParam {
		public static final String REBATE_CONDITION = "rebate_condition"; //推荐返佣条件,必须推荐3个以上才享受推荐返佣
		public static final String ACTIVATE_MEMBER = "activate_member"; //所有会员需要缴纳一万元等值的币（USDT）进行激活精英会员,根据此配置分配邀请返佣
		public static final String VISIT_CODE = "VISIT_CODE"; //顶级用户注册邀请码(赌场)

	}
}
