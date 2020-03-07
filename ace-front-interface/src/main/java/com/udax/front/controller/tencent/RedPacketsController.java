package com.udax.front.controller.tencent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.*;
import com.github.wxiaoqi.security.common.entity.front.*;
import com.github.wxiaoqi.security.common.entity.ud.HOrderDetail;
import com.github.wxiaoqi.security.common.entity.ud.HParam;
import com.github.wxiaoqi.security.common.entity.ud.HUserInfo;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.enums.ud.UDOrderDetailStatus;
import com.github.wxiaoqi.security.common.mapper.ud.HOrderDetailMapper;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.support.Assert;
import com.github.wxiaoqi.security.common.util.*;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.github.wxiaoqi.security.common.vo.ValuationModeVo;
import com.udax.front.annotation.UserOperationAuthority;
import com.udax.front.biz.*;
import com.udax.front.biz.ud.HOrderDetailBiz;
import com.udax.front.biz.ud.HParamBiz;
import com.udax.front.biz.ud.HUserInfoBiz;
import com.udax.front.controller.BaseFrontController;
import com.udax.front.service.ServiceUtil;
import com.udax.front.tencent.Configure;
import com.udax.front.tencent.TencentSendUtils;
import com.udax.front.tencent.req.SendSingleMsgModel;
import com.udax.front.util.BizControllerUtil;
import com.udax.front.util.CacheBizUtil;
import com.udax.front.vo.reqvo.*;
import com.udax.front.vo.rspvo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.validation.Valid;
import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.wxiaoqi.security.common.constant.Constants.BATCHUPDATE_LIMIT;
import static com.github.wxiaoqi.security.common.constant.Constants.TENCENT_GROUP_NAME;

/**
 *
 *  红包交易
 * @author liuzz
 *
 */

@RestController
@RequestMapping("/wallet/redpackets")
@Slf4j
public class RedPacketsController extends BaseFrontController<DcAssertAccountBiz, DcAssetAccount> {




	@Autowired
	private DcAssertAccountBiz dcAssertAccountBiz;

	@Autowired
	private FrontUserBiz frontUserBiz;

    @Autowired
    private CacheBiz cacheBiz;


    @Autowired
    private RedPacketsLogBiz redPacketsLogBiz;

    @Autowired
    private RedPacketsSendBiz redPacketsSendBiz;

	@Autowired
	private TransferOrderBiz transferOrderBiz;

	/**
	 *  发红包
	 *  参数 代币 红包类型
	 *
	 * @return
	 */
	@PostMapping("sendRedPackets")
	public ObjectRestResponse sendRedPackets(@RequestBody @Valid SendRedPacketsModel model) throws Exception {
			Integer sendType = model.getSendType();//发送类型  0 个人红包   1 群红包
			Integer type = model.getType();  //  0. 普通红包  1. 随机红包
			//红包类型是否错误
			if(!RedPacketSendType.isType(sendType)
				  || !RedPacketType.isType(type)){
			  return new ObjectRestResponse().status(ResponseCode.PARAM_ERROR);
			}
		    if(sendType.intValue() == RedPacketSendType.SINGLE.value() ){
			    if(StringUtils.isBlank(model.getUserID())){
                    return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
                }
                FrontUser  frontUser = frontUserBiz.selectByUsername(model.getUserID());
			    if(frontUser == null){
                    return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
                }
                model.setReceiveUserId(frontUser.getId());
			}
			//群红包 ，设置群号
			if(sendType.intValue() == RedPacketSendType.MULTI.value()){
				if(StringUtils.isBlank(model.getGroupID())){
					return new ObjectRestResponse().status(ResponseCode.PARAM_ERROR);
				}
			}

			boolean flag = ServiceUtil.judgePayPassword(model.getPassword(),BaseContextHandler.getUserID(),frontUserBiz);
			if(!flag){
				return new ObjectRestResponse<>().status(ResponseCode.TRADE_PWD_ERROR);
			}

			RedPacketSend send = redPacketsSendBiz.sendRedPackets(model);
			RedPacketSendRspVo rspVo = new RedPacketSendRspVo();
			BeanUtils.copyProperties(send,rspVo);
			return new ObjectRestResponse().data(rspVo);
	}


	/**
	 *  抢红包(打开红包)
	 *  参数 (订单号或者ID)
	 *
	 * @return
	 */
	@PostMapping("clickRedPackets")
	public ObjectRestResponse clickRedPackets(@RequestBody  @Valid PacketsModel model) throws  Exception {
		//从redis缓存中判断是否还有名额
		if(StringUtils.isBlank(model.getOrderNo())){
			return new ObjectRestResponse(false);
		}
		//从redis缓存中判断是否还有名额
		RedPacketSend send  = (RedPacketSend) CacheUtil.getCache().get(Constants.REDPACKETS.RPORDER + model.getOrderNo());
		if(send == null){
			return new ObjectRestResponse(false);
		}
		RedPacketQueryRspVo rspVo = new RedPacketQueryRspVo();
		rspVo.setReceiveNum(send.getCurrentNum());
		rspVo.setTotalNum(send.getNum());
		Long timeDiff = (LocalDateUtil.localDateTime2Date(LocalDateTime.now()).getTime() - send.getCreateTime().getTime())/1000;
		log.info("订单状态:" + send.getStatus());
		log.info("当前时间:" + LocalDateUtil.localDateTime2Date(LocalDateTime.now()).getTime() + ";订单时间" + send.getCreateTime().getTime());
		if((send.getStatus() != null && send.getStatus().intValue() > RedPacketOrderStatus.ALLDONE.value()) ||
				timeDiff > 86400){
			rspVo.setIfExpire(true);
		}
		//从缓存中拿到记录,如果用户已经抢过了,返回抢到的金额,以及对应的红包抢的记录列表
		List<Serializable> list = CacheUtil.getCache().getRangeList(Constants.REDPACKETS.RP_USERID + send.getOrderNo(),0L, 100L);
		List<Serializable> cacheLogList = CacheUtil.getCache().getRangeList(Constants.REDPACKETS.RPLOG + send.getOrderNo(),0L, 100L);
		rspVo.setList(cacheLogList);
		if(list.contains(BaseContextHandler.getUserID())){
			rspVo.setIfReceive(true);
			return new ObjectRestResponse().data(rspVo);
		}
		return new ObjectRestResponse().data(rspVo);
	}

	/**
	 *
	 *  参数 (订单号或者ID)
	 *  拆红包
	 * @return
	 */
	@PostMapping("openRedPackets")
	public ObjectRestResponse openRedPackets(@RequestBody  @Valid PacketsModel model) throws Exception {
//从redis缓存中判断是否还有名额
		if(StringUtils.isBlank(model.getOrderNo())){
		    log.error("订单号错误");
			return new ObjectRestResponse(false);
		}
		//从redis缓存中判断是否还有名额
		RedPacketSend send  = (RedPacketSend) CacheUtil.getCache().get(Constants.REDPACKETS.RPORDER + model.getOrderNo());
		if(send == null){
            log.error("缓存获取订单号错误");
			return new ObjectRestResponse(false);
		}
		//如果是单人红包,判断下抢的人的用户id
		if(send.getSendType().intValue() == RedPacketSendType.SINGLE.value() && send.getReceiveUserId().intValue() != BaseContextHandler.getUserID().longValue()){
            log.error("单人红包必须制定的用户可以抢");
			return new ObjectRestResponse(false);
		}
		if(send.getSendType().intValue() == RedPacketSendType.MULTI.value()){
			String groupId = send.getGroupId();
			log.info("当前用户名：" + BaseContextHandler.getUsername());
			//判断群组中是否包含这个人
			if(!TencentSendUtils.getInfoInGroup(BaseContextHandler.getUsername(),groupId)){
				return new ObjectRestResponse(false);
			}
		}

		//BaseContextHandler.setUserID(model.getUserId());
		RedPacketQueryRspVo rspVo = new RedPacketQueryRspVo();
		rspVo.setReceiveNum(send.getCurrentNum());
		rspVo.setTotalNum(send.getNum());
		if(send.getStatus() != null && send.getStatus().intValue() > RedPacketOrderStatus.ALLDONE.value()){
			rspVo.setIfExpire(true);
		}
		//从缓存中拿到记录,如果用户已经抢过了,返回抢到的金额,以及对应的红包抢的记录列表
		List<Serializable> list = CacheUtil.getCache().getRangeList(Constants.REDPACKETS.RP_USERID + send.getOrderNo(),0L, 100L);
		if(list.contains(BaseContextHandler.getUserID())){
			rspVo.setIfReceive(true);
			List<Serializable> cacheLogList = CacheUtil.getCache().getRangeList(Constants.REDPACKETS.RPLOG + send.getOrderNo(),0L, 100L);
			rspVo.setReceiveNum(cacheLogList != null ? cacheLogList.size() :  0);
			rspVo.setList(cacheLogList);
			return new ObjectRestResponse().data(rspVo);
		}

		//如果数量还有剩余
		if(send.getCurrentNum() < send.getNum()){
			//拆成功,redis数据-1
			//开始数据库拆红包
			ObjectRestResponse rsp = redPacketsLogBiz.openRedPacket(model.getOrderNo());
			//如果抢成功了
			if( rsp.isRel()){
				if(send.getSendType().intValue() == RedPacketSendType.MULTI.value()){
					log.info("发送群红包：" + send.getOrderNo());
					TencentSendUtils.multiRedPackMsg((RedPacketLog) rsp.getData(),frontUserBiz);
				} else {
					log.info("发送个人红包：" + send.getOrderNo());
					TencentSendUtils.singleRedPackMsg((RedPacketLog) rsp.getData(),frontUserBiz);
				}
				//抢红包成功
				//从redis缓存中判断是否还有名额
				rspVo.setIfReceive(true);
				List<Serializable> cacheLogList = CacheUtil.getCache().getRangeList(Constants.REDPACKETS.RPLOG + send.getOrderNo(),0L, 100L);
				rspVo.setReceiveNum(cacheLogList != null ? cacheLogList.size() :  0);
				rspVo.setList(cacheLogList);
				return new ObjectRestResponse().data(rspVo);
			}
		}
		return new ObjectRestResponse().data(rspVo);


	}



	/**
	 * 根据红包订单号查询红包当前的记录
	 * @return
	 */
	@PostMapping("redPacketsLog")
	public ObjectRestResponse redPacketsLog(@RequestBody @Valid PacketsModel model) throws Exception {
		//从redis缓存中判断是否还有名额
		if(StringUtils.isBlank(model.getOrderNo())){
			return new ObjectRestResponse(false);
		}
		//从redis缓存中判断是否还有名额
		RedPacketSend send  = (RedPacketSend) CacheUtil.getCache().get(Constants.REDPACKETS.RPORDER + model.getOrderNo());
		if(send == null){
			return new ObjectRestResponse(false);
		}
		//BaseContextHandler.setUserID(model.getUserId());
		RedPacketQueryRspVo rspVo = new RedPacketQueryRspVo();
		rspVo.setTotalNum(send.getNum());
		if(send.getStatus() != null && send.getStatus().intValue() > RedPacketOrderStatus.ALLDONE.value()){
			rspVo.setIfExpire(true);
		}
		List<Serializable> list = CacheUtil.getCache().getRangeList(Constants.REDPACKETS.RP_USERID + send.getOrderNo(),0L, 100L);
		if(list.contains(BaseContextHandler.getUserID())){
			rspVo.setIfReceive(true);
		}
		List<Serializable> cacheLogList = CacheUtil.getCache().getRangeList(Constants.REDPACKETS.RPLOG + send.getOrderNo(),0L, 100L);
		rspVo.setReceiveNum(cacheLogList != null ? cacheLogList.size() :  0);
		rspVo.setList(cacheLogList);
		return new ObjectRestResponse().data(rspVo);

	}



	// 红包转账预下单
	@UserOperationAuthority("USER_TRANSFER") //判断当前用户转账权限是否被禁用
	@PostMapping("/rpTransPreOrder")
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


		//余额是否足够
		DcAssetAccount dcAssetAccount = new DcAssetAccount();
		dcAssetAccount.setSymbol(model.getDcCode());
		dcAssetAccount.setUserId(BaseContextHandler.getUserID());
		dcAssetAccount = dcAssertAccountBiz.selectOne(dcAssetAccount);
		if(dcAssetAccount == null || dcAssetAccount.getAvailableAmount().compareTo(model.getTransferAmount()) < 0){
			return new ObjectRestResponse().status(ResponseCode.BALANCE_NOT_ENOUGH);
		}

		Date expireTime = CacheBizUtil.getExpireTime(Constants.BaseParam.TRANS_ORDER_EXPIRE,cacheBiz);


		TransferOrder transferRecord = new TransferOrder();
		transferRecord.setOrderNo(String.valueOf(IdGenerator.nextId()));
		transferRecord.setUserId(BaseContextHandler.getUserID());
		transferRecord.setUserName(transferUser.getUserName());
		transferRecord.setReceiveUserId(user.getId());
		transferRecord.setReceiveUserName(user.getUserName());
		transferRecord.setAmount(model.getTransferAmount());
		transferRecord.setChargeAmount(BigDecimal.ZERO);
		transferRecord.setArrivalAmount(model.getTransferAmount());
		transferRecord.setSymbol(model.getDcCode());
		transferRecord.setCreateTime(LocalDateUtil.localDateTime2Date(LocalDateTime.now()));
		//过期时间=当前时间+过期时间配置
		transferRecord.setExpireTime(expireTime);
		transferRecord.setType(TransferType.REDPACKET.value());
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
        ObjectRestResponse rsp =  baseBiz.transferAssert(model.getOrderNo());
        //往腾讯服务器发送消息
        TransferOrder order = (TransferOrder)rsp.getData();
        if(order != null){
			TencentSendUtils.singleMsg(order,frontUserBiz);
		}
		return rsp.data(null);
    }


	public static void main(String[] args) {
		System.out.println((1573282677448L - 1573268770000L)/1000);
	}
}
