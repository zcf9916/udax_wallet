package com.udax.front.biz;

import com.alibaba.fastjson.JSON;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.configuration.KeyConfiguration;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.BasicSymbol;
import com.github.wxiaoqi.security.common.entity.admin.SymbolExch;
import com.github.wxiaoqi.security.common.entity.front.FrontRecharge;
import com.github.wxiaoqi.security.common.entity.front.FrontTokenAddress;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.front.FrontWithdraw;
import com.github.wxiaoqi.security.common.entity.lock.UserSymbolLock;
import com.github.wxiaoqi.security.common.entity.lock.UserSymbolLockDetail;
import com.github.wxiaoqi.security.common.entity.merchant.MchNotify;
import com.github.wxiaoqi.security.common.entity.merchant.Merchant;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.enums.merchant.MchNotifyStatus;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.exception.MchException;
import com.github.wxiaoqi.security.common.mapper.admin.BasicSymbolMapper;
import com.github.wxiaoqi.security.common.mapper.admin.SymbolExchMapper;
import com.github.wxiaoqi.security.common.mapper.front.FrontRechargeMapper;
import com.github.wxiaoqi.security.common.mapper.front.FrontTokenAddressMapper;
import com.github.wxiaoqi.security.common.mapper.front.FrontUserMapper;
import com.github.wxiaoqi.security.common.mapper.front.FrontWithdrawMapper;
import com.github.wxiaoqi.security.common.mapper.lock.UserSymbolLockDetailMapper;
import com.github.wxiaoqi.security.common.mapper.lock.UserSymbolLockMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.SecurityUtil;
import com.github.wxiaoqi.security.common.util.SendUtil;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.udax.front.biz.merchant.MchNotifyBiz;
import com.udax.front.biz.merchant.MerchantBiz;
import com.udax.front.service.ServiceUtil;
import com.udax.front.util.CacheBizUtil;
import com.udax.front.util.SignUtil;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import com.udax.front.vo.reqvo.merchant.MchPayTokenModel;
import com.udax.front.vo.rspvo.merchant.RechargeNotifyVo;
import com.udax.front.vo.rspvo.merchant.WithdrawNotifyVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class BlockChainBiz extends BaseBiz<FrontTokenAddressMapper, FrontTokenAddress> {

	@Autowired
	private FrontRechargeMapper frontRechargeMapper;

	@Autowired
	private FrontWithdrawMapper frontWithdrawMapper;

	@Autowired
	private DcAssertAccountBiz dcAssertAccountBiz;

	@Autowired
	private FrontTokenAddressBiz addressBiz;

	@Autowired
	private MchNotifyBiz mchNotifyBiz;

	@Autowired
	private MerchantBiz merchantBiz;

	@Autowired
	private FrontUserMapper userMapper;

	@Autowired
	private KeyConfiguration config;

	@Autowired
	private SymbolExchMapper symbolExchMapper;

	@Autowired
	private UserSymbolLockMapper userSymbolLockMapper;

    @Autowired
    private UserSymbolLockDetailBiz userSymbolLockDetailBiz;

	@Autowired
	private CacheBiz cacheBiz;


	@Autowired
	private BasicSymbolMapper basicSymbolMapper;


	@Autowired
	private FrontUserBiz frontUserBiz;

//	//分配地址
//	public FrontTokenAddress   allotAddress(String symbol,String userSymbol, Long userId){
//		FrontTokenAddress tokenUser = new FrontTokenAddress();
//		// 分配钱包地址
//		tokenUser.setSymbol(symbol);
//		tokenUser.setEnable(EnableType.DISABLE.value());
//		Integer count = mapper.selectCount(tokenUser);
//		if (count < 25) {
//			String url = env.getProperty("blockchain.pushaddress.url");
//			HttpUtils.asynget(url);
//		}
//        //绑定一个新的地址
//		Example example = new Example(FrontTokenAddress.class);
//		example.createCriteria().andEqualTo("userId", null)
//		.andEqualTo("symbol",symbol)
//		.andEqualTo("enable",EnableType.DISABLE.value());
//
//		FrontTokenAddress updateParam = new FrontTokenAddress();
//		updateParam.setUserId(userId);
//		updateParam.setEnable(EnableType.ENABLE.value());
//		if(StringUtils.isNotBlank(userSymbol)){
//			updateParam.setMerchantUser(userSymbol);
//		}
//        mapper.updateByExampleSelective(updateParam,example);
//
//		//重新查询数据
//		tokenUser.setUserId(userId);
//		if(StringUtils.isNotBlank(userSymbol)){
//			tokenUser.setMerchantUser(userSymbol);
//		}
//		tokenUser.setEnable(EnableType.ENABLE.value());
//		FrontTokenAddress tokenAddress = mapper.selectOne(tokenUser);
//		return tokenAddress;
//	}
	public int updateByExampleSelective(FrontTokenAddress tokenAddress, Example example){
		return  mapper.updateByExampleSelective(tokenAddress,example);
	}

	//是否需要锁仓
	private boolean ifNeedFreeze(FrontRecharge frontRecharge){
		if(StringUtils.isBlank(frontRecharge.getProxyCode())) {
			return false;
		}
		Long exchId = CacheBizUtil.getExchId(frontRecharge.getProxyCode(),cacheBiz);
		if(exchId == null ){
			return  false;
		}

		BasicSymbol symbol = new BasicSymbol();
		symbol.setProtocolType(frontRecharge.getProtocolType());
		symbol.setSymbol(frontRecharge.getSymbol());
		BasicSymbol dbSymbol = basicSymbolMapper.selectOne(symbol);
		if (dbSymbol ==null){
			logger.error("用户充值到账,根据链类型 币种获取币种信息失败!币种:"+frontRecharge.getSymbol()+"链类型:"+frontRecharge.getProtocolType());
			return false;
		}


		SymbolExch symbolExchParam = new SymbolExch();
		symbolExchParam.setExchId(exchId);
		symbolExchParam.setSymbolId(dbSymbol.getId());
		SymbolExch symbolExch = symbolExchMapper.selectOne(symbolExchParam);
		if(symbolExch == null){
			return  false;
		}
		//如果币种不需要锁仓
		if(symbolExch.getHasLock().intValue() != EnableType.ENABLE.value()){
			return false;
		}
		UserSymbolLock userSymbolLockParam = new UserSymbolLock();
		userSymbolLockParam.setUserId(frontRecharge.getUserId());
		userSymbolLockParam.setSymbol(frontRecharge.getSymbol());
		UserSymbolLock userSymbolLock = userSymbolLockMapper.selectOne(userSymbolLockParam);
		//是否已经过了锁定期5
		if(userSymbolLock != null && userSymbolLock.getIsFreed() == EnableType.ENABLE.value()){
			return false;
		}
		//如果需要释放,生成一条新数据
		if(userSymbolLock == null){
            userSymbolLockParam.setTotalTime(symbolExch.getFreedNumber());
            userSymbolLockParam.setFreedCycle(symbolExch.getFreedCycle());
            userSymbolLockParam.setTotalAmount(frontRecharge.getRechargeAmount());
            userSymbolLockParam.setCreateTime(new Date());
            userSymbolLockMapper.insertSelective(userSymbolLockParam);
        }else{
            userSymbolLockParam = new UserSymbolLock();
            //更新锁仓总数量
            userSymbolLockParam.setTotalAmount(userSymbolLock.getTotalAmount().add(frontRecharge.getRechargeAmount()));
            userSymbolLockParam.setVersion(userSymbolLock.getVersion() + 1);//乐观锁
            Example example  = new Example(UserSymbolLockDetail.class);
            example.createCriteria().andEqualTo("version",userSymbolLock.getVersion())
                    .andEqualTo("id",userSymbolLock.getId());
            int result = userSymbolLockMapper.updateByExampleSelective(userSymbolLockParam,example);
            //乐观锁冲突,重新执行
            if(result < 1){
                throw new BusinessException("锁仓时,乐观锁冲突");
            }
            //重新更新锁仓还没释放的明细
            userSymbolLockDetailBiz.updateDetail(userSymbolLock.getSymbol(),userSymbolLock.getUserId(),userSymbolLockParam.getTotalAmount());
        }
        return true;
	}

	/**
	 * 充值并更新资产信息
	 * 
	 * @param frontRecharge
	 */
	@Transactional(rollbackFor = Exception.class)
	public MchNotify addRecharge(FrontRecharge frontRecharge) throws Exception {
			boolean ifFreeze = ifNeedFreeze(frontRecharge);
			if(ifFreeze){
				//资产锁仓
				AccountAssertLogVo vo = new AccountAssertLogVo();
				vo.setUserId(frontRecharge.getUserId());
				vo.setSymbol(frontRecharge.getSymbol());
				vo.setAmount(frontRecharge.getRechargeAmount());
				vo.setChargeSymbol(frontRecharge.getSymbol());
				vo.setType(AccountLogType.LOCK_ASSERT);
				vo.setTransNo(frontRecharge.getOrderId().toString());//充值流水号
				vo.setRemark(AccountLogType.LOCK_ASSERT.name());
				dcAssertAccountBiz.signUpdateAssert(vo, AccountSignType.ACCOUNT_RECHARGE_FREEZE);

			}else{
				AccountAssertLogVo vo = new AccountAssertLogVo();
				vo.setUserId(frontRecharge.getUserId());
				vo.setSymbol(frontRecharge.getSymbol());
				vo.setAmount(frontRecharge.getRechargeAmount());
				vo.setChargeSymbol(frontRecharge.getSymbol());
				vo.setType(AccountLogType.RECHARGE);
				vo.setTransNo(frontRecharge.getOrderId().toString());//充值流水号
				vo.setRemark(AccountLogType.RECHARGE.name());

				dcAssertAccountBiz.signUpdateAssert(vo, AccountSignType.ACCOUNT_RECHARGE);
			}
			frontRechargeMapper.insert(frontRecharge);
			FrontTokenAddress param = new FrontTokenAddress();
			param.setUserAddress(SecurityUtil.encryptDes(frontRecharge.getUserAddress(),config.getWalletKey().getBytes()));
			param.setEnable(EnableType.ENABLE.value());//使用中的

			FrontTokenAddress tokenAddress = addressBiz.selectOne(param);
			MchNotify mchNotify = null;
			//判斷是否商户类型的地址
			if (tokenAddress != null && tokenAddress.getType().equals(FrontRechargeType.MERCHANT.value())) { // 推送给商户到账成功通知
				Merchant merchant = merchantBiz.getMerchantInfoByUserId(frontRecharge.getUserId());
				if(StringUtils.isNotBlank(merchant.getRechargeCallback())){
					RechargeNotifyVo vo = new RechargeNotifyVo();
//					vo.setAmount(frontRecharge.getRechargeAmount().stripTrailingZeros().toPlainString());
//					vo.setSymbol(frontRecharge.getSymbol());
					List<MchPayTokenModel> tokenList=new ArrayList<MchPayTokenModel>();
					MchPayTokenModel model=new MchPayTokenModel();
					model.setAmount(frontRecharge.getRechargeAmount());
					model.setSymbol(frontRecharge.getSymbol());
					tokenList.add(model);
					vo.setTokenList(tokenList);
					vo.setMchNo(String.valueOf(merchant.getMchNo()));//商户号
					vo.setNotifyId(String.valueOf(IdGenerator.nextId()));
					vo.setTime(frontRecharge.getCreateTime());
					vo.setTransNo(frontRecharge.getOrderId().toString());//流水号
					vo.setUserId(tokenAddress.getMerchantUser());
					vo.setBlockOrderId(frontRecharge.getBlockOrderId());
					vo.setUserAddress(frontRecharge.getUserAddress());
					String sign=SignUtil.sign(vo, merchant.getSecretKey());
					vo.setSign(sign);
					mchNotify = new MchNotify();
					mchNotify.setType(MchNotifyStatus.RECHARGE_SUCCESS_NOTICE.value());
					mchNotify.setMchId(merchant.getId());
					mchNotify.setNotifyId(Long.parseLong(vo.getNotifyId()));
					mchNotify.setOrderNo(vo.getTransNo());
					mchNotify.setCreateTime(new Date());
					mchNotify.setCount(0);
					mchNotify.setNotifyStr(JSON.toJSONString(vo));//签名字符串
					mchNotify.setCallbackUrl(merchant.getRechargeCallback());
					mchNotifyBiz.insertSelective(mchNotify);
				}
			}
		//用户充值到账短信或者邮件通知
		FrontUser frontUser = ServiceUtil.selectUnionUserInfoById(frontRecharge.getUserId(),frontUserBiz);
		SendUtil.sendSmsOrEmail(SendMsgType.RECHARGE_COIN_IN.value(), EmailTemplateType.RECHARGE_COIN_IN.value(),frontUser,null,frontUser.getUserName(),frontRecharge.getSymbol(),frontRecharge.getRechargeAmount());



				return mchNotify;


	}

	/**
	 * 扫描提币状态为已审核的数据并更新为区块链已扫描并返回
	 * 
	 * @param frontWithdrawals
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public List<FrontWithdraw> updateWithdrawStatus(FrontWithdraw frontWithdrawals) {
		List<FrontWithdraw> withdrawList = frontWithdrawMapper.selectUnionExId(frontWithdrawals);
		for (FrontWithdraw frontWithdraw : withdrawList) {
			frontWithdraw.setStatus(FrontWithdrawStatus.WITHDRAWSEND.value());
			this.frontWithdrawMapper.updateByPrimaryKey(frontWithdraw);
		}
		return withdrawList;
	}

	public FrontWithdraw queryByWithdraw(FrontWithdraw frontWithdraw) {
		return frontWithdrawMapper.selectOne(frontWithdraw);
	}

	/**
	 * 到账成功更新资产信息
	 * 
	 * @param withdraw
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public MchNotify updateWithdraw(FrontWithdraw withdraw) throws Exception {
		frontWithdrawMapper.updateByPrimaryKey(withdraw);
		MchNotify mchNotify = null;
		if (withdraw.getType().equals(FrontWithdrawType.MERCHANT.value())) { // 推送给商户到账成功通知
			Merchant merchant = merchantBiz.getMerchantInfoByUserId(withdraw.getUserId());
			if(StringUtils.isNotBlank(merchant.getWithdrawCallback())){
				WithdrawNotifyVo vo = new WithdrawNotifyVo();
//				vo.setAmount(withdraw.getArrivalAmoumt().stripTrailingZeros().toPlainString());//到账数量
//				vo.setSymbol(withdraw.getSymbol());
				List<MchPayTokenModel> tokenList=new ArrayList<MchPayTokenModel>();
				MchPayTokenModel model=new MchPayTokenModel();
				model.setAmount(withdraw.getArrivalAmoumt());
				model.setSymbol(withdraw.getSymbol());
				tokenList.add(model);
				vo.setTokenList(tokenList);
				vo.setMchNo(String.valueOf(merchant.getMchNo()));//商户号
				vo.setNotifyId(String.valueOf(IdGenerator.nextId()));
				vo.setTime(withdraw.getUpdateTime());
				vo.setTransNo(withdraw.getTransNo());//流水号
				vo.setBlockOrderId(withdraw.getTransactionId());
				vo.setNonceStr(withdraw.getNonceStr());
				vo.setMchOrderNo(withdraw.getMchOrderNo());
                vo.setWithdrawAdd(withdraw.getUserAddress());
               if(FrontWithdrawStatus.TransSuccess.value().equals(withdraw.getStatus())){
				   vo.setStatus(EnableType.ENABLE.value());//提现状态
			   } else {
				   vo.setStatus(EnableType.DISABLE.value());//提现状态
			   }

				String sign=SignUtil.sign(vo, merchant.getSecretKey());
				vo.setSign(sign);



				mchNotify = new MchNotify();
				mchNotify.setType(MchNotifyStatus.WITHDRAW_SUCCESS_NOTICE.value());
				mchNotify.setMchId(merchant.getId());
				mchNotify.setNotifyId(Long.parseLong(vo.getNotifyId()));
				mchNotify.setOrderNo(vo.getTransNo());
				mchNotify.setCreateTime(new Date());
				mchNotify.setNotifyStr(JSON.toJSONString(vo));//签名字符串
				mchNotify.setCount(0);
				mchNotify.setCallbackUrl(merchant.getWithdrawCallback());
				mchNotifyBiz.insertSelective(mchNotify);
			}
		}




		if(withdraw.getStatus().equals(FrontWithdrawStatus.TransSuccess.value())){
			//提现成功
			AccountAssertLogVo vo = new AccountAssertLogVo();
			vo.setUserId(withdraw.getUserId());
			vo.setSymbol(withdraw.getSymbol());
			vo.setAmount(withdraw.getTradeAmount());//扣减交易数量（不是实际到账数量）
			vo.setChargeAmount(withdraw.getChargeAmount());//手续费数量
			vo.setChargeSymbol(withdraw.getSymbol());
			vo.setType(AccountLogType.WITHDRAW);
			vo.setTransNo(withdraw.getTransNo());//提现流水号
			vo.setRemark(AccountLogType.WITHDRAW.name());

			dcAssertAccountBiz.signUpdateAssert(vo, AccountSignType.ACCOUNT_WITHDRAW);

			logger.info("提现成功,发送短信,代币:" + withdraw.getSymbol() + ",数量:" + withdraw.getTradeAmount());
			Map<String, Object> param = InstanceUtil.newHashMap("userId", withdraw.getUserId());
			FrontUser user = userMapper.selectUnionUserInfo(param);
			SendUtil.sendSmsOrEmail(SendMsgType.WITHDRAW_COIN_OUT.value(),EmailTemplateType.WITHDRAW_COIN_OUT.value(),user,null,
					user.getUserName(),withdraw.getTradeAmount(),withdraw.getSymbol());
		} else {
			//提现失败
			AccountAssertLogVo vo = new AccountAssertLogVo();
			vo.setUserId(withdraw.getUserId());
			vo.setSymbol(withdraw.getSymbol());
			vo.setAmount(withdraw.getTradeAmount());//扣减交易数量（不是实际到账数量）
			vo.setChargeSymbol(withdraw.getSymbol());
			vo.setType(AccountLogType.ASSERT_UNFREEZE);//资产解冻
			vo.setTransNo(withdraw.getTransNo());//提现流水号
			vo.setRemark(AccountLogType.ASSERT_UNFREEZE.name());
			dcAssertAccountBiz.signUpdateAssert(vo, AccountSignType.ACCOUNT_WITHDRAW_DEDUTION);
		}
		return mchNotify;
	}

	/**
	 * 新增提币申请
	 * 
	 * @param withdraw
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public void addWithdraw(FrontWithdraw withdraw) throws Exception {
		FrontWithdraw param = new FrontWithdraw();
		param.setMchOrderNo(withdraw.getMchOrderNo());
		param.setUserId(withdraw.getUserId());
		FrontWithdraw record = frontWithdrawMapper.selectForUpdate(param);
		if(record !=  null){
			throw new MchException(MchResponseCode.MERCHANT_WITHDRAW_ORDER_NO_DUP.name());
		}

		frontWithdrawMapper.insert(withdraw);



		AccountAssertLogVo vo = new AccountAssertLogVo();
		vo.setUserId(withdraw.getUserId());
		vo.setSymbol(withdraw.getSymbol());
		vo.setAmount(withdraw.getTradeAmount());
		vo.setChargeSymbol(withdraw.getSymbol());
		vo.setType(AccountLogType.WITHDRAW_FREEZE);

		vo.setTransNo(withdraw.getTransNo());//提现流水号
		vo.setRemark(AccountLogType.WITHDRAW_FREEZE.name());

		dcAssertAccountBiz.signUpdateAssert(vo, AccountSignType.ACCOUNT_WITHDRAW_FREEZE);
	}

}
