package com.udax.front.biz;

import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.HedgeDetail;
import com.github.wxiaoqi.security.common.entity.front.DcAssetAccount;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.enums.TransferOrderStatus;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.mapper.admin.HedgeDetailMapper;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.udax.front.util.CacheBizUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.entity.admin.UserOfferInfo;
import com.github.wxiaoqi.security.common.entity.front.FrontTransferDetail;
import com.github.wxiaoqi.security.common.enums.AccountSignType;
import com.github.wxiaoqi.security.common.enums.CurrencyTransferType;
import com.github.wxiaoqi.security.common.mapper.admin.UserOfferInfoMapper;
import com.github.wxiaoqi.security.common.mapper.front.FrontTransferDetailMapper;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class TransferBiz extends BaseBiz<FrontTransferDetailMapper, FrontTransferDetail> {

	@Autowired
	private DcAssertAccountBiz assertAccountBiz;

	@Autowired
	private UserOfferInfoMapper userOfferInfoMapper;

	@Autowired
	private FrontTransferDetailMapper frontTransferDetailMapper;

    @Autowired
    private HedgeDetailMapper hedgeDetailMapper;


    @Autowired
    private CacheBiz cacheBiz;

	@Transactional(rollbackFor = Exception.class)
	public FrontTransferDetail transfer(String orderNo) throws Exception {
		//查询订单
		FrontTransferDetail param = new FrontTransferDetail();
		param.setOrderNo(orderNo);
		FrontTransferDetail transferDetail = frontTransferDetailMapper.selectOne(param);
		//订单失效
		if(transferDetail == null || transferDetail.getStatus() != TransferOrderStatus.UNPAY.value()){
			throw new BusinessException(ResponseCode.TRANSFER_DEALED.name());
		}
		//是否大于超时时间
		if(transferDetail.getExpireTime().before(new Date())){
			throw new BusinessException(ResponseCode.TRANSFER_DEALED.name());
		}
        //如果是平台对冲
		if (transferDetail.getTransTargetType() == CurrencyTransferType.PLATFORM_HEDGE_FLAG.value()) {
            //判断是否需要对冲
			if(transferDetail.getHedgeFlag().equals(1)){
				HedgeDetail hedgeDetail = new HedgeDetail();
				BeanUtils.copyProperties(transferDetail,hedgeDetail);
				hedgeDetail.setTradeTime(new Date());
				String proxyCode = CacheBizUtil.getExchInfo(BaseContextHandler.getExId(),cacheBiz);//用户白标标示
				hedgeDetail.setProxyCode(proxyCode);
				hedgeDetail.setUserId(transferDetail.getUserId());
				hedgeDetail.setSymbol(transferDetail.getSrcSymbol()+"/"+transferDetail.getDstSymbol());
				hedgeDetailMapper.insertSelective(hedgeDetail);
			}
			assertAccountBiz.tranferCoinPlatForm(transferDetail,false);
//			//增加
//			assertAccountBiz.signUpdateAssert(transferDetail.getUserId(), transferDetail.getSrcSymbol(),
//					transferDetail.getSrcAmount(), AccountSignType.ACCOUNT_WITHDRAW);// 平台操作时扣除个人需要转换的币资产
//			assertAccountBiz.signUpdateAssert(transferDetail.getUserId(), transferDetail.getDstSymbol(),
//					transferDetail.getDstAmount(), AccountSignType.ACCOUNT_RECHARGE); // 增加目标币资产
		} else {
			assertAccountBiz.tranferCoinForUser(transferDetail);
//			UserOfferInfo userParam = new UserOfferInfo();
//			userParam.setSrcSymbol(transferDetail.getDstSymbol());
//			userParam.setDstSymbol(transferDetail.getSrcSymbol());
//			UserOfferInfo userOfferInfo = userOfferInfoMapper.selectOneForUpdate(userParam);
//            if(userOfferInfo.getRemainVolume().compareTo(transferDetail.getD))
//
//			//用户报价
//			assertAccountBiz.signUpdateAssert(transferDetail.getUserId(), transferDetail.getSrcSymbol(),
//					transferDetail.getSrcAmount(), AccountSignType.ACCOUNT_WITHDRAW);// 扣除个人徐亚转换的币资产
//			assertAccountBiz.signUpdateAssert(transferDetail.getUserId(), transferDetail.getDstSymbol(),
//					transferDetail.getDstAmount(), AccountSignType.ACCOUNT_RECHARGE); // 增加目标币资产
//
//			assertAccountBiz.signUpdateAssert(transferDetail.getReceiveUserId(), transferDetail.getDstSymbol(),
//					transferDetail.getSrcAmount(), AccountSignType.ACCOUNT_WITHDRAW);// 做市卖出方扣除目标币
//			assertAccountBiz.signUpdateAssert(transferDetail.getReceiveUserId(), transferDetail.getSrcSymbol(),
//					transferDetail.getDstAmount(), AccountSignType.ACCOUNT_RECHARGE); // 增加转换币资产
			
			//如果为上币方报价，需要更新用户报价剩余数量
			
		}

		FrontTransferDetail updateParam = new FrontTransferDetail();
		updateParam.setId(transferDetail.getId());
		updateParam.setStatus(TransferOrderStatus.PAYED.value());
		updateParam.setUpdateTime(new Date());
		frontTransferDetailMapper.updateByPrimaryKeySelective(updateParam);
		return transferDetail;
	}


	public UserOfferInfo getUserOffInfo(String srcSymbol, String dstSymbol) {
		UserOfferInfo offerInfo=new UserOfferInfo();
		offerInfo.setSrcSymbol(srcSymbol);
		offerInfo.setDstSymbol(dstSymbol);
		return userOfferInfoMapper.selectOneForUpdate(offerInfo);
	}

}
