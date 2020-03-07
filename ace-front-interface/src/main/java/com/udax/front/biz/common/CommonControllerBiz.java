package com.udax.front.biz.common;

import com.github.wxiaoqi.security.common.configuration.KeyConfiguration;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.CfgChargeTemplate;
import com.github.wxiaoqi.security.common.entity.admin.CfgCurrencyCharge;
import com.github.wxiaoqi.security.common.entity.admin.CfgDcRechargeWithdraw;
import com.github.wxiaoqi.security.common.entity.admin.Param;
import com.github.wxiaoqi.security.common.entity.front.*;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.HttpUtils;
import com.github.wxiaoqi.security.common.util.SecurityUtil;
import com.github.wxiaoqi.security.common.util.SendUtil;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.udax.front.bean.WithdrawModel;
import com.udax.front.biz.*;
import com.udax.front.service.ServiceUtil;
import com.udax.front.util.CacheBizUtil;
import com.udax.front.vo.reqvo.merchant.MchWithdrawModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.github.wxiaoqi.security.common.constant.Constants.BaseParam.TOKEN_PARAM;
import static com.github.wxiaoqi.security.common.constant.Constants.BaseParam.WITHDRAW_BALANCE_LIMIT;
import static com.github.wxiaoqi.security.common.constant.Constants.BaseParam.WITHDRAW_LIMIT;

/**
 * app和pc端公用的接口
 */
@Service
@Slf4j
public  class CommonControllerBiz {


	@Autowired
	private UserAccountBiz userAccountBiz;

	@Autowired
	private BlockChainBiz blockChainBiz;

	@Autowired
	private CacheBiz cacheBiz;


	@Autowired
	private KeyConfiguration config;

	@Autowired
	private FrontWithdrawBiz frontWithdrawBiz;


	@Autowired
	private FrontWithdrawAddBiz frontWithdrawAddBiz;

	@Autowired
	private DcAssertAccountLogBiz accountLogBiz;//流水


	@Autowired
    private Environment env;
	/**
	 * 获取充值地址,
	 * @param symbol
	 * @param mchSybmol 商户传过来的用户标示,有可能为空
	 * @return
	 */
	@Transactional( rollbackFor = Exception.class)
    public String  getRechargetAdd(Long userId,String symbol,String mchSybmol){
		//白标标识
		String exchMark = CacheBizUtil.getExchInfo(BaseContextHandler.getExId(), cacheBiz);
		FrontTokenAddress queryParam = new FrontTokenAddress();
		queryParam.setUserId(userId);//用户Id
		queryParam.setType(FrontRechargeType.NORMAL.value());
		queryParam.setProxyCode(exchMark);//白标标示
		if(StringUtils.isNotBlank(mchSybmol)){
			queryParam.setMerchantUser(mchSybmol);//商户端用户标示
			queryParam.setType(FrontRechargeType.MERCHANT.value());
		}

		Map<String, Object> symbolMap = CacheBizUtil.getTokenListCache(cacheBiz);
		if (symbolMap != null && symbolMap.containsKey(symbol))
			queryParam.setSymbol(Constants.SYMBOL_ETH);
		else
			queryParam.setSymbol(symbol);
		//查询该用户是否存在对应币种对应的充值地址
		FrontTokenAddress tokenAddress = blockChainBiz.selectOne(queryParam);
		log.info(userId + ": "+ exchMark + ": " +queryParam.getSymbol());
		if (tokenAddress == null) {
//			// 分配钱包地址
			queryParam.setUserId(null);
			queryParam.setMerchantUser(null);
			queryParam.setEnable(EnableType.DISABLE.value());
			//如果不存在,查询未分配的地址列表
			List<FrontTokenAddress> tokenList = blockChainBiz.selectList(queryParam);
			if (tokenList.size() < 25) {
				String url = env.getProperty("blockchain.pushaddress.url") +"symbol=" + queryParam.getSymbol()+"&t_="+System.currentTimeMillis()
						+ "&platform=2&proxyCode=" + exchMark;//platform 1.交易所  2.钱包
				HttpUtils.asynget(url);
			}
			if (tokenList != null && !tokenList.isEmpty()) {
				//拿到列表中的地条数据  绑定用户
				tokenAddress = tokenList.get(0);

				//绑定使用户
				Example example = new Example(FrontTokenAddress.class);
				example.createCriteria().andEqualTo("enable",EnableType.DISABLE.value())
						.andEqualTo("id",tokenAddress.getId());
				FrontTokenAddress updateParam = new FrontTokenAddress();
				updateParam.setUserId(userId);
				updateParam.setType(FrontRechargeType.NORMAL.value());
				if(StringUtils.isNotBlank(mchSybmol)){
					updateParam.setMerchantUser(mchSybmol);
					updateParam.setType(FrontRechargeType.MERCHANT.value());
				}
				updateParam.setEnable(EnableType.ENABLE.value());// 占用钱包地址
				int count = blockChainBiz.updateByExampleSelective(updateParam,example);
				if( count < 1 ){
					return "";
				}
			}
			//tokenAddress = blockChainBiz.allotAddress(tokenUser.getSymbol(),userSymbol,userId);
		}
		//地址不为空,解密成真实的地址
		if (tokenAddress != null) {
			String address = SecurityUtil.decryptDes(tokenAddress.getUserAddress(),
					config.getWalletKey().getBytes());
			if (address.indexOf("_") > 0) {
				String[] addrs = address.split("_");
				tokenAddress.setUserAddress(address);
				tokenAddress.setTag(addrs[1]);
			} else {
				tokenAddress.setUserAddress(address);
			}
		}
		//充值地址
		String userAddress = tokenAddress == null || StringUtils.isBlank(tokenAddress.getUserAddress()) ? "" : tokenAddress.getUserAddress();
		return userAddress;
	}

	/**
	 *
	 * @param model  提币对象
	 * @param frontUser  用户
	 * @param type  提现类型 用户还是商户
	 * @param validAuth  是否身份验证
	 * @return
	 * @throws Exception
	 */
	public ObjectRestResponse withdraw(Object model, FrontUser frontUser, FrontWithdrawType type,boolean validAuth) throws Exception{
        String symbol = "";
        BigDecimal withdrawAmount = BigDecimal.ZERO;
        BigDecimal chargeAmount = BigDecimal.ZERO;
        String withdrawAdd = "";
        String tag = "";
        String mchNo = "";
        String nonceStr = "";
		String protocolType="";
        Long exId = frontUser.getUserInfo().getExchangeId();
        Integer withDrawType = FrontWithdrawType.NORMAL.value();
    	if(type == FrontWithdrawType.MERCHANT){
			MchWithdrawModel object = (MchWithdrawModel) model;
			symbol = object.getSymbol();
			withdrawAmount = object.getAmount();
			withdrawAdd = object.getAddress();
			tag = object.getTag();
			mchNo = object.getMchOrderNo();
			nonceStr = object.getNonceStr();
			withDrawType = FrontWithdrawType.MERCHANT.value();
			protocolType = object.getProtocolType();
		} else {
			WithdrawModel object = (WithdrawModel) model;
			symbol = object.getSymbol();
			withdrawAmount = object.getTradeAmount();
			chargeAmount = object.getChargeAmount();
			withdrawAdd = object.getUserAddress();
			tag = object.getTag();
			mchNo = String.valueOf(IdGenerator.nextId());
			protocolType = object.getProtocolType();
//			exId = BaseContextHandler.getAppExId();//用户提现用的是app带过来的id
			//nonceStr = String.valueOf(IdGenerator.nextId());
		}

		// 后台再次判断是否能提币,防止前端修改数据
		CfgDcRechargeWithdraw symbolConfig = CacheBizUtil.getSymbolConfigShow(symbol,protocolType,BaseContextHandler.getAppExId(),cacheBiz);
		System.out.println(symbolConfig);
		if (symbolConfig == null) {
			return new ObjectRestResponse<>().status(ResponseCode.WITHDRAW_50004);
		}
		if(!symbolConfig.getWithdrawStatus().equals(EnableType.ENABLE.value())){
			return new ObjectRestResponse<>().status(ResponseCode.WITHDRAW_50002);
		}
		//判断单笔最大提币量
		if(symbolConfig.getMaxWithdrawAmount().compareTo(withdrawAmount) < 0){
			return new ObjectRestResponse<>().status(ResponseCode.WITHDRAW_50006);
		}
		//判断单笔最小提鼻梁
		if(symbolConfig.getMinWithdrawAmount().compareTo(withdrawAmount) > 0){
			return new ObjectRestResponse<>().status(ResponseCode.WITHDRAW_50005);
		}
		//判断当日提币量
		BigDecimal withdrawDaily = frontWithdrawBiz.queryWithdrawDaily(frontUser.getId(),symbol);
		//当日提币量加上这次请求的总量大于当日限制提币总量
		if(withdrawDaily != null && withdrawDaily.add(withdrawAmount).compareTo(symbolConfig.getMaxWithdrawDay()) > 0){
			return new ObjectRestResponse<>().status(ResponseCode.WITHDRAW_50007);
		}

		CfgCurrencyCharge currencyCharge = CacheBizUtil.getSymbolCharge(cacheBiz,symbol,exId,protocolType);
		CfgChargeTemplate template = currencyCharge.getDcWithdrawCharge();//提现模板
		if( template == null){
			return new ObjectRestResponse<>().status(ResponseCode.WITHDRAW_50004);

		}

		//如果模板类型不是固定值和比例,提示错误
		if(!template.getChargeType().equals(ChargeTemplateType.FIXATION_VALUE.value()) &&
				!template.getChargeType().equals(ChargeTemplateType.RATIO.value())){
			return new ObjectRestResponse<>().status(ResponseCode.WITHDRAW_50009);
		}

		BigDecimal chargeValue = ChargeTemplateType.getChargeValue(template.getChargeType(),template.getChargeValue(), withdrawAmount);


		//判断后台计算的手续费和前台传过来的手续费是否一致
		if(type != FrontWithdrawType.MERCHANT){
			if(chargeValue.compareTo(chargeAmount) != 0){
				return new ObjectRestResponse().status(ResponseCode.WITHDRAW_50008);
			}
		}


		//判断手续费是否大于交易数量
		if(chargeValue.compareTo(withdrawAmount) >= 0){
			return new ObjectRestResponse<>().status(ResponseCode.WITHDRAW_50010);

		}

		//判断用户是否不能提币
		if (frontUser.getUserInfo().getIsWithdraw().intValue() != EnableType.ENABLE.value().intValue()) { // 判断是否为允许提币
			//String lockTime = CacheBizUtil.getWithdrawLockTime();
			return new ObjectRestResponse<>().status(ResponseCode.WITHDRAW_50003);
		}
		//判断用户身份认证通过
		if (frontUser.getUserInfo().getIsValid().intValue() != ValidType.AUTH.value().intValue() && validAuth) {
			return new ObjectRestResponse<>().status(ResponseCode.UNAUTH);
		}

		//查询资产
		DcAssetAccount entity = new DcAssetAccount();
		entity.setUserId(frontUser.getId());
		entity.setSymbol(symbol);
		DcAssetAccount account = userAccountBiz.selectOne(entity);
		//校验余额是否足够
		if (account == null || account.getAvailableAmount().compareTo(withdrawAmount) <= 0) {
			return new ObjectRestResponse<>().status(ResponseCode.BALANCE_NOT_ENOUGH);
		}




		boolean ifCanAutoWithdraw = ifCanAutoWithdraw(frontUser.getId(),symbol,withdrawAmount);

		//确认提币
		FrontWithdraw withdraw = new FrontWithdraw();
		withdraw.setArrivalAmoumt(withdrawAmount.subtract(chargeValue));
		withdraw.setChargeAmount(chargeValue);
		withdraw.setSymbol(symbol);
		withdraw.setTradeAmount(withdrawAmount);
		String address = withdrawAdd.trim();
		if (StringUtils.isNotEmpty(tag)) {
			address = withdrawAdd.trim() + "_" + tag;
		}
		withdraw.setUserAddress(address);
		withdraw.setUserId(frontUser.getId());
		//如果符合自动提币要求,状态变成已审核
		withdraw.setStatus(ifCanAutoWithdraw ? FrontWithdrawStatus.Audited.value() : FrontWithdrawStatus.WaitAuto.value());// 提交提币申请，待冻结资金
		withdraw.setAutoWithdraw(ifCanAutoWithdraw ? AutoWithdrawType.AUTO.value() : AutoWithdrawType.UNAUTO.value());
		withdraw.setTransNo(String.valueOf(IdGenerator.nextId()));
		withdraw.setCreateTime(new Date());
		withdraw.setType(withDrawType);//商户提币
		withdraw.setMchOrderNo(mchNo);
		withdraw.setNonceStr(nonceStr);
		String exchMark = CacheBizUtil.getExchInfo(exId, cacheBiz);
		withdraw.setProxyCode(exchMark);//交易所标识
		withdraw.setProtocolType(protocolType);//链类型
		Map<String, Object> symbolMap = CacheBizUtil.getTokenListCache(cacheBiz);
		if (symbolMap.containsKey(symbol)) {
			withdraw.setBasicSymbol(Constants.SYMBOL_ETH);
		} else {
			withdraw.setBasicSymbol(symbol);
		}
		//提交提币申请
		blockChainBiz.addWithdraw(withdraw); // 新增提币申请，冻结用户资金

		//添加用户提币记录
		FrontWithdrawAdd queryParam = new FrontWithdrawAdd();
		queryParam.setUserId(frontUser.getId());
		queryParam.setWithdrawAdd(address);
		FrontWithdrawAdd frontWithdrawAdd = frontWithdrawAddBiz.selectOne(queryParam);
		if(frontWithdrawAdd == null){
			frontWithdrawAdd = new FrontWithdrawAdd();
			frontWithdrawAdd.setWithdrawAdd(address);
			frontWithdrawAdd.setEnable(EnableType.ENABLE.value());
			frontWithdrawAdd.setUserId(frontUser.getId());
			frontWithdrawAdd.setSymbol(symbol);
			frontWithdrawAdd.setType(withDrawType);
			frontWithdrawAdd.setCreateTime(new Date());
			frontWithdrawAddBiz.insertSelective(frontWithdrawAdd);
			return new ObjectRestResponse<>();
		}
		//如果只是失效  重新启用
		if(frontWithdrawAdd.getEnable().intValue() == EnableType.DISABLE.value()){
			FrontWithdrawAdd updateParam = new FrontWithdrawAdd();
			updateParam.setId(frontWithdrawAdd.getId());
			updateParam.setEnable(EnableType.ENABLE.value());
			frontWithdrawAddBiz.updateSelectiveById(updateParam);
			return new ObjectRestResponse<>();
		}

       //向管理员发送提币审核钉钉群消息通知
		SendUtil.noticeManager(EmailTemplateType.WITHDRAW_AUDIT_REMIND.value(),frontUser.getUserName());

        return new ObjectRestResponse().data(withdraw);
	}

	//是否可以自动提币
	private boolean ifCanAutoWithdraw(Long userId,String symbol,BigDecimal withdrawAmount){
		//参数为空  不让自动提币
		Param limit_param = (Param) CacheUtil.getCache().get(WITHDRAW_LIMIT);//自动提币限额(USDT)
		Param balance_limit_param = (Param) CacheUtil.getCache().get(WITHDRAW_BALANCE_LIMIT);//自动提币不能小于当前余额的倍数
		if(limit_param == null || balance_limit_param == null){
			return false;
		}
		BigDecimal limit_usdt = new BigDecimal(limit_param.getParamValue());
		BigDecimal balance_limit = new BigDecimal(balance_limit_param.getParamValue());
		//查询最近一批流水
        DcAssetAccountLog logParam = new DcAssetAccountLog();
		logParam.setUserId(userId);
		logParam.setSymbol(symbol);
		DcAssetAccountLog assertLog = accountLogBiz.getLatestLog(logParam);
		if(assertLog == null){
			return false;
		}

		String lastPriceUrl = env.getProperty("udax.lastprices");
		BigDecimal symbolUsdtPrice =  ServiceUtil.getUSDTPrice(lastPriceUrl,symbol);
		if(symbolUsdtPrice == null || symbolUsdtPrice.compareTo(BigDecimal.ZERO) <= 0){
			log.info(symbol + "对应usdt价格为空或者小于0");
			return false;
		}
        //根据余额和参数确定最大能提币的数量
		BigDecimal balance_limit_amount = balance_limit.multiply(assertLog.getAfterAvailable());
		//提币数量对应的usdt数量
		BigDecimal withdraw_usdt_amount = withdrawAmount.multiply(symbolUsdtPrice);
		//提币数量小于参数限制的数量,自动提币
        if(withdraw_usdt_amount.compareTo(limit_usdt) <= 0 && withdrawAmount.compareTo(balance_limit_amount) <= 0){
        	return true;
		}
   		return false;
	}

}
