package com.udax.front.controller.ifr;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.front.FrontUserInfo;
import com.github.wxiaoqi.security.common.entity.ifr.IfrExchangeRate;
import com.github.wxiaoqi.security.common.entity.ifr.IfrPayOrder;
import com.github.wxiaoqi.security.common.entity.ifr.IfrPlan;
import com.github.wxiaoqi.security.common.entity.merchant.Merchant;
import com.github.wxiaoqi.security.common.enums.IfrOrderStatus;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.HttpUtils;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.WebUtil;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.thoughtworks.xstream.XStream;
import com.udax.front.bean.XMLModel;
import com.udax.front.biz.CacheBiz;
import com.udax.front.biz.FrontUserInfoBiz;
import com.udax.front.biz.ifr.IfrExchangeRateBiz;
import com.udax.front.biz.ifr.IfrPayOrderBiz;
import com.udax.front.biz.ifr.IfrPlanBiz;
import com.udax.front.biz.merchant.MerchantBiz;
import com.udax.front.configuration.IfrConfiguration;
import com.udax.front.controller.BaseFrontController;
import com.udax.front.util.BizControllerUtil;
import com.udax.front.util.SignUtil;
import com.udax.front.vo.reqvo.ifr.IfrPayOrderModel;
import com.udax.front.vo.rspvo.UdaxQuotationBean;
import com.udax.front.vo.rspvo.ifr.IfrPlanRspVo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liuzz
 * @create IFR 现金充值
 */
@RestController
@RequestMapping("/wallet/ifr/")
public class IfrRechargeController extends BaseFrontController<MerchantBiz,Merchant> {

	@Autowired
	private IfrPlanBiz planBiz;

    @Autowired
	private IfrPayOrderBiz payOrderBiz;

	@Autowired
	private CacheBiz cacheBiz;

	@Autowired
	private Environment env;

	protected Logger logger = LogManager.getLogger();

	@Autowired
	private IfrExchangeRateBiz exchangeRateBiz;

	@Autowired
	private FrontUserInfoBiz userInfoBiz;

	@Autowired
	private IfrConfiguration ifrConfiguration;

	@PostMapping("index")
	public ObjectRestResponse index() throws Exception{


		String url = env.getProperty("udax.quotation");//udax行情接口
		String returnJson = HttpUtils.postJson(url,"");
		UdaxQuotationBean jsonBean = JSON.parseObject(returnJson,UdaxQuotationBean.class);
		List<JSONObject> rateList = (List<JSONObject>) jsonBean.getData().get("rateList");

		//找ifr/udst交易对价格·
		BigDecimal ifrUsdtPrice = null;
		Map<String,JSONObject> quotationBeanMap = (Map<String,JSONObject>) jsonBean.getData().get("quotation");
		for(Map.Entry<String,JSONObject> set : quotationBeanMap.entrySet()){
			String k = set.getKey();
			JSONObject v = set.getValue();
			if(k.contains("IFR/"+Constants.QUOTATION_DCCODE)){
				Object ob = v.getBigDecimal("lastPrice");
				if(ob != null){
					ifrUsdtPrice = (BigDecimal) ob;
				}
			}
		}
		if(ifrUsdtPrice == null){
			return new ObjectRestResponse().status(ResponseCode.PARAM_ERROR);
		}

		BigDecimal ifrUsdPrice = null;
		for(JSONObject l : rateList) {
			if (l.get("currencyName").equals("USDUSD")) {
				Map<String, BigDecimal> map = (Map) l.get("rateMap");
				if (map != null && map.get("USDT") != null) {
					BigDecimal usdPrice = (BigDecimal) map.get("USDT");
					ifrUsdPrice = ifrUsdtPrice.multiply(usdPrice);
				}
			}
		}

		if(ifrUsdPrice == null){
			return new ObjectRestResponse().status(ResponseCode.PARAM_ERROR);
		}

		List<IfrPlan> ifrPlan =  planBiz.selectListAll();
		List<IfrExchangeRate> exchangeRateList = exchangeRateBiz.selectListAll();
		Map<String,Object> map = InstanceUtil.newHashMap();
		List<IfrPlanRspVo> list = BizControllerUtil.transferEntityToListVo(IfrPlanRspVo.class,ifrPlan);
		map.put("planList",list);
		map.put("rateList",exchangeRateList);
		map.put("ifrPrice",ifrUsdPrice.multiply(new BigDecimal(1.08)).setScale(2,BigDecimal.ROUND_UP).stripTrailingZeros().toPlainString());
		return new ObjectRestResponse().data(map);

	}


	@PostMapping("recharge")
	public ObjectRestResponse createPayOrder(@RequestBody @Valid IfrPayOrderModel model) {
		IfrPlan plan = planBiz.selectById(model.getPlanId());
        if(plan == null){
			return new ObjectRestResponse().status(ResponseCode.PARAM_ERROR);
		}

		IfrExchangeRate rate = exchangeRateBiz.selectById(model.getCurrency());
		if(rate == null){
			return new ObjectRestResponse().status(ResponseCode.PARAM_ERROR);
		}

		FrontUserInfo userInfoParam = new FrontUserInfo();
		userInfoParam.setVisitCode(model.getReferralId());
		FrontUserInfo userInfo = userInfoBiz.selectOne(userInfoParam);
		if( userInfo == null){
			return new ObjectRestResponse().status(ResponseCode.VISITCODE_LENGTH);
		}

		String url = env.getProperty("udax.quotation");//udax行情接口
		String returnJson = HttpUtils.postJson(url,"");
		UdaxQuotationBean jsonBean = JSON.parseObject(returnJson,UdaxQuotationBean.class);
		List<JSONObject> rateList = (List<JSONObject>) jsonBean.getData().get("rateList");

        //找ifr/udst交易对价格·
		BigDecimal ifrUsdtPrice = null;
		Map<String,JSONObject> quotationBeanMap = (Map<String,JSONObject>) jsonBean.getData().get("quotation");
		for(Map.Entry<String,JSONObject> set : quotationBeanMap.entrySet()){
			String k = set.getKey();
			JSONObject v = set.getValue();
			if(k.contains("IFR/"+Constants.QUOTATION_DCCODE)){
				Object ob = v.getBigDecimal("lastPrice");
				if(ob != null){
					ifrUsdtPrice = (BigDecimal) ob;
				}
			}
		}
		if(ifrUsdtPrice == null){
			return new ObjectRestResponse().status(ResponseCode.PARAM_ERROR);
		}

        BigDecimal ifrUsdPrice = null;
		for(JSONObject l : rateList) {
			if (l.get("currencyName").equals("USDUSD")) {
				Map<String, BigDecimal> map = (Map) l.get("rateMap");
				if (map != null && map.get("USDT") != null) {
					BigDecimal usdPrice = (BigDecimal) map.get("USDT");
					ifrUsdPrice = ifrUsdtPrice.multiply(usdPrice);
				}
			}
		}

		if(ifrUsdPrice == null){
			return new ObjectRestResponse().status(ResponseCode.PARAM_ERROR);
		}

		BigDecimal ifrPrice = rate.getExchangeRate().multiply(ifrUsdPrice).multiply(new BigDecimal(1.08)).setScale(2,BigDecimal.ROUND_UP);


		//生成订单
		IfrPayOrder order = new IfrPayOrder();
		order.setUserId(BaseContextHandler.getUserID());
		order.setOrderNo(String.valueOf(IdGenerator.nextId()));
        order.setCampaignId(model.getCampaignId());
       // order.setCountry(model.getCountry());
        order.setCreateTime(new Date());
        order.setPayStatus(IfrOrderStatus.INIT.value());
        order.setPeriod(plan.getTimePeriod());
        order.setReferralId(model.getReferralId());
        order.setUpdateTime(new Date());
        order.setUnits(model.getAmount().setScale(0,BigDecimal.ROUND_DOWN));//充值金额
        order.setAmount(order.getUnits().multiply(ifrPrice).setScale(0,BigDecimal.ROUND_UP));//换算的法币价值
		order.setCurrency(rate.getSymbol());
      //  order.set
		payOrderBiz.insertSelective(order);

		//调用支付接口,返回订单数据给页面
		Map resultMap = walaoPay(order);
		resultMap.remove("key");
		resultMap.put("units",order.getUnits());
		resultMap.put("realAmount",order.getAmount());
		return new ObjectRestResponse().data(resultMap);
	}

	public LinkedHashMap<String, Object> walaoPay(IfrPayOrder order) {
		LinkedHashMap<String, Object> verifyParams = InstanceUtil.newLinkedHashMap();//有序
		verifyParams.put("service_version", getConstantMap().get("service_version"));
		verifyParams.put("partner_code", getConstantMap().get("partner_code"));
		verifyParams.put("partner_orderid", order.getOrderNo());
		verifyParams.put("member_id", order.getUserId());
		verifyParams.put("member_ip", WebUtil.getHost(request));
		verifyParams.put("currency", order.getCurrency());
		//verifyParams.put("amount", order.getUnits());
		verifyParams.put("amount", order.getAmount().multiply(new BigDecimal(100)).longValue());
		verifyParams.put("backend_url",  getConstantMap().get("backend_url"));
		verifyParams.put("redirect_url", getConstantMap().get("redirect_url"));
		verifyParams.put("trans_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		verifyParams.put("key", getConstantMap().get("merchantKey"));
		verifyParams.put("sign", SignUtil.getSignature(verifyParams));

		return verifyParams;
	}

	//IFR现金充值回调通知
	@RequestMapping(value = "/backendNotify", produces = {"application/xml;charset=UTF-8"})
	public Object backendNotify(HttpServletRequest request) throws Exception {
		Map<String, Object> params = WebUtil.getParameterMap(request);
		logger.info("ifr充值回调参数如下:" + params);

		LinkedHashMap<String, Object> verifyParams = InstanceUtil.newLinkedHashMap();//有序
		verifyParams.put("service_version", params.get("service_version"));
		verifyParams.put("billno", params.get("billno"));
		verifyParams.put("partner_orderid", params.get("partner_orderid"));
		verifyParams.put("currency", params.get("currency"));
		verifyParams.put("amount", params.get("amount"));
		verifyParams.put("status", params.get("status"));
		verifyParams.put("key", getConstantMap().get("merchantKey"));
		Boolean flag = SignUtil.verifySignature(params, verifyParams);

		XMLModel mode = new XMLModel();
		XStream xStream = new XStream();
		xStream.alias("xml", XMLModel.class);//设置类别名
		mode.setBillno(String.valueOf(params.get("billno")));
		if (!flag) {
			mode.setStatus("Failed,Invalid Signature");
			logger.info("ifr充值参数验证不通过！");
			return xStream.toXML(mode);
		}

		if ("000".equals(String.valueOf(params.get("status"))) || "002".equals(String.valueOf(params.get("status")))) {
			payOrderBiz.orderCallback(IfrOrderStatus.SUCCESS.value(),String.valueOf(params.get("partner_orderid")),Integer.valueOf(String.valueOf(params.get("amount"))));
		}else if("111".equals(params.get("status").toString())){
			payOrderBiz.orderCallback(IfrOrderStatus.FAILED.value(),String.valueOf(params.get("partner_orderid")),Integer.valueOf(String.valueOf(params.get("amount"))));
		}
		mode.setStatus("OK");

		return xStream.toXML(mode);
	}

	//获取ifr充值的常量数据
	private Map<String,Object> getConstantMap() {
		Map<String, Object> map = InstanceUtil.newHashMap();
		map.put("service_version",ifrConfiguration.getServiceVersion());
		map.put("partner_code",ifrConfiguration.getPartnerCode());
		map.put("backend_url",ifrConfiguration.getBackendUrl());
		map.put("redirect_url",ifrConfiguration.getRedirectUrl());
		map.put("merchantKey",ifrConfiguration.getMerchantKey());
		return map;
	}


	public static void main(String[] args) {

//		LinkedHashMap<String, Object> verifyParams = InstanceUtil.newLinkedHashMap();//有序
//		verifyParams.put("service_version", "1.0");
//		verifyParams.put("billno", "2019071231000379");
//		verifyParams.put("partner_orderid", "1149645351322390528");
//		verifyParams.put("currency", "MYR");
//		verifyParams.put("amount", "800");
//		verifyParams.put("status",  "111");
//		verifyParams.put("key","SA7SsikDx6dFelHeniKeJgADhTRd8IPI");
//		System.out.println(SignUtil.getSignature(verifyParams));
		XMLModel mode = new XMLModel();
		mode.setBillno("201907181437");
		mode.setStatus("OK");
		XStream xStream = new XStream();
		//output(0, xStream, person);
		/************** 设置类别名 ****************/
		xStream.alias("xml", com.udax.front.bean.XMLModel.class);
		System.out.println(xStream.toXML(mode));
	}
}
