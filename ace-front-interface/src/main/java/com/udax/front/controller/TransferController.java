package com.udax.front.controller;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.*;
import com.github.wxiaoqi.security.common.entity.front.*;
import com.github.wxiaoqi.security.common.entity.ud.HParam;
import com.github.wxiaoqi.security.common.entity.ud.HUserInfo;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.support.Assert;
import com.github.wxiaoqi.security.common.util.*;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.github.wxiaoqi.security.common.util.model.CalType;
import com.github.wxiaoqi.security.common.vo.ValuationModeVo;
import com.udax.front.annotation.UserOperationAuthority;
import com.udax.front.biz.*;
import com.udax.front.biz.ud.HParamBiz;
import com.udax.front.biz.ud.HUserInfoBiz;
import com.udax.front.event.TransferCoinEvent;
import com.udax.front.event.TransferOrderEvent;
import com.udax.front.service.ServiceUtil;
import com.udax.front.util.BizControllerUtil;
import com.udax.front.vo.reqvo.*;
import com.udax.front.vo.rspvo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.udax.front.util.CacheBizUtil;

import javax.validation.Valid;

/**
 * 币币交易转账
 *
 * @author liuzz
 *
 */

@RestController
@RequestMapping("/wallet/transfer")
@Slf4j
public class TransferController extends BaseFrontController<DcAssertAccountBiz, DcAssetAccount> {

	@Autowired
	private TransferBiz transferBiz;

	@Autowired
	private TransferOrderBiz transferOrderBiz;


	@Autowired
	private TransferListBiz transferListBiz;

	@Autowired
	private UserOfferInfoBiz userOfferInfoBiz;

	@Autowired
	private FrontTransferDetailBiz frontTransferDetailBiz;

	@Autowired
	private DcAssertAccountBiz dcAssertAccountBiz;

	@Autowired
	private FrontUserBiz frontUserBiz;

    @Autowired
    private CacheBiz cacheBiz;

    @Autowired
    private HParamBiz paramBiz;

    @Autowired
    private Environment env;

    @Autowired
    private HUserInfoBiz userInfoBiz;

	/**
	 * 行情
	 *
	 * @return
	 */
	@PostMapping("quotation")
	public ObjectRestResponse quotation() {
		String url = env.getProperty("udax.quotation");//udax行情接口
		log.info("请求地址:" + url);
		String returnJson = HttpUtils.postJson(url,"");
		log.info("行情接口返回数据:" + returnJson);
		List<BasicSymbol> basicSymbolList = CacheBizUtil.getBasicSymbolExchNotRepeat(cacheBiz,BaseContextHandler.getAppExId());
		Map<String,BasicSymbol> basicSymbolMap = basicSymbolList.stream().collect(Collectors.toMap(BasicSymbol::getSymbol,BasicSymbol -> BasicSymbol));
		UdaxQuotationBean jsonBean = JSON.parseObject(returnJson,UdaxQuotationBean.class);
		List<JSONObject> rateList = (List<JSONObject>) jsonBean.getData().get("rateList");
        Map<String,String> currencyTypeMap = InstanceUtil.newHashMap();//法币列表
		if(StringUtil.listIsNotBlank(rateList)){
			JSONObject inbiqBean = new JSONObject();
			inbiqBean.put("currencyName","USDINBIQ");//ifr项目
			JSONObject udBean = new JSONObject();
			udBean.put("currencyName","USDUD");//ud社区项目
			rateList.stream().forEach((l)->{
				if(l.get("currencyName").equals("USDUSD")){
					inbiqBean.put("rateMap",l.get("rateMap"));
					currencyTypeMap.put("USDINBIQ","");
					udBean.put("rateMap",l.get("rateMap"));
					currencyTypeMap.put("USDUD","");
				}else {
					currencyTypeMap.put((String)l.get("currencyName"),"");
				}
			});
			rateList.add(inbiqBean);
			rateList.add(udBean);

		}
		Map<String,JSONObject> quotationBeanMap = (Map<String,JSONObject>) jsonBean.getData().get("quotation");
		Map<String,QuotationBean> newQuotationBeanMap  = InstanceUtil.newHashMap();
		quotationBeanMap.forEach((k,v) ->{
			if(!k.contains("/"+Constants.QUOTATION_DCCODE)){
				return;
			}
			//获取代币
			String[] dcCode = k.split("/");
			//如果钱包基础币种list里不存在,那么过滤
			BasicSymbol symbol = basicSymbolMap.get(dcCode[0]);
			if( symbol == null) {
				return;
			}
			CfgCurrencyCharge charge = CacheBizUtil.getSymbolCharge(cacheBiz,dcCode[0],BaseContextHandler.getAppExId(),null);
			BigDecimal spread = BigDecimal.ZERO;
			if(charge != null){
				spread = charge.getSpread();
			}

			QuotationBean bean = new QuotationBean();
			bean.setLastPrice(v.getBigDecimal("lastPrice"));
			bean.setSymbol(v.getString("symbol"));
			bean.setSpread(spread);
			bean.setDecimalPlaces(symbol.getDecimalPlaces());
			BigDecimal openPrice = v.getBigDecimal("openPrice");
			bean.setOpenPrice(openPrice);
            if(openPrice == null || bean.getLastPrice() == null
					|| bean.getLastPrice().compareTo(BigDecimal.ZERO) <= 0){
            	bean.setRose(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_DOWN));
			} else {
            	bean.setRose(bean.getLastPrice().subtract(openPrice).divide(openPrice,2,BigDecimal.ROUND_DOWN));
			}
			newQuotationBeanMap.put(k,bean);
		});

        //获取默认结算币种
		List<ValuationModeVo> list = CacheBizUtil.getValuationManner(cacheBiz,BaseContextHandler.getAppExId(),BaseContextHandler.getLanguage());
        String defaultLtCode = CurrencyType.USA.value();;//交易所的结算币种
		if(StringUtil.listIsNotBlank(list)){
			for(ValuationModeVo l : list) {
				//查找默认的结算币种
				if (StringUtils.isNotBlank(l.getDefaultSymbol())) {
					defaultLtCode = l.getDefaultSymbol();
					break;
				}
			}
		}
		if(currencyTypeMap.get("USD" + defaultLtCode) == null){
			defaultLtCode = CurrencyType.USA.value();
		}



		Map<String,Object> result = InstanceUtil.newHashMap("rateList",rateList);
		result.put("quotation",newQuotationBeanMap);
		result.put("currencyType",defaultLtCode);//默认显示的法币币种
		result.put("market",Constants.QUOTATION_DCCODE);//默认取行情的市场
		return new ObjectRestResponse().data(result);
	}


    /**
     * 获取当前币种可转换的币种列表
     *
     * @return
     */
    @PostMapping("symbols")
    public ObjectRestResponse<List<CoinVo>> transferList() {


		//查询所有可用的用户报价
    	UserOfferInfo param = new UserOfferInfo();
		param.setStatus(EnableType.ENABLE.value());
		List<UserOfferInfo> userOfferInfoList = userOfferInfoBiz.selectList(param);
		//srcsymbol+dstsymbol作为key
		Map<String,UserOfferInfo> map = userOfferInfoList.stream().collect(Collectors.toMap(UserOfferInfo->UserOfferInfo.getSrcSymbol()
				+ UserOfferInfo.getDstSymbol(),UserOfferInfo->UserOfferInfo));

        ObjectRestResponse<List<CoinVo>> result = new ObjectRestResponse<List<CoinVo>>().rel(true);
        List<CfgCurrencyTransfer> transferList = CacheBizUtil.getSymbolTransferList(cacheBiz,BaseContextHandler.getAppExId());//通用配置
		List<TransferExch> transferExchList = CacheBizUtil.getTransferExchList(BaseContextHandler.getAppExId(), cacheBiz);//白标配置
        setTransAmount(transferList,transferExchList);//设置最大最小转币数量
        List<CoinVo> coinList = new ArrayList<CoinVo>();
        for (CfgCurrencyTransfer transferConfig : transferList) {
        	if(StringUtils.isBlank(transferConfig.getSrcSymbol())){
        		continue;
			}
            CoinVo vo = new CoinVo();
            vo.setSymbol(transferConfig.getSrcSymbol());
            List<CoinVo.TargetVo> targetList = new ArrayList<CoinVo.TargetVo>();
            for (CfgCurrencyTransfer transfer : transferConfig.getDstList()) {
				CoinVo.TargetVo targetVo = new CoinVo().new TargetVo();
				targetVo.setSymbol(transfer.getDstSymbol());
				targetVo.setChargeSymbol(transfer.getChargeDcCode());
				targetVo.setMinTransAmount(transfer.getMinTransAmount());
				targetVo.setMaxTransAmount(transfer.getMaxTransAmount());
				targetVo.setChargeAmount(transfer.getCfgChargeTemplate().getChargeValue());
				targetVo.setChargeType(transfer.getCfgChargeTemplate().getChargeType());
				targetVo.setTransferType(transfer.getTransferType());//用户报价类型
				//如果是用户报价类型的用户报价信息
				if(transfer.getTransferType().equals(CurrencyTransferType.USER_OFFER.value())){
					UserOfferInfo userOfferInfo = map.get(transfer.getDstSymbol()+transferConfig.getSrcSymbol());
					//如果没有用户报价订单信息
					if(userOfferInfo == null){
						continue;
					}
					targetVo.setAvailableAmount(userOfferInfo.getRemainVolume());
					targetVo.setLastPrice(userOfferInfo.getOrderPrice());
				}
                targetList.add(targetVo);
            }
            vo.setTargetList(targetList);

            coinList.add(vo);
        }
        result.setData(coinList);
        return result;
    }

	private void setTransAmount(List<CfgCurrencyTransfer> transferList, List<TransferExch> transferExchList) {

		Map<String, TransferExch> map = InstanceUtil.newHashMap();
		if(StringUtil.listIsNotBlank(transferExchList)){
			transferExchList.stream().forEach((f) -> {
				map.put(f.getSrcSymbol() + File.separator + f.getDstSymbol(),f);
			});
		}

		for (CfgCurrencyTransfer transferConfig : transferList) {
			if(StringUtils.isBlank(transferConfig.getSrcSymbol())){
				continue;
			}
			//优先使用白标的配置,没有使用交易所配置
			for (CfgCurrencyTransfer transfer : transferConfig.getDstList()) {
				TransferExch transferExch = map.get(transferConfig.getSrcSymbol()+File.separator+transfer.getDstSymbol());
				if(transferExch != null && transferExch.getCfgChargeTemplate() != null){
					transfer.setMinTransAmount(transferExch.getMinTransAmount());
					transfer.setMaxTransAmount(transferExch.getMaxTransAmount());
				}

			}

		}

	}


	/**
	 * 币币装换预下单
	 *
	 * @return
	 * @throws Exception
	 */
	@UserOperationAuthority("TRANSFER_COIN") //判断当前用户币币交易权限是否被禁用
	@PostMapping("transferCoinPreOrder")
	public ObjectRestResponse transferCoinPreOrder(@RequestBody TransferVo vo) throws  Exception{
		ObjectRestResponse result = new ObjectRestResponse().rel(true);
		CfgCurrencyTransfer transferConfig = CacheBizUtil.getSymbolTransfer(vo.getSymbol(), vo.getTargetSymbol(), cacheBiz);
		TransferExch transferExch = CacheBizUtil.getTransferExch(vo.getSymbol(), vo.getTargetSymbol(),BaseContextHandler.getAppExId(), cacheBiz);
		if(transferConfig == null || transferConfig.getCfgChargeTemplate() == null || vo.getTargetSymbol().equals(vo.getSymbol()) ||  transferExch == null
				|| transferExch.getIsOpen().intValue() == EnableType.DISABLE.value()){
			result.status(ResponseCode.TRANSFER_60007);
			return result;
		}

	    CfgChargeTemplate template = transferExch.getCfgChargeTemplate() == null ? transferConfig.getCfgChargeTemplate() : transferExch.getCfgChargeTemplate();
		BigDecimal minTransAmount = transferExch.getMinTransAmount() == null ? transferConfig.getMinTransAmount() : transferExch.getMinTransAmount();
		BigDecimal maxTransAmount = transferExch.getMaxTransAmount() == null ?  transferConfig.getMaxTransAmount() :  transferExch.getMaxTransAmount();

		//判断是否超过最大最小转换额度
		if (vo.getTransferAmount().compareTo(minTransAmount) < 0) {
			result.status(ResponseCode.TRANSFER_60001);
			return result;
		}
		if (vo.getTransferAmount().compareTo(maxTransAmount) > 0) {
			result.status(ResponseCode.TRANSFER_60002);
			return result;
		}
		//收取的手续费币种必须是转换的交易对中的一个
		if(!transferConfig.getChargeDcCode().equals(vo.getSymbol()) &&
				!transferConfig.getChargeDcCode().equals(vo.getTargetSymbol())){
			result.status(ResponseCode.TRANSFER_60005);
			return result;
		}
        //判断是否余额足够
		DcAssetAccount account = dcAssertAccountBiz.lockRecord(vo.getSymbol(),BaseContextHandler.getUserID());
		if(account == null || account.getAvailableAmount().compareTo(vo.getTransferAmount()) < 0){
			return new ObjectRestResponse().status(ResponseCode.BALANCE_NOT_ENOUGH);
		}


        FrontTransferDetail transferDetail = new FrontTransferDetail();
		transferDetail.setOrderNo(String.valueOf(IdGenerator.nextId()));//订单号
		transferDetail.setUserId(BaseContextHandler.getUserID());//转币用户id
		transferDetail.setSrcSymbol(vo.getSymbol());//转币用户持有代币/接收用户接收币种
		transferDetail.setDstSymbol(vo.getTargetSymbol());//转币用户待转代币/接收用户持有币种
		transferDetail.setChargeCurrencyCode(transferConfig.getChargeDcCode());//收取的手续费币种
		transferDetail.setSrcAmount(vo.getTransferAmount());//转换的数量
		transferDetail.setDstCurrencyType(CurrencyInfoType.COIN_TRANSFER.value());

		BigDecimal lastPrice = BigDecimal.ZERO;
		//用户报价
		if(transferConfig.getTransferType().equals(CurrencyTransferType.USER_OFFER.value())){
			UserOfferInfo offerInfo = transferBiz.getUserOffInfo(vo.getTargetSymbol(),vo.getSymbol());
			if (offerInfo == null || offerInfo.getOrderPrice() == null) {
				result.status(ResponseCode.TRANSFER_60003);
				return result;
			}
			lastPrice = offerInfo.getOrderPrice();
			transferDetail.setReceiveUserId(offerInfo.getFrontUser().getId());
			if (transferDetail.getReceiveUserId().longValue() == BaseContextHandler.getUserID().longValue()) {
				return new ObjectRestResponse().status(ResponseCode.TRANSFER_60008);
			}
		} else if(transferConfig.getTransferType().equals(CurrencyTransferType.PLATFORM_HEDGE_FLAG.value())){
			//如果是平台对冲类型的
			//获取交易对最新行情
			String url = env.getProperty("udax.quotation");//udax行情接口
			String returnJson = HttpUtils.postJson(url,"");
			UdaxQuotationBean jsonBean = JSON.parseObject(returnJson,UdaxQuotationBean.class);
			//获取交易对的最新价格
			if(jsonBean == null || jsonBean.getCode().intValue() != 200){
				result.status(ResponseCode.TRANSFER_60004);
				return result;
			}
			BigDecimal srcSymbolPrice = BigDecimal.ZERO;
			BigDecimal dstSymbolPrice = BigDecimal.ZERO;
			Map<String,JSONObject> quotationBeanMap = (Map<String,JSONObject>) jsonBean.getData().get("quotation");
			if(quotationBeanMap != null){
				for(Map.Entry<String, JSONObject> s : quotationBeanMap.entrySet()){
					if(Constants.QUOTATION_DCCODE.equals(vo.getSymbol())){
						srcSymbolPrice = BigDecimal.ONE;
					}
					if(Constants.QUOTATION_DCCODE.equals(vo.getTargetSymbol())){
						dstSymbolPrice = BigDecimal.ONE;
					}
					if(s.getKey().equals(vo.getSymbol()+"/"+Constants.QUOTATION_DCCODE)){
						srcSymbolPrice = s.getValue().getBigDecimal("lastPrice");
					}
					if(s.getKey().equals(vo.getTargetSymbol()+"/"+Constants.QUOTATION_DCCODE)){
						dstSymbolPrice = s.getValue().getBigDecimal("lastPrice");

					}
				}
			}

			if(srcSymbolPrice.compareTo(BigDecimal.ZERO) <= 0 || dstSymbolPrice.compareTo(BigDecimal.ZERO) <= 0){
				result.status(ResponseCode.TRANSFER_60004);
				return result;
			}

			//计算转账手续费[获取不带链类型手续费配置] 行情点差
			CfgCurrencyCharge charge = CacheBizUtil.getSymbolCharge(cacheBiz,vo.getTargetSymbol(),BaseContextHandler.getAppExId(),null);
			BigDecimal spread = BigDecimal.ZERO;
			if(charge != null){
				spread = charge.getSpread();
			}
			lastPrice = srcSymbolPrice.divide(dstSymbolPrice.add(spread),8,BigDecimal.ROUND_UP);

		}

		if(lastPrice.compareTo(BigDecimal.ZERO) <= 0){
			result.status(ResponseCode.TRANSFER_60004);
			return result;
		}

		BigDecimal charge = BigDecimal.ZERO;
		//如果收取的待兑换币种,先扣除手续费
		if(transferConfig.getChargeDcCode().equals(vo.getSymbol())){
			charge = ChargeTemplateType.getChargeValue(template.getChargeType(),
					template.getChargeValue(),vo.getTransferAmount());
			if(charge.compareTo(vo.getTransferAmount()) >= 0){
				result.status(ResponseCode.TRANSFER_60006);
				return result;
			}
			//计算兑换的对手币的数量
			BigDecimal dstAmount = transferDetail.getSrcAmount().subtract(charge).multiply(lastPrice).setScale(8,BigDecimal.ROUND_DOWN);
			transferDetail.setDstAmount(dstAmount);
		}
		//如果收取的兑换目标币种,先计算能兑换多少对手币,再计算手续费
		if(transferConfig.getChargeDcCode().equals(vo.getTargetSymbol())){
			//先计算数量再扣除手续费
			BigDecimal dstAmount = transferDetail.getSrcAmount().multiply(lastPrice).setScale(8,BigDecimal.ROUND_UP);
			charge = ChargeTemplateType.getChargeValue(template.getChargeType(),
					template.getChargeValue(),dstAmount);
			if(charge.compareTo(dstAmount) >= 0){
				result.status(ResponseCode.TRANSFER_60006);
				return result;
			}
			transferDetail.setDstAmount(dstAmount.subtract(charge).setScale(8,BigDecimal.ROUND_DOWN));
		}
		//transferDetail.setHedgeFlag(transferConfig.getTransferType()); //1.平台对冲 2.后台用户报价
        transferDetail.setTransTargetType(transferConfig.getTransferType()); //1.平台对冲 2.后台用户报价
		transferDetail.setHedgeFlag(transferConfig.getHedgeFlag());//1.平台对冲 2.后台用户报价
		transferDetail.setSrcCurrencyType(CurrencyInfoType.COIN_TRANSFER.value());
		transferDetail.setDstCurrencyType(CurrencyInfoType.COIN_TRANSFER.value());
		transferDetail.setTransPrice(lastPrice);
		transferDetail.setChargeAmount(charge);
		transferDetail.setCreateTime(new Date());
		transferDetail.setStatus(TransferOrderStatus.UNPAY.value());
		//计算订单失效时间
		Date expireTime = CacheBizUtil.getExpireTime(Constants.BaseParam.TRANS_COIN_ORDER_EXPIRE,cacheBiz);
		transferDetail.setExpireTime(expireTime);
		transferBiz.insertSelective(transferDetail);

		TransCoinRspVo rspVo = new TransCoinRspVo();
		BeanUtils.copyProperties(transferDetail,rspVo);
		rspVo.setChargeSymbol(transferDetail.getChargeCurrencyCode());
		return new ObjectRestResponse<>().data(rspVo);
	}




    /**
     * 币币之间转换
     *
     * @return
     * @throws Exception
     */
    @PostMapping("transferCoin")
    public ObjectRestResponse<String> transferCoin(@RequestBody @Valid TransferByOrderNoModel model) throws Exception {

		Assert.length(model.getPassword(), 6, 6, "TRADE_PASSWORD");
		// 待转账用户
		FrontUser transferUser = frontUserBiz.selectById(BaseContextHandler.getUserID());
		if (transferUser == null) {
			return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
		}
		// 判断支付密码
		boolean flag = ServiceUtil.judgePayPassword(model.getPassword(), BaseContextHandler.getUserID(), frontUserBiz);
		if (!flag) {
			return new ObjectRestResponse().status(ResponseCode.TRADE_PWD_ERROR);
		}
		// 转换币
		FrontTransferDetail detail = transferBiz.transfer(model.getOrderNo());
		publisher.publishEvent(new TransferCoinEvent(this,detail,BaseContextHandler.getAppExId()));
		return new ObjectRestResponse<>();
    }

	// 转账预下单
	@UserOperationAuthority("USER_TRANSFER") //判断当前用户转账权限是否被禁用
	@PostMapping("/transferPreOrder")
	public Object transferByAccount(@RequestBody @Valid TransferPreOrderModel model) {
		// 收款用户
		FrontUser user = ServiceUtil.selectUnionUserInfoByUserName(model.getUsername(), frontUserBiz);
		if (user == null || user.getUserInfo() == null) {
			return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
		}

		// 转账用户
		FrontUser transferUser = frontUserBiz.selectById(BaseContextHandler.getUserID());
		if (transferUser == null) {
			return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
		}
		if (user.getId().longValue() == BaseContextHandler.getUserID()) {
			return new ObjectRestResponse().status(ResponseCode.TRANSFER_FAIL);
		}

		HParam param = ServiceUtil.getUdParamByKey("MIN_TRANSFER",paramBiz);
        if( param != null){
			HUserInfo userInfoParam = new HUserInfo();
			userInfoParam.setUserId(BaseContextHandler.getUserID());
			HUserInfo hUserInfo = userInfoBiz.selectOne(userInfoParam);
			//如果没投资过,判断转账限制
			if(hUserInfo != null && hUserInfo.getIsValid().equals(EnableType.DISABLE.value())){
				//转账配置,判断最小转账数量
				//CfgDcRechargeWithdraw transferConfig = CacheBizUtil.getSymbolConfig(model.getDcCode(),BaseContextHandler.getAppExId(),cacheBiz);
				//if(transferConfig != null){
				BigDecimal minTransferAmount = new BigDecimal(param.getUdValue());
			//	BigDecimal minTransferAmount = transferConfig.getMinTransferAmount();
					//不能小于最小转账数量
				HParam symbol = ServiceUtil.getUdParamByKey("HOME_SYMBOL_TWO",paramBiz);
					if(symbol !=null && symbol.getUdValue().equals(model.getDcCode()) && minTransferAmount != null && model.getTransferAmount().compareTo(minTransferAmount) < 0){
						return new ObjectRestResponse().status(ResponseCode.MIN_TRANSFER);
					}
				//}
			}

		}





		//余额是否足够
        DcAssetAccount dcAssetAccount = new DcAssetAccount();
		dcAssetAccount.setSymbol(model.getDcCode());
		dcAssetAccount.setUserId(BaseContextHandler.getUserID());
		dcAssetAccount = dcAssertAccountBiz.selectOne(dcAssetAccount);
		if(dcAssetAccount == null || dcAssetAccount.getAvailableAmount().compareTo(model.getTransferAmount()) < 0){
			return new ObjectRestResponse().status(ResponseCode.BALANCE_NOT_ENOUGH);
		}

        Date expireTime = CacheBizUtil.getExpireTime(Constants.BaseParam.TRANS_ORDER_EXPIRE,cacheBiz);

        //计算转账手续费[不带链类型]
		CfgCurrencyCharge charge = CacheBizUtil.getSymbolCharge(cacheBiz,model.getDcCode(),BaseContextHandler.getAppExId(),null);
		BigDecimal chargeAmount = BigDecimal.ZERO;
        if(charge != null){
           CfgChargeTemplate template = charge.getTradeCharge();
           chargeAmount = ChargeTemplateType.getChargeValue(template.getChargeType(),template.getChargeValue(),model.getTransferAmount());
		}
		if(chargeAmount.compareTo(model.getTransferAmount()) >= 0 ){
			return new ObjectRestResponse<>().status(ResponseCode.TRANSFER_60006);
		}


		TransferOrder transferRecord = new TransferOrder();
		transferRecord.setOrderNo(String.valueOf(IdGenerator.nextId()));
		transferRecord.setUserId(BaseContextHandler.getUserID());
		transferRecord.setUserName(transferUser.getUserName());
		transferRecord.setReceiveUserId(user.getId());
		transferRecord.setReceiveUserName(user.getUserName());
		transferRecord.setAmount(model.getTransferAmount());
		transferRecord.setChargeAmount(chargeAmount.setScale(8,BigDecimal.ROUND_UP));
		transferRecord.setArrivalAmount(model.getTransferAmount().subtract(chargeAmount));
		transferRecord.setSymbol(model.getDcCode());
		transferRecord.setCreateTime(LocalDateUtil.localDateTime2Date(LocalDateTime.now()));
        //过期时间=当前时间+过期时间配置
        transferRecord.setExpireTime(expireTime);
		transferRecord.setStatus(TransferOrderStatus.UNPAY.value());
		if(StringUtils.isNotBlank(model.getRemark())){
			transferRecord.setRemark(model.getRemark());
		}

		transferOrderBiz.insertSelective(transferRecord);

		TransOrderRspVo rspVo = new TransOrderRspVo();
		BeanUtils.copyProperties(transferRecord, rspVo);
		if(StringUtils.isNotBlank(user.getUserInfo().getFirstName()) &&
				StringUtils.isNotBlank(user.getUserInfo().getRealName()) ){
			rspVo.setRealName("*"+user.getUserInfo().getRealName());
		}

		return new ObjectRestResponse().data(rspVo);
	}

	// 转账
	@PostMapping("/transByOrderNo")
	public Object transByOrderNo(@RequestBody @Valid TransferByOrderNoModel model) {
		Assert.length(model.getPassword(), 6, 6, "TRADE_PASSWORD");
		// 待转账用户
		FrontUser transferUser = frontUserBiz.selectById(BaseContextHandler.getUserID());
		if (transferUser == null) {
			return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
		}
		// 判断支付密码
		boolean flag = ServiceUtil.judgePayPassword(model.getPassword(), BaseContextHandler.getUserID(), frontUserBiz);
		if (!flag) {
			return new ObjectRestResponse().status(ResponseCode.TRADE_PWD_ERROR);
		}
		// 转账
		ObjectRestResponse response = baseBiz.transferAssert(model.getOrderNo());
        if(response.isRel()){
			publisher.publishEvent(new TransferOrderEvent(this,(TransferOrder)response.getData(),BaseContextHandler.getAppExId()));
		}
		return response.data(null);
	}


	// 转账记录
	@PostMapping("/transOrderList")
	public Object transList(@RequestBody @Valid PageInfo pageInfo) throws Exception {
		Long userId = BaseContextHandler.getUserID();
		Map<String,Object> map = BizControllerUtil.modelToMap(pageInfo);
		map.put("userId", userId);
		map.put("status",TransferOrderStatus.PAYED.value());//显示转账成功的记录
		map.put("orderByInfo","create_time desc");
		Query query = new Query(map);
    	TableResultResponse rsplist = pageQueryTransToVo(query,TransferOrder.class,TransRecordVo.class,transferOrderBiz);
		// 转账
		return rsplist;
	}



	// 收款记录
	@PostMapping("/receiveOrderList")
	public Object receiveOrderList(@RequestBody @Valid PageInfo pageInfo) throws Exception {
		Long userId = BaseContextHandler.getUserID();
		Map<String,Object> map = BizControllerUtil.modelToMap(pageInfo);
		map.put("receiveUserId", userId);
		map.put("status",TransferOrderStatus.PAYED.value());//显示转账成功的记录
		map.put("orderByInfo","create_time desc");
		Query query = new Query(map);
		TableResultResponse rsplist = pageQueryTransToVo(query,TransferOrder.class,TransRecordVo.class,transferOrderBiz);
		// 转账
		return rsplist;
	}



	// 转币记录
	@PostMapping("/transCoinList")
	public Object transCoinList(@RequestBody @Valid PageInfo pageInfo) throws Exception {
		Long userId = BaseContextHandler.getUserID();
		Map<String,Object> map = BizControllerUtil.modelToMap(pageInfo);
		map.put("userId", userId);
		map.put("status",TransferOrderStatus.PAYED.value());//显示转币成功的记录
		map.put("orderByInfo","create_time desc");
		Query query = new Query(map);
		TableResultResponse rsplist = pageQueryTransToVo(query,FrontTransferDetail.class,TransCoinRecordVo.class,frontTransferDetailBiz);
		// 转账
		return rsplist;
	}

	// 转账好友列表
	@PostMapping("/transList")
	public Object transList() {
		FrontUser transferUser = frontUserBiz.selectById(BaseContextHandler.getUserID());
		if (transferUser == null) {
			return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
		}
		TransferList param = new TransferList();
		param.setUserId(BaseContextHandler.getUserID());
        List<TransferList> resultList = transferListBiz.getListUnionUserInfo(param);
		List<TransListRspVo> rspList = new ArrayList<>();
		resultList.stream().forEach((r) ->{
			TransListRspVo vo = new TransListRspVo();
			vo.setIsValid(r.getFrontUserInfo().getIsValid());
			vo.setSecondName(r.getFrontUserInfo().getRealName());
			vo.setUserName(r.getReceiveUserName());
			rspList.add(vo);
		});
		// 转账
		return new ObjectRestResponse<>().data(rspList);
	}



//	// 生成转账相关二维码
//	@PostMapping("/qrcode")
//	public Object transferByAccount(@RequestBody @Valid QRCodeModel model) throws Exception {
//		// 对象转成排序map
//		TreeMap<String, Object> map = new  TreeMap<String,Object>();
//		map.put("type",model.getType());
//		if (!QRCodeType.isType(Integer.valueOf(model.getType()))) {
//			return new ObjectRestResponse<>().status(ResponseCode.QRCODE_TYPE_ERROR);
//		}
//		StringBuilder append = new StringBuilder();
//		//如果是收款二维码
//		if(QRCodeType.RECEIVE_CODE.value().equals(model.getType())){
//			if (model.getAmount() != null && StringUtils.isNotBlank(model.getDcCode())) {
//				if (model.getAmount().compareTo(new BigDecimal(0.00000001)) < 0) {
//					return new ObjectRestResponse<>().status(ResponseCode.MERCHANT_AMOUNT_LIMIT);
//				}
//				//有金额必须有代币
//				//验证代币
//				if(StringUtils.isNotBlank(model.getDcCode())){
//					boolean flag = ServiceUtil.validDcCode(model.getDcCode(),cacheBiz);
//					if(!flag) return new ObjectRestResponse<>().status(ResponseCode.DC_INVALID);
//				}
//				map.put("amount",model.getAmount());
//				map.put("dcCode",model.getDcCode());
//			}
//			map.put("userName", BaseContextHandler.getUsername());// 加入用户名
//			map.forEach((k, v) -> {
//				append.append(k + "=" + v + "&");
//			});
//		}
//
////		else if(QRCodeType.RECHARGE_CODE.value().equals(model.getType())){
////			//充值二维码
////
////
////		}
//		// 拼接成key value的字符串
//		String appendStr = append.substring(0, append.length() > 1 ? append.length() - 1 : 0);
//		// 用双向加密算法加密
//		String signStr = SecurityUtil.encryptDes(appendStr, config.getSignKey().getBytes());
//		append.delete(0, append.length());
//		// 生成字符串
//		String returnStr = append.append(Constants.CACHE_NAMESPACE).append("//").append(signStr).toString();
//		// 生成二维码
//		QrcodeUtil.produceQR(response.getOutputStream(), returnStr, 350, 350);
//		return new ObjectRestResponse();
//	}

}
