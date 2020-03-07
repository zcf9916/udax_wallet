package com.udax.front.biz.merchant;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.github.wxiaoqi.security.common.entity.admin.WhiteExchInfo;
import com.github.wxiaoqi.security.common.entity.front.FrontUserInfo;
import com.github.wxiaoqi.security.common.entity.merchant.MchTradeDetail;
import com.github.wxiaoqi.security.common.enums.AccountLogType;
import com.github.wxiaoqi.security.common.enums.AccountSignType;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.enums.merchant.MchOrderSettleStatus;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.mapper.admin.WhiteExchInfoMapper;
import com.github.wxiaoqi.security.common.mapper.front.FrontUserInfoMapper;
import com.github.wxiaoqi.security.common.mapper.merchant.MchTradeDetailMapper;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import com.jcraft.jsch.UserInfo;
import com.udax.front.biz.DcAssertAccountBiz;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.entity.front.GeneratorId;
import com.github.wxiaoqi.security.common.entity.merchant.Merchant;
import com.github.wxiaoqi.security.common.enums.GeneratorIdType;
import com.github.wxiaoqi.security.common.enums.merchant.MchStatus;
import com.github.wxiaoqi.security.common.mapper.front.GeneratorIdMapper;
import com.github.wxiaoqi.security.common.mapper.merchant.MerchantMapper;
import com.github.wxiaoqi.security.common.util.merchant.GenerateCodeUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

/**
 * 
 *
 * @author Mr.AG
 * @email 463540703@qq.com
 * @date 2017-12-26 19:43:46
 */
@Service
@Slf4j
public class MerchantBiz extends BaseBiz<MerchantMapper, Merchant> {
	@Autowired
	private MerchantMapper merchantMapper;

	@Autowired
	private GeneratorIdMapper generatorIdMapper;

    @Autowired
	private WhiteExchInfoMapper exchInfoMapper;

    @Autowired
    private FrontUserInfoMapper userInfoMapper;

    @Autowired
    private DcAssertAccountBiz dcAssertAccountBiz;

    @Autowired
    private MchTradeDetailMapper mchTradeDetailMapper;

	//获取商户结算时间
	public Integer getMchSettleTime(Long userId){

        //先取商户表中的配置
		Merchant merchant = getMerchantInfoByUserId(userId);
		if( merchant == null){
			throw new BusinessException(ResponseCode.USER_NOT_EXIST.name());
		}
		if( merchant.getSettleTime() > 0){
			return merchant.getSettleTime();
		}
        //如果没配置,取白标中的配置
		FrontUserInfo userInfo = userInfoMapper.selectByUserId(userId);
		if( userInfo == null){
			throw new BusinessException(ResponseCode.USER_NOT_EXIST.name());
		}
		WhiteExchInfo exchInfo = exchInfoMapper.selectByPrimaryKey(userInfo.getExchangeId());
		if(exchInfo == null){
			return 0;
		}
		return exchInfo.getSettleTime();
	}

	@Transactional(rollbackFor = Exception.class)
	public void  mchSettle(MchTradeDetail t)  {
		try{
			MchTradeDetail mchparam = new MchTradeDetail();
			mchparam.setWalletOrderNo(t.getWalletOrderNo());
			//锁记录
			MchTradeDetail tradeDetail = mchTradeDetailMapper.selectForUpdate(mchparam);
			//总量-手续费-已退款数量 = 结算数量
			BigDecimal settleTotalAmount = tradeDetail.getAmount().subtract(tradeDetail.getChargeAmount()).subtract(tradeDetail.getRefundAmount());

			AccountAssertLogVo vo = new AccountAssertLogVo();
			vo.setUserId(tradeDetail.getMchUserId());
			vo.setSymbol(tradeDetail.getSymbol());
			vo.setAmount(settleTotalAmount);
			vo.setChargeSymbol(tradeDetail.getSymbol());
			vo.setType(AccountLogType.MERCHANT_SETTLE);
			//目前实时结算
			vo.setTransNo(tradeDetail.getWalletOrderNo());//支付流水号
			vo.setRemark(AccountLogType.MERCHANT_SETTLE.name());
			dcAssertAccountBiz.signUpdateAssert(vo,AccountSignType.MERCHANT_SETTLE);
			//更新结算状态
			MchTradeDetail updateParam = new MchTradeDetail();
			updateParam.setId(t.getId());
			updateParam.setUpdateTime(new Date());
			updateParam.setSettleStatus(MchOrderSettleStatus.SETTLE.value());

		}catch ( Exception e){
			logger.error("计算商户订单时出错,订单号:" + t.getWalletOrderNo(),e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
	}




	/**
	 * 根据用户Id查询商户信息
	 */
	public Merchant getMerchantInfoByUserId(Long userId) {

		Example example = new Example(Merchant.class);
		example.createCriteria().andEqualTo("userId", userId);
		List<Merchant> merList = merchantMapper.selectByExample(example);
		if (merList != null && !merList.isEmpty()) {
			return merList.get(0);
		}
		return null;
	}

	@Transactional(rollbackFor = Exception.class)
	public void authMerchant(Merchant merchantParam) {
		merchantParam.setMchStatus(MchStatus.SUBMIT.value());
		merchantParam.setSecretKey(RandomStringUtils.randomAlphabetic(32));// 设置秘钥
		if (merchantParam.getId() == null) {
			merchantParam.setMchNo(Long.valueOf(generateMerchantId()));// 商户号
			//merchantParam.setMchManagerId(merchantParam.getMchNo());// 商户后台登录账号
			merchantMapper.insertSelective(merchantParam);
		} else {
			merchantMapper.updateByPrimaryKeySelective(merchantParam);
		}
	}

	// 生成邀请码,通过 select for update加行锁来限制
	public String generateMerchantId() {

		GeneratorId id = generatorIdMapper.selectForUpdateByKey(GeneratorIdType.MERCHNAT_ACCOUNT.value());
		if (id == null) {
			throw new IllegalArgumentException("商户号配置没有对应的数据");
		}
		// 根据配置生成邀请码
		String merchantId = GenerateCodeUtil.generateCode(id.getV(), id.getIncreLen(), 1,
				GenerateCodeUtil.Data.MERCHANT_NO);
		if (StringUtils.isBlank(merchantId)) {
			throw new IllegalArgumentException("生成商户号失败,id:" + id.getV() + ",increLen:" + id.getIncreLen());
		}

		Example example = new Example(GeneratorId.class);
		example.createCriteria().andEqualTo("version", id.getVersion()).andEqualTo("k",
				GeneratorIdType.MERCHNAT_ACCOUNT.value());
		GeneratorId record = new GeneratorId();
		record.setVersion(id.getVersion() + 1);
		record.setV(merchantId);
		int result = generatorIdMapper.updateByExampleSelective(record, example);
		// 根据版本更新
		if (result == 1) {
			return merchantId;
		}
		throw new IllegalArgumentException("生成商户号失败");
	}
}