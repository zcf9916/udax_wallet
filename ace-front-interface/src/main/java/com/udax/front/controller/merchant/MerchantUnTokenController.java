package com.udax.front.controller.merchant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.BasicSymbol;
import com.github.wxiaoqi.security.common.entity.admin.CfgChargeTemplate;
import com.github.wxiaoqi.security.common.entity.admin.CfgDcRechargeWithdraw;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.front.FrontWithdraw;
import com.github.wxiaoqi.security.common.entity.merchant.MchPayToken;
import com.github.wxiaoqi.security.common.entity.merchant.MchRefundDetail;
import com.github.wxiaoqi.security.common.entity.merchant.MchTradeDetail;
import com.github.wxiaoqi.security.common.entity.merchant.Merchant;
import com.github.wxiaoqi.security.common.enums.ChargeTemplateType;
import com.github.wxiaoqi.security.common.enums.FrontWithdrawType;
import com.github.wxiaoqi.security.common.enums.MchResponseCode;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.enums.merchant.MchOrderStatus;
import com.github.wxiaoqi.security.common.enums.merchant.MchRefundAccountType;
import com.github.wxiaoqi.security.common.enums.merchant.MchSignType;
import com.github.wxiaoqi.security.common.enums.merchant.MchTradeType;
import com.github.wxiaoqi.security.common.exception.MchException;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.Query;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.WebUtil;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.udax.front.biz.CacheBiz;
import com.udax.front.biz.CfgChargeTemplateBiz;
import com.udax.front.biz.FrontUserBiz;
import com.udax.front.biz.FrontWithdrawBiz;
import com.udax.front.biz.common.CommonControllerBiz;
import com.udax.front.biz.merchant.MchRefundDetailBiz;
import com.udax.front.biz.merchant.MchTradeDetailBiz;
import com.udax.front.biz.merchant.MerchantBiz;
import com.udax.front.controller.BaseFrontController;
import com.udax.front.service.ServiceUtil;
import com.udax.front.util.BizControllerUtil;
import com.udax.front.util.CacheBizUtil;
import com.udax.front.util.SignUtil;
import com.udax.front.vo.reqvo.merchant.MchGetAddModel;
import com.udax.front.vo.reqvo.merchant.MchPayTokenModel;
import com.udax.front.vo.reqvo.merchant.MchPreOrderModel;
import com.udax.front.vo.reqvo.merchant.MchQueryOrderModel;
import com.udax.front.vo.reqvo.merchant.MchQueryWithdrawModel;
import com.udax.front.vo.reqvo.merchant.MchRefundModel;
import com.udax.front.vo.reqvo.merchant.MchRefundQueryModel;
import com.udax.front.vo.reqvo.merchant.MchSymbolInfoModel;
import com.udax.front.vo.reqvo.merchant.MchWithdrawModel;
import com.udax.front.vo.rspvo.merchant.GetRechargeAddRspVo;
import com.udax.front.vo.rspvo.merchant.MchSymbolInfoRspModel;
import com.udax.front.vo.rspvo.merchant.OrderQueryRspVo;
import com.udax.front.vo.rspvo.merchant.PreOrderRspVo;
import com.udax.front.vo.rspvo.merchant.QueryWithdrawRspVo;
import com.udax.front.vo.rspvo.merchant.RefundRspVo;
import com.udax.front.vo.rspvo.merchant.WithdrawRspVo;

/**
 * @author zhoucf
 * @create 2018／3/13 商户相关接口
 */
@RestController
@RequestMapping("/merchant/")
public class MerchantUnTokenController extends BaseFrontController<MerchantBiz, Merchant> {

	@Autowired
	private FrontUserBiz frontUserBiz;

	//
	@Autowired
	private FrontWithdrawBiz frontWithdrawBiz;
	//
	@Autowired
	private CacheBiz cacheBiz;

	@Autowired
	private MchTradeDetailBiz mchTradeDetailBiz;

	@Autowired
	private CfgChargeTemplateBiz cfgChargeTemplateBiz;

	@Autowired
	private CommonControllerBiz commonBiz;

	@Autowired
	private MchRefundDetailBiz mchRefundDetailBiz;

	// 商户查询可交易代币相关信息
	@PostMapping("/symbolInfo")
	public Object orderquery(@RequestBody @Valid MchSymbolInfoModel model) throws Exception {
		// 通过登录名查询出用户
		Merchant merchant = validMerchant(model, baseBiz, true);
		FrontUser frontUser = ServiceUtil.selectUnionUserInfoById(merchant.getUserId(), frontUserBiz);
		Map<String, CfgDcRechargeWithdraw> symbolConfig = CacheBizUtil.getSymbolConfig(cacheBiz);
		ObjectRestResponse result = new ObjectRestResponse().rel(true);
		List<BasicSymbol> basicList = CacheBizUtil.getBasicSymbolExch(cacheBiz,
				frontUser.getUserInfo().getExchangeId());
		// 用户资产数据转vo
		List<MchSymbolInfoRspModel> list = BizControllerUtil.transferEntityToListVo(MchSymbolInfoRspModel.class,
				basicList);
		list.stream().forEach((l) -> {
			CfgDcRechargeWithdraw config = null;
			// 获取交易所配置信息
			config = symbolConfig.get(l.getSymbol() + ":" + BaseContextHandler.getAppExId());
			if (config == null) {
				// 获取默认的配置信息
				config = symbolConfig.get(l.getSymbol() + ":" + AdminCommonConstant.ROOT);
			}
			// 设置是否可以充提币数据
			if (config != null) {
				l.setCanRecharge(config.getRechargeStatus());
				l.setCanWithdraw(config.getWithdrawStatus());
				BeanUtils.copyProperties(config, l);
			}
		});

		Map<String, Object> map = InstanceUtil.newHashMap();
		map.put("list", list);
		map.put("nonceStr", model.getNonceStr());
		map.put("mchNo", model.getMchNo());
		result.setData(map);
		return result;
	}

	// 商户查询用户订单
	@PostMapping("/orderquery")
	public Object orderquery(@RequestBody @Valid MchQueryOrderModel model) throws Exception {
		// 通过登录名查询出用户
		Merchant merchant = validMerchant(model, baseBiz, true);
		MchTradeDetail mchTradeDetail = null;

		MchTradeDetail param = new MchTradeDetail();
		if (StringUtils.isNotBlank(model.getTransNo())) {
			param.setWalletOrderNo(model.getTransNo());
			param.setMchNo(Long.valueOf(model.getMchNo()));
		} else if (StringUtils.isNotBlank(model.getMchOrderNo())) {
			param.setMchOrderNo(model.getMchOrderNo());
			param.setMchNo(Long.valueOf(model.getMchNo()));
		} else {
			return new ObjectRestResponse<>().status(ResponseCode.ORDER_NOT_EXIST);
		}
		mchTradeDetail = mchTradeDetailBiz.selectOne(param);
		if (mchTradeDetail == null) {
			return new ObjectRestResponse<>().status(ResponseCode.ORDER_NOT_EXIST);
		}
		// 返回vo
		OrderQueryRspVo rspVo = new OrderQueryRspVo();
		BeanUtils.copyProperties(mchTradeDetail, rspVo);
		rspVo.setMchNo(model.getMchNo());
		rspVo.setTransNo(Long.valueOf(mchTradeDetail.getWalletOrderNo()));
		String sign = SignUtil.sign(rspVo, merchant.getSecretKey());
		rspVo.setSign(sign);
		return new ObjectRestResponse<>().data(rspVo);
	}

	// 预下单接口
	@PostMapping("/preorder")
	public ObjectRestResponse preorder(@RequestBody @Valid MchPreOrderModel model) throws Exception {

		if (!MchTradeType.isType(model.getTradeType())) {
			throw new MchException(MchResponseCode.MERCHANT_TRADETYPE_ERROR.name());
		}
		if (!MchSignType.isType(model.getSignType())) {
			throw new MchException(MchResponseCode.MERCHANT_SIGNTYPE_ERROR.name());
		}
		Merchant merchant = validMerchant(model, baseBiz, false);

		CfgChargeTemplate cfgChargeTemplate = cfgChargeTemplateBiz.selectById(merchant.getChargeId());
		BigDecimal chargeValue = ChargeTemplateType.getChargeValue(cfgChargeTemplate.getChargeType(),
				cfgChargeTemplate.getChargeValue(), model.getTokenList().get(0).getAmount());
		// 判断手续费是否大于交易数量
		if (chargeValue.compareTo(model.getTokenList().get(0).getAmount()) >= 0) {
			return new ObjectRestResponse<>().status(ResponseCode.WITHDRAW_50010);
		}

		String actualIp = WebUtil.getHost(request);
		// 计算订单过期时间
		Date expireTime = CacheBizUtil.getExpireTime(Constants.BaseParam.MCH_ORDER_EXPIRE, cacheBiz);
		MchTradeDetail mchTradeDetail = new MchTradeDetail();
		mchTradeDetail.setBody(model.getBody());// 商品描述
		mchTradeDetail.setIp(actualIp);// 用戶傳輸過來的ip
		mchTradeDetail.setActualIp(actualIp);// http請求拿到的ip
//		if (StringUtils.isNotEmpty(model.getSymbol())) { //单币种支付时逻辑处理
//			mchTradeDetail.setSymbol(model.getSymbol());// 订单代币
//			mchTradeDetail.setAmount(model.getTotalAmount());// 订单数量
//		}else {
		List<MchPayTokenModel> tokenList = model.getTokenList();
		List<MchPayToken> beanList = new ArrayList<MchPayToken>();
		for (MchPayTokenModel mchPayTokenModel : tokenList) {
			MchPayToken token=new MchPayToken();
			token.setSymbol(mchPayTokenModel.getSymbol());
			token.setAmount(mchPayTokenModel.getAmount());
			beanList.add(token);
		}
		mchTradeDetail.setTokenList(beanList);			
//		}
		mchTradeDetail.setChargeSymbol(model.getTokenList().get(0).getSymbol());// 手续费代币
		mchTradeDetail.setMchUserId(merchant.getUserId());// 商户对应用户id
		mchTradeDetail.setChargeAmount(chargeValue);// 手续费数量
		mchTradeDetail.setMchId(merchant.getId());// 商户Id
		mchTradeDetail.setMchOrderNo(model.getMchOrderNo());// 商户订单号
		mchTradeDetail.setOrderTime(new Date());// 订单生成时间
		mchTradeDetail.setStatus(MchOrderStatus.UNPAY.value());// 订单状态
		mchTradeDetail.setMchNo(merchant.getMchNo());// 商户号
		mchTradeDetail.setWalletOrderNo(String.valueOf(IdGenerator.nextId()));// 钱包订单号
		mchTradeDetail.setTradeType(model.getTradeType());// 交易類型 參考MchTradeType
		mchTradeDetail.setNotifyUrl(model.getNotifyUrl());// 回調地址
		mchTradeDetail.setExpireTime(expireTime);// 過期時間
		mchTradeDetail.setNonceStr(model.getNonceStr());// 随机字符串
		mchTradeDetail.setSettleTime(new Date());
		Long prepayId = mchTradeDetailBiz.preOrder(mchTradeDetail);
		// 接口返回的信息
		PreOrderRspVo rspVo = new PreOrderRspVo();
		rspVo.setMchOrderNo(model.getMchOrderNo());
		// 返回的预支付交易会话标识加密
		rspVo.setPrepayId(prepayId.toString());// 预支付交易会话ID
		rspVo.setTradeType(model.getTradeType());
		rspVo.setMchNo(model.getMchNo());
		rspVo.setAmount(mchTradeDetail.getAmount());
		rspVo.setChargeAmount(mchTradeDetail.getChargeAmount());
		rspVo.setNonceStr(model.getNonceStr());
		rspVo.setSymbol(mchTradeDetail.getSymbol());
		// rspVo.setBody(mchTradeDetail.getBody());
		String rspSign = SignUtil.sign(rspVo, merchant.getSecretKey());
		rspVo.setSign(rspSign);
		return new ObjectRestResponse().data(rspVo);
	}

	// 获取商户充值地址
	@PostMapping("/getRechargetAdd")
	public Object getRechargetAdd(@RequestBody @Valid MchGetAddModel model) throws Exception {
		// 验证商户/
		Merchant merchant = validMerchant(model, baseBiz, true);
		String userAddress = commonBiz.getRechargetAdd(merchant.getUserId(), model.getSymbol(), model.getUserId());
		// 返回结果
		GetRechargeAddRspVo rspVo = new GetRechargeAddRspVo();
		rspVo.setAddress(userAddress);
		rspVo.setSymbol(model.getSymbol());
		rspVo.setMchNo(merchant.getMchNo().toString());
		rspVo.setNonceStr(model.getNonceStr());
		String rspSign = SignUtil.sign(rspVo, merchant.getSecretKey());
		rspVo.setSign(rspSign);
		return new ObjectRestResponse<>().data(rspVo);
	}

	// 商户提现
	@PostMapping("/withdraw")
	public Object withdraw(@RequestBody @Valid MchWithdrawModel model) throws Exception {
		Merchant merchant = validMerchant(model, baseBiz, true);
		FrontUser frontUser = ServiceUtil.selectUnionUserInfoById(merchant.getUserId(), frontUserBiz);
		ObjectRestResponse response = commonBiz.withdraw(model, frontUser, FrontWithdrawType.MERCHANT, true);
		if (!response.isRel()) {
			return response;
		}
		FrontWithdraw withdraw = (FrontWithdraw) response.getData();
		// 返回的Vo
		WithdrawRspVo rspVo = new WithdrawRspVo();
		rspVo.setAddress(model.getAddress());
		rspVo.setSymbol(model.getSymbol());
		rspVo.setNonceStr(model.getNonceStr());
		rspVo.setMchNo(merchant.getMchNo().toString());
		rspVo.setMchOrderNo(model.getMchOrderNo());
		rspVo.setAmount(withdraw.getTradeAmount());
		rspVo.setChargeAmount(withdraw.getChargeAmount());
		rspVo.setTransNo(withdraw.getTransNo());
		String rspSign = SignUtil.sign(rspVo, merchant.getSecretKey());
		rspVo.setSign(rspSign);
		response.setData(rspVo);
		return response;
	}

	// 商户提现结果查询
	@PostMapping("/queryWithdraw")
	public Object withdrawResult(@RequestBody @Valid MchQueryWithdrawModel model) throws Exception {
		Merchant merchant = validMerchant(model, baseBiz, true);

		FrontWithdraw param = new FrontWithdraw();
		param.setUserId(merchant.getUserId());

		if (StringUtils.isNotBlank(model.getTransNo())) {
			boolean ifMatch = Pattern.matches("^[0-9]{1,32}$", model.getTransNo());
			if (!ifMatch) {
				throw new MchException(MchResponseCode.MERCHANT_TRANS_NO.name());
			}
			param.setTransNo(model.getTransNo());
		} else if (StringUtils.isNotBlank(model.getMchOrderNo())) {
			boolean ifMatch = Pattern.matches("^[a-zA-Z0-9]{1,32}$", model.getMchOrderNo());
			if (!ifMatch) {
				throw new MchException(MchResponseCode.MERCHANT_ORDER_NO.name());
			}
			param.setMchOrderNo(model.getMchOrderNo());

		} else {
			throw new MchException(MchResponseCode.MERCHANT_TRANS_NO.name());
		}
		FrontWithdraw frontWithdraw = frontWithdrawBiz.selectOne(param);
		if (frontWithdraw == null) {
			throw new MchException(MchResponseCode.MERCHANT_TRANS_NO.name());
		}

		QueryWithdrawRspVo rspVo = new QueryWithdrawRspVo();
		// 返回的Vo
		rspVo.setMchNo(merchant.getMchNo().toString());
		rspVo.setTranNo(frontWithdraw.getTransNo());
		rspVo.setMchOrderNO(frontWithdraw.getMchOrderNo());
		rspVo.setStatus(frontWithdraw.getStatus());
		rspVo.setNonceStr(model.getNonceStr());
		rspVo.setChargeAmount(frontWithdraw.getChargeAmount());
		rspVo.setAddress(frontWithdraw.getUserAddress());
		String rspSign = SignUtil.sign(rspVo, merchant.getSecretKey());
		rspVo.setSign(rspSign);
		return new ObjectRestResponse<>().data(rspVo);
	}

	// 商户退款
	@PostMapping("/refund")
	public Object refund(@RequestBody @Valid MchRefundModel model) throws Exception {
		if (!MchRefundAccountType.isType(model.getRefundAccountType())) {
			throw new MchException(MchResponseCode.MERCHANT_REFUND_ACCOUNT_TYPE.name());
		}

		Merchant merchant = validMerchant(model, baseBiz, true);
		String ip = WebUtil.getHost(request);

		MchRefundDetail mchRefundDetail = mchRefundDetailBiz.refund(model, merchant.getId(), ip);

		RefundRspVo rspVo = new RefundRspVo();
		rspVo.setMchOrderNO(mchRefundDetail.getOriMchOrderNo());// 原商户订单号
		rspVo.setMchRefundOrderNo(mchRefundDetail.getMchOrderNo());// 商户退款订单号
		rspVo.setRefundTransNo(mchRefundDetail.getWalletOrderNo());// 退款流水
		rspVo.setTotalAmount(mchRefundDetail.getTotalAmount());// 原订单总金额
		rspVo.setTransNo(mchRefundDetail.getOriWalletOrderNo());// 原始流水号
		rspVo.setAmount(mchRefundDetail.getAmount());// 退款金额
		rspVo.setMchNo(mchRefundDetail.getMchNo().toString());// 商户号
		rspVo.setNonceStr(model.getNonceStr());// 随机字符串
		rspVo.setSymbol(mchRefundDetail.getSymbol());// 代币
		String rspSign = SignUtil.sign(rspVo, merchant.getSecretKey());
		rspVo.setSign(rspSign);
		return new ObjectRestResponse<>().data(rspVo);
	}

	// 商户退款查询
	@PostMapping("/refundquery")
	public Object refundquery(@RequestBody @Valid MchRefundQueryModel model) throws Exception {
		Merchant merchant = validMerchant(model, baseBiz, true);
		Map<String, Object> param = InstanceUtil.newHashMap();
		// 优先级 钱包退款流水号 > 商户退款订单号 > 商户原订单号
		// 钱包退款流水号
		if (StringUtils.isNotBlank(model.getRefundTransNo())) {
			param.put("walletOrderNo", model.getRefundTransNo());
			param.put("mchNo", model.getMchNo());
		} else if (StringUtils.isNotBlank(model.getMchRefundOrderNo())) {
			// 商户退款订单号
			param.put("mchOrderNo", model.getMchRefundOrderNo());
			// 商户ID
			param.put("mchId", merchant.getId());
		} else if (StringUtils.isNotBlank(model.getTransNo())) {
			// 原钱包订单号
			param.put("oriWalletOrderNo", model.getTransNo());
			param.put("mchNo", model.getMchNo());
		} else if (StringUtils.isNotBlank(model.getMchOrderNo())) {
			// 原商户订单号
			param.put("oriMchOrderNo", model.getMchOrderNo());
			param.put("mchNo", model.getMchNo());
		}
		param.put("limit", model.getLimit());
		param.put("page", model.getPage());
		Query query = new Query(param);
		// List<MchRefundDetail> mchRefundDetail = mchRefundDetailBiz.selectList(param);

		TableResultResponse resultResponse = pageQuery(query, MchRefundDetail.class, mchRefundDetailBiz);
		if (!resultResponse.isRel()) {
			return response;
		}
		// 分页返回的数据不为空
		if (resultResponse != null && resultResponse.getData() != null
				&& StringUtil.listIsNotBlank(resultResponse.getData().getRows())) {
			List<MchRefundDetail> list = resultResponse.getData().getRows();
			List<RefundRspVo> rspList = new ArrayList<>(list.size());
			for (MchRefundDetail mchRefundDetail : list) {
				RefundRspVo rspVo = new RefundRspVo();
				rspVo.setMchOrderNO(mchRefundDetail.getOriMchOrderNo());// 原商户订单号
				rspVo.setMchRefundOrderNo(mchRefundDetail.getMchOrderNo());// 商户退款订单号
				// rspVo.setRefundAmount(model.getRefundAmount());//退款金额
				rspVo.setRefundTransNo(mchRefundDetail.getWalletOrderNo());// 退款流水号
				rspVo.setTotalAmount(mchRefundDetail.getTotalAmount());// 原订单总金额
				rspVo.setTransNo(mchRefundDetail.getOriWalletOrderNo());// 原始流水号
				rspVo.setAmount(mchRefundDetail.getAmount());// 退款金额
				rspVo.setMchNo(mchRefundDetail.getMchNo().toString());// 商户号
				rspVo.setNonceStr(model.getNonceStr());// 随机字符串
				rspVo.setSymbol(mchRefundDetail.getSymbol());// 代币
				String rspSign = SignUtil.sign(rspVo, merchant.getSecretKey());
				rspVo.setSign(rspSign);
				rspList.add(rspVo);
			}
			resultResponse.getData().setRows(rspList);
		}
		return resultResponse;
	}

}
