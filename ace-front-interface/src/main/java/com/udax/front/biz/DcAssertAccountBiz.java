package com.udax.front.biz;

import com.alibaba.fastjson.JSON;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.configuration.KeyConfiguration;
import com.github.wxiaoqi.security.common.entity.admin.UserOfferInfo;
import com.github.wxiaoqi.security.common.entity.front.*;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.mapper.admin.UserOfferInfoMapper;
import com.github.wxiaoqi.security.common.mapper.front.DcAssetAccountLogMapper;
import com.github.wxiaoqi.security.common.mapper.front.DcAssetAccountMapper;
import com.github.wxiaoqi.security.common.mapper.front.TransferListMapper;
import com.github.wxiaoqi.security.common.mapper.front.TransferOrderMapper;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.HttpUtils;
import com.github.wxiaoqi.security.common.vo.UdaxLastPricesBean;
import com.udax.front.service.ServiceUtil;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

import static com.github.wxiaoqi.security.common.enums.AccountSignType.ACCOUNT_PAY_AVAILABLE;
import static com.github.wxiaoqi.security.common.enums.AccountSignType.ACCOUNT_RECHARGE_FREEZE;
import static com.github.wxiaoqi.security.common.enums.AccountSignType.MERCHANT_REFUND_SETTLE;


@Service
@Slf4j
public class DcAssertAccountBiz extends BaseBiz<DcAssetAccountMapper, DcAssetAccount> {

	@Autowired
	private KeyConfiguration configuration;

	@Autowired
	private TransferListMapper transferListMapper;

	@Autowired
	private TransferOrderMapper transferOrderMapper;


	@Autowired
	private UserOfferInfoMapper userOfferInfoMapper;

	@Autowired
	private DcAssertAccountBiz dcAssertAccountBiz;

	@Autowired
	private DcAssetAccountLogMapper logMapper;

	@Autowired
	private Environment env;

    //锁记录
	@Transactional(rollbackFor = Exception.class)
    public DcAssetAccount lockRecord(String dcCode,Long userId) throws Exception {
		DcAssetAccount dcAssetAccountParam = new DcAssetAccount();
		dcAssetAccountParam.setUserId(userId);
		dcAssetAccountParam.setSymbol(dcCode);
		//锁表
		DcAssetAccount dcAssetAccount = mapper.selectForUpdate(dcAssetAccountParam);
		if(dcAssetAccount == null ) {
			insertNewRecord(userId, dcCode);
			dcAssetAccount = mapper.selectForUpdate(dcAssetAccountParam);
		}
		return dcAssetAccount;
	}


	/**
	 * 用户之间转账
	 * @param orderNo 转账订单号
	 */
	@Transactional(rollbackFor = Exception.class)
	public  ObjectRestResponse transferAssert(String orderNo){
		ObjectRestResponse response = new ObjectRestResponse();
		TransferOrder order = new TransferOrder();
		order.setOrderNo(orderNo);
		order = transferOrderMapper.selectForUpdate(order);
		//订单失效
		if(order == null || order.getStatus() != TransferOrderStatus.UNPAY.value()){
            response.status(ResponseCode.TRANSFER_DEALED);
			return response;
		}
		//是否大于超时时间
		if(order.getExpireTime().before(new Date())){
			response.status(ResponseCode.TRANSFER_DEALED);
			return response;
		}

        if(order.getUserId().longValue() == order.getReceiveUserId().longValue()){
			response.status(ResponseCode.TRANSFER_FAIL);
			return response;
		}
		try{

			//扣减可用余额
			AccountAssertLogVo payVo = new AccountAssertLogVo();
			payVo.setUserId(order.getUserId());
			payVo.setSymbol(order.getSymbol());
			payVo.setAmount(order.getAmount());
			payVo.setChargeSymbol(order.getSymbol());
			payVo.setChargeAmount(order.getChargeAmount());
			payVo.setType(order.getType() == TransferType.COMMON.value() ? AccountLogType.TRANSFER : AccountLogType.RP_TRANSFER);
			payVo.setTransNo(orderNo);//转账流水号
			payVo.setRemark(order.getType() == TransferType.COMMON.value() ? AccountLogType.TRANSFER.name() : AccountLogType.RP_TRANSFER.name());
			dcAssertAccountBiz.signUpdateAssert(payVo,AccountSignType.ACCOUNT_PAY_AVAILABLE);
			AccountAssertLogVo receive = new AccountAssertLogVo();
			receive.setUserId(order.getReceiveUserId());
			receive.setSymbol(order.getSymbol());
			receive.setAmount(order.getArrivalAmount());
			receive.setChargeSymbol(order.getSymbol());
			receive.setTransNo(orderNo);//转账流水号
			receive.setType(order.getType() == TransferType.COMMON.value() ? AccountLogType.RECEIVE_TRANSFER : AccountLogType.RP_RECEIVE_TRANSFER);
			receive.setRemark(order.getType() == TransferType.COMMON.value() ? AccountLogType.RECEIVE_TRANSFER.name(): AccountLogType.RP_RECEIVE_TRANSFER.name());
			dcAssertAccountBiz.signUpdateAssert(receive,AccountSignType.ACCOUNT_RECHARGE);
//			DcAssetAccountLog log = new DcAssetAccountLog();
//			log.setUserId(order.getUserId());
//			log.setSymbol(order.getSymbol());
//			log.setAmount(order.getAmount());
//			log.setChargeAmount(order.getChargeAmount());
//			log.setCreateTime(new Date());
//			log.setDirection(DirectionType.PAY.value());
//			log.setChargeSymbol(order.getSymbol());
//			log.setPreBalance();
//			log.setAfterBalance();
//			log.setRemark();
//			log.set



//			//查询用户的这条资产记录
//			DcAssetAccount dcAssetAccount= lockRecord(order.getSymbol(),order.getUserId());
//			//判断是否余额不足
//			if(dcAssetAccount.getAvailableAmount().compareTo(order.getAmount()) < 0) {
//				response.status(ResponseCode.BALANCE_NOT_ENOUGH);
//				return response;
//			}
//			//校验mac
//			boolean flag =ServiceUtil.verifySign(dcAssetAccount,configuration.getSignKey(),dcAssetAccount.getUmac());
//			if (!flag) {
//				// 返回校验失败
//				response.status(ResponseCode.ASSERT_30001);
//				return response;
//			}


			//查询对手方记录
//			DcAssetAccount receiveAccount= lockRecord(order.getSymbol(),order.getReceiveUserId());
//
//			//校验mac
//			flag =ServiceUtil.verifySign(receiveAccount,configuration.getSignKey(),receiveAccount.getUmac());
//			if (!flag) {
//				// 返回校验失败
//				response.status(ResponseCode.ASSERT_30001);
//				return response;
//			}
//
//
//
//			//收款方资产变动,并重新生成umac
//			receiveAccount.setTotalAmount(receiveAccount.getTotalAmount().add(order.getAmount()));
//			receiveAccount.setAvailableAmount(receiveAccount.getAvailableAmount().add(order.getAmount()));
//			receiveAccount.setUmac(ServiceUtil.macSign(receiveAccount,configuration.getSignKey()));
//			//付款方资产变动,并重新生成umac
//			dcAssetAccount.setTotalAmount(dcAssetAccount.getTotalAmount().subtract(order.getAmount()));
//			dcAssetAccount.setAvailableAmount(dcAssetAccount.getAvailableAmount().subtract(order.getAmount()));
//			dcAssetAccount.setUmac(ServiceUtil.macSign(dcAssetAccount,configuration.getSignKey()));
			//更新数据
//			mapper.updateByPrimaryKeySelective(dcAssetAccount);
//			mapper.updateByPrimaryKeySelective(receiveAccount);

			//更新订单状态
			TransferOrder transferOrder = new TransferOrder();
			transferOrder.setStatus(TransferOrderStatus.PAYED.value());
			transferOrder.setUpdateTime(new Date());
			transferOrder.setId(order.getId());
			transferOrderMapper.updateByPrimaryKeySelective(transferOrder);


			TransferList transferList = new TransferList();
			transferList.setReceiveUserId(order.getReceiveUserId());
			transferList.setUserId(order.getUserId());
			int count = transferListMapper.selectCount(transferList);
			//如果转账关系不存在,保存关系
			if(count == 0){
				BeanUtils.copyProperties(order,transferList);
				transferList.setId(null);
				transferListMapper.insertSelective(transferList);
			}
			return response.data(order);
		}catch (Exception e){
			log.error(e.getMessage(),e);
			throw new BusinessException(ResponseCode.TRANSFER_FAIL.name());
		}

	}




	//插入一条新记录
	private DcAssetAccount insertNewRecord(Long userId,String dcCode) throws Exception {
		DcAssetAccount	dcAssetAccount = new DcAssetAccount();
		dcAssetAccount.setSymbol(dcCode);
		dcAssetAccount.setUserId(userId);
		dcAssetAccount.setTotalAmount(BigDecimal.ZERO.setScale(8,BigDecimal.ROUND_HALF_UP));
		dcAssetAccount.setAvailableAmount(BigDecimal.ZERO.setScale(8,BigDecimal.ROUND_HALF_UP));
		dcAssetAccount.setFreezeAmount(BigDecimal.ZERO.setScale(8,BigDecimal.ROUND_HALF_UP));
		dcAssetAccount.setWaitConfirmAmount(BigDecimal.ZERO.setScale(8,BigDecimal.ROUND_HALF_UP));
		dcAssetAccount.setCreateTime(new Date());
		dcAssetAccount.setUpdateTime(new Date());
		dcAssetAccount.setUmac(ServiceUtil.macSign(dcAssetAccount,configuration.getSignKey()));
		mapper.insertSelective(dcAssetAccount);
		return dcAssetAccount;
	}


	/**
	 * 更新资产信息
	 *
	 * @param userId
	 * @param symbol
	 * @param amount
	 * @param type
	 *            资产类型：充值(Recharge) or 提现(Withdraw)
	 */
	@Transactional(rollbackFor = Exception.class)
	public void signUpdateAssert(AccountAssertLogVo vo, AccountSignType type)
			throws Exception {
		Long userId = vo.getUserId();
		String symbol= vo.getSymbol();
		BigDecimal amount = vo.getAmount();

		ObjectRestResponse rspResult = new ObjectRestResponse();
			DcAssetAccount account = lockRecord(symbol,userId);
		boolean flag = ServiceUtil.verifySign(account, configuration.getSignKey(), account.getUmac());
		if (!flag) {
			// 返回校验失败
			throw new BusinessException(ResponseCode.ASSERT_30001.name());
		}
		//保留一份副本
		DcAssetAccount preAccount = new DcAssetAccount();
		BeanUtils.copyProperties(account,preAccount);//复制属性

		// 更新字段
		if (type == AccountSignType.ACCOUNT_WITHDRAW) {
			account.setTotalAmount(account.getTotalAmount().subtract(amount)); // 扣除冻结资产
			account.setFreezeAmount(account.getFreezeAmount().subtract(amount));
		} else if (type == AccountSignType.ACCOUNT_WITHDRAW_FREEZE) { // 冻结资产
			account.setAvailableAmount(account.getAvailableAmount().subtract(amount));//
			account.setFreezeAmount(account.getFreezeAmount().add(amount));
		} else if (type == AccountSignType.ACCOUNT_WITHDRAW_DEDUTION) {
			account.setAvailableAmount(account.getAvailableAmount().add(amount));// 解冻资产
			account.setFreezeAmount(account.getFreezeAmount().subtract(amount));
		} else if (type == AccountSignType.ACCOUNT_RECHARGE) {
			account.setAvailableAmount(account.getAvailableAmount().add(amount));// 增加资产
			account.setTotalAmount(account.getTotalAmount().add(amount));
		} else if (type == AccountSignType.MERCHANT_ADD_ASSERT) {
			//商户增加待结算资产
			account.setTotalAmount(account.getTotalAmount().add(amount));
			account.setWaitConfirmAmount(account.getWaitConfirmAmount().add(amount));
		} else if (type == AccountSignType.MERCHANT_SETTLE) {
			//商户结算
			account.setAvailableAmount(account.getAvailableAmount().add(amount));
			account.setWaitConfirmAmount(account.getWaitConfirmAmount().subtract(amount));
		} else if(type == ACCOUNT_PAY_AVAILABLE){
			account.setTotalAmount(account.getTotalAmount().subtract(amount)); // 扣除可用资产
			account.setAvailableAmount(account.getAvailableAmount().subtract(amount));
		} else if( type == MERCHANT_REFUND_SETTLE){
			account.setTotalAmount(account.getTotalAmount().subtract(amount)); // 扣除待结算资产
			account.setWaitConfirmAmount(account.getWaitConfirmAmount().subtract(amount));
		} else if( type == ACCOUNT_RECHARGE_FREEZE){
			account.setFreezeAmount(account.getFreezeAmount().add(amount));// 增加冻结资产
			account.setTotalAmount(account.getTotalAmount().add(amount));//增加总资产
		} else {
			throw new BusinessException(ResponseCode.UNKNOW_ERROR.name());
		}

		if(account.getTotalAmount().compareTo(BigDecimal.ZERO) < 0 ||
				account.getAvailableAmount().compareTo(BigDecimal.ZERO) < 0
				|| account.getFreezeAmount().compareTo(BigDecimal.ZERO) < 0
				|| account.getWaitConfirmAmount().compareTo(BigDecimal.ZERO) < 0){
			throw new BusinessException(ResponseCode.BALANCE_NOT_ENOUGH.name());
		}



		// 重新生成mac
		String sign = ServiceUtil.macSign(account, configuration.getSignKey());
		account.setUmac(sign);
		account.setUpdateTime(new Date());
		int count = mapper.updateByPrimaryKeySelective(account);
		if (count <= 0) {
			rspResult.setRel(false);
		}

		DcAssetAccountLog log = new DcAssetAccountLog();
		BeanUtils.copyProperties(vo,log);
		log.setCreateTime(new Date());
		AccountLogType.setParam(vo.getType(),account,preAccount,log);


		BigDecimal lastPrice = BigDecimal.ZERO;
		String url = env.getProperty("udax.lastprices");
		try{
			String returnJson = HttpUtils.postJson(url,vo.getSymbol()+"-USDT-USDT");
			UdaxLastPricesBean jsonBean = JSON.parseObject(returnJson,UdaxLastPricesBean.class);
			//获取交易对的最新价格
			if(jsonBean != null && jsonBean.getCode().intValue() == 200){
				lastPrice = jsonBean.getData();
			}
		}catch (Exception e){
		    logger.error(e.getMessage(),e);
		}
		log.setUsdtRate(lastPrice);
        log.setUsdtAmount(log.getUsdtRate().multiply(log.getAmount()));

		logMapper.insertSelective(log);

	}




//	@Transactional(rollbackFor = Exception.class)
//    public void insertLog(){
//		DcAssetAccountLog log = new DcAssetAccountLog();
//		log.setChangeAmount();
//		log.setChangeType();
//		log.setCreateTime();
//		log.setSymbol();
//		log.setUpdateTime();
//		log.setUserId();
//		dcAssetAccountLogMapper.insert()
//	}
//



	/**
	 * 针对同一个用户的平台对冲的转币
	 *
	 * @param detail
	 * @param freeze
	 */
	@Transactional(rollbackFor = Exception.class)
	public void tranferCoinPlatForm(FrontTransferDetail detail,Boolean freeze)
			throws Exception {

		Long userId = detail.getUserId();
		String subSymbol = detail.getSrcSymbol();
		BigDecimal subAmount = detail.getSrcAmount();
		String addSymbol = detail.getDstSymbol();
		BigDecimal addAmount = detail.getDstAmount();

//		//锁住源币种
//		DcAssetAccount subAccount = lockRecord(subSymbol,userId);
//		boolean flag = ServiceUtil.verifySign(subAccount, configuration.getSignKey(), subAccount.getUmac());
//		if (!flag) {
//			// 返回校验失败
//			rspResult.status(ResponseCode.ASSERT_30001);
//			return rspResult;
//		}
//		//锁住目标币种
//		DcAssetAccount addAccount = lockRecord(addSymbol,userId);
//		flag = ServiceUtil.verifySign(addAccount, configuration.getSignKey(), addAccount.getUmac());
//		if (!flag) {
//			// 返回校验失败
//			rspResult.status(ResponseCode.ASSERT_30001);
//			return rspResult;
//		}
		AccountSignType signType = null;
       if(!freeze){
//		   //余额不足
//		   if(subAccount.getTotalAmount().compareTo(subAmount) < 0 ||
//				   subAccount.getAvailableAmount().compareTo(subAmount) < 0){
//			   return  new ObjectRestResponse().status(ResponseCode.BALANCE_NOT_ENOUGH);
//		   }
//           //扣減可用的
//		   subAccount.setTotalAmount(subAccount.getTotalAmount().subtract(subAmount)); // 扣除资产
//		   subAccount.setAvailableAmount(subAccount.getAvailableAmount().subtract(subAmount));
//		   subAccount.setUpdateTime(new Date());
		   signType = AccountSignType.ACCOUNT_PAY_AVAILABLE;//扣减可用
	   }else{
		   //余额不足
//		   if(subAccount.getTotalAmount().compareTo(subAmount) < 0 ||
//				   subAccount.getFreezeAmount().compareTo(subAmount) < 0){
//			   return  new ObjectRestResponse().status(ResponseCode.BALANCE_NOT_ENOUGH);
//		   }
//           //扣減凍結的
//		   subAccount.setTotalAmount(subAccount.getTotalAmount().subtract(subAmount)); // 扣除资产
//		   subAccount.setFreezeAmount(subAccount.getFreezeAmount().subtract(subAmount));
//		   subAccount.setUpdateTime(new Date());
		   signType = AccountSignType.ACCOUNT_WITHDRAW;//扣减总量和冻结数量
	   }


		AccountAssertLogVo subVo = new AccountAssertLogVo();
		subVo.setUserId(userId);
		subVo.setSymbol(subSymbol);
		subVo.setAmount(subAmount);
		subVo.setChargeSymbol(detail.getChargeCurrencyCode());
		subVo.setChargeAmount(detail.getChargeAmount());
		subVo.setType(AccountLogType.TRANS_COIN_PAY);//转币支出
		subVo.setTransNo(detail.getOrderNo());//转币流水号
		subVo.setRemark(AccountLogType.TRANS_COIN_PAY.name());
		signUpdateAssert(subVo,signType);

		AccountAssertLogVo addVo = new AccountAssertLogVo();
		addVo.setUserId(userId);
		addVo.setSymbol(addSymbol);
		addVo.setAmount(addAmount);
		addVo.setChargeSymbol(addSymbol);
		addVo.setType(AccountLogType.TRANS_COIN_INCOME);//转币收入
		addVo.setTransNo(detail.getOrderNo());//转币流水号
		addVo.setRemark(AccountLogType.TRANS_COIN_INCOME.name());
		signUpdateAssert(addVo,AccountSignType.ACCOUNT_RECHARGE);//增加资产

//		// 增加资产
//		addAccount.setAvailableAmount(addAccount.getAvailableAmount().add(addAmount));
//		addAccount.setTotalAmount(addAccount.getTotalAmount().add(addAmount));
//		addAccount.setUpdateTime(new Date());
//		// 重新生成mac
//		String sign = ServiceUtil.macSign(subAccount, configuration.getSignKey());
//		subAccount.setUmac(sign);
//		int count = mapper.updateByPrimaryKeySelective(subAccount);
//		if (count <= 0) {
//			rspResult.setRel(false);
//		}
//
//		// 重新生成mac
//		sign = ServiceUtil.macSign(addAccount, configuration.getSignKey());
//		addAccount.setUmac(sign);
//		count = mapper.updateByPrimaryKeySelective(addAccount);
//		if (count <= 0) {
//			rspResult.setRel(false);
//		}

	}


	/**
	 * 针对用户和后台用户报价之间的转币
	 *
	 * @param userId
	 * @param transSymbol  用户转换币种
	 * @param transAmount 转换数量
	 * @param userSymbol 后台报价接收的币种
	 * @param receiveAmount 用户能得到目标币种的数量
	 *
	 *   假设用户用ATT转btc  transSymbol就是ATT  userSymbol就是btc
	 */
	@Transactional(rollbackFor = Exception.class)
	public void tranferCoinForUser(FrontTransferDetail detail)
			throws Exception {

		String transSymbol = detail.getSrcSymbol();//用户转换币种
		BigDecimal transAmount = detail.getSrcAmount();
		String userSymbol = detail.getDstSymbol();
		BigDecimal receiveAmount = detail.getDstAmount();//后台报价接收的币种数量
		String chargeSymbol = detail.getChargeCurrencyCode();//手续费币种
		//锁表
		UserOfferInfo userParam = new UserOfferInfo();
		userParam.setSrcSymbol(userSymbol);
		userParam.setDstSymbol(transSymbol);
		UserOfferInfo userOfferInfo = userOfferInfoMapper.selectOneForUpdate(userParam);
		//用户报价的余额不足
		if (userOfferInfo == null || userOfferInfo.getFrontUser() == null ||
				userOfferInfo.getFrontUser().getId() == null ) {
			throw new BusinessException(ResponseCode.TRANSFER_60003.name());
		}


		//如果手续费收的是用户转换的币种,报价用户收到的量要扣减手续费的量
		if(chargeSymbol.equals(transSymbol)){
			transAmount = transAmount.subtract(detail.getChargeAmount());
		}
		//如果手续费收的是目标币种,报价用户收到实际到账数量等于用户转换币转出目标币的全额数量
		if(chargeSymbol.equals(userSymbol)){
			receiveAmount = receiveAmount.add(detail.getChargeAmount());
		}
        //是否余额不足
		if(userOfferInfo.getRemainVolume().compareTo(receiveAmount) < 0){
			throw new BusinessException(ResponseCode.TRANSFER_60003.name());
		}



		tranferCoinPlatForm(detail,false);


		//用户减少源币  增加目标币
		FrontTransferDetail userOffDetail = new FrontTransferDetail();
		userOffDetail.setUserId(userOfferInfo.getFrontUser().getId());
		userOffDetail.setSrcSymbol(userSymbol);
		userOffDetail.setSrcAmount(receiveAmount);
		userOffDetail.setDstSymbol(transSymbol);
		userOffDetail.setDstAmount(transAmount);
		userOffDetail.setChargeCurrencyCode(detail.getChargeCurrencyCode());
		userOffDetail.setChargeAmount(BigDecimal.ZERO);
		userOffDetail.setOrderNo(detail.getOrderNo());
		//后台报价用户增加源币  减少目标币
	    tranferCoinPlatForm(userOffDetail,true);
		//更新剩余数量
		UserOfferInfo updateOfferInfo = new UserOfferInfo();
		updateOfferInfo.setId(userOfferInfo.getId());
		updateOfferInfo.setRemainVolume(userOfferInfo.getRemainVolume().subtract(receiveAmount));
		userOfferInfoMapper.updateByPrimaryKeySelective(updateOfferInfo);

	}


}
