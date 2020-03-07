package com.udax.front.controller;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.configuration.KeyConfiguration;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.entity.admin.*;
import com.github.wxiaoqi.security.common.entity.front.*;
import com.github.wxiaoqi.security.common.entity.ud.HCommissionRelation;
import com.github.wxiaoqi.security.common.entity.ud.HUserInfo;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.util.*;
import com.udax.front.annotation.UserOperationAuthority;
import com.udax.front.biz.common.CommonControllerBiz;
import com.udax.front.biz.ud.HCommissionRelationBiz;
import com.udax.front.util.BizControllerUtil;
import com.udax.front.vo.reqvo.*;
import com.udax.front.vo.reqvo.ud.QueueListModel;
import com.udax.front.vo.rspvo.*;
import com.udax.front.biz.*;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.udax.front.bean.WithdrawModel;
import com.udax.front.biz.BlockChainBiz;
import com.udax.front.biz.DcAssertAccountBiz;
import com.udax.front.biz.FrontUserBiz;
import com.udax.front.biz.UserAccountBiz;
import com.udax.front.bizmodel.CommonBiz;
import com.udax.front.service.ServiceUtil;
import com.udax.front.util.CacheBizUtil;

import lombok.extern.slf4j.Slf4j;
import tk.mybatis.mapper.entity.Example;

import javax.validation.Valid;

@RestController
@RequestMapping("/wallet/account")
@Slf4j
public class AccountController extends BaseFrontController<DcAssertAccountBiz, DcAssetAccount> {

	@Autowired
	private UserAccountBiz userAccountBiz;
	@Autowired
	private BlockChainBiz blockChainBiz;

	@Autowired
	private CacheBiz cacheBiz;


	@Autowired
	private KeyConfiguration config;



	@Autowired
	private FrontUserBiz frontUserBiz;

	@Autowired
    private HCommissionRelationBiz cmsRelationBiz;


	@Autowired
	private CommissionLogBiz cmsLogBiz;

    @Autowired
    private FrontRechargeBiz frontRechargeBiz;


	@Autowired
	private FrontWithdrawAddBiz frontWithdrawAddBiz;

    @Autowired
    private FrontWithdrawBiz frontWithdrawBiz;

	@Autowired
	private CommonControllerBiz commonBiz;




	/**
	 * 查询账户信息列表接口
	 * 
	 * @return
	 */
	@PostMapping("accountList")
	public ObjectRestResponse accountList() throws Exception {
        Map<String,CfgDcRechargeWithdraw> symbolConfig = CacheBizUtil.getSymbolConfig(cacheBiz);
		ObjectRestResponse result = new ObjectRestResponse().rel(true);
		DcAssetAccount assetAccount = new DcAssetAccount();
		assetAccount.setUserId(BaseContextHandler.getUserID());
		//资产数据
		List<DcAssetAccount> accountList = userAccountBiz.selectList(assetAccount);
		//可用资产币种map
		Map<String,DcAssetAccount> assertMap = new HashMap<>();
		if(StringUtil.listIsNotBlank(accountList)){
			assertMap = accountList.stream().collect(Collectors.toMap(DcAssetAccount::getSymbol,DcAssetAccount->DcAssetAccount));
		}
		//白标可用币种列表(有可能重复,公链类型不一样)
		List<BasicSymbol> basicList = CacheBizUtil.getBasicSymbolExch(cacheBiz,BaseContextHandler.getAppExId());
		//key是代币,value是公链类型
		Map<String,TreeSet<String>> basicSymbolMap = basicList.stream().collect(Collectors.toMap(
				BasicSymbol::getSymbol,
					e -> Sets.newTreeSet(e.getProtocolType() == null ? "" : e.getProtocolType()),
				(TreeSet<String> oList ,TreeSet<String> nList) -> {
                       oList.addAll(nList);
                       return oList;
		        }
		));

        //过滤不需要显示的币种
		List<DcAssetAccount> rspAssertList = InstanceUtil.newArrayList();
		for(String k : basicSymbolMap.keySet()){
			//如果资产里包含,加入列表
			if(assertMap.get(k) != null){
				rspAssertList.add(assertMap.get(k));
			   continue;
			}
			//如果资产不包含,重新构建一条数据
			DcAssetAccount account  = new DcAssetAccount();
			account.setSymbol(k);
			account.setTotalAmount(BigDecimal.ZERO);
			account.setAvailableAmount(BigDecimal.ZERO);
			account.setFreezeAmount(BigDecimal.ZERO);
			account.setWaitConfirmAmount(BigDecimal.ZERO);
			rspAssertList.add(account);
			//symbolMap.put(k,k);
		}


		//用户资产数据转vo
        List<AccountListModel> list = BizControllerUtil.transferEntityToListVo(AccountListModel.class,rspAssertList);
        list.stream().forEach((l) ->{
            if(basicSymbolMap.get(l.getSymbol()).contains("")){
                basicSymbolMap.get(l.getSymbol()).remove("");
            }
            l.setProtocolTypeList(basicSymbolMap.get(l.getSymbol()));

			CfgDcRechargeWithdraw config=null;
             config = symbolConfig.get(l.getSymbol()+":"+BaseContextHandler.getAppExId());
             if (config==null){
				 config = symbolConfig.get(l.getSymbol()+":"+ AdminCommonConstant.ROOT);
			 }
            //设置是否可以充提币数据
            if( config != null){
                l.setCanRecharge(config.getRechargeStatus());
                l.setCanWithdraw(config.getWithdrawStatus());
            } else {
				l.setCanRecharge(EnableType.DISABLE.value());//默认禁用
				l.setCanWithdraw(EnableType.DISABLE.value());//默认禁用
			}
        });
		result.setData(list);
		return result;
	}

	/**
	 * 获取充币地址
	 * 
	 * @return
	 */
	// @UserOperationAuthority("RECHARGE_COIN") //判断当前用户充币权限是否被禁用
	@PostMapping("/getRechargeAdd")
	public ObjectRestResponse getRechargeAdd(@RequestBody @Valid DcCodeModel model) {
		//验证代币
		if(StringUtils.isNotBlank(model.getDcCode())){
			boolean flag = ServiceUtil.validDcCode(model.getDcCode(),cacheBiz);
			if(!flag) return new ObjectRestResponse<>().status(ResponseCode.DC_INVALID);
		}

		DictData data = CacheBizUtil.getRechargeProtocol(cacheBiz).get(model.getProtocolType());
		String dcCode = data == null ? model.getDcCode() : data.getDictValue();
		String userAddress = commonBiz.getRechargetAdd(BaseContextHandler.getUserID(),
                dcCode,null);

		String protocolType = model.getProtocolType();
		String language =  BaseContextHandler.getLanguage();
		CfgSymbolDescription symbolDescription =  CacheBizUtil.getCfgSymbolDescription(cacheBiz,model.getDcCode(),BaseContextHandler.getAppExId(),language,protocolType);
		//  List<DictData> list = CacheBizUtil.getValuationManner(cacheBiz,language);
		Map<String,Object> rspMap = InstanceUtil.newHashMap();
		if(model.getDcCode().equals("XRP")&&userAddress.contains("_")){
			rspMap.put("address",userAddress);
			rspMap.put("tag",userAddress.split("_").length > 1 ? userAddress.split("_")[1] : "");
		} else {
			rspMap.put("address",userAddress);
			rspMap.put("tag","");
		}

		if(symbolDescription != null){
			rspMap.put("rechargeDesp",symbolDescription.getRechargeDesp());//充值描述
		}

		return new ObjectRestResponse<>().data(rspMap);
	}



//
//	/**
//	 * 获取充币地址二维码
//	 *
//	 * @return
//	 */
//	@PostMapping("/{symbol}/rechargeQrcode")
//	public ObjectRestResponse bindAddress(@PathVariable String symbol) throws IOException {
//
//		ObjectRestResponse objectRestResponse =  getRechargeAddress(symbol);
//		if(!objectRestResponse.isRel()){
//			return objectRestResponse;
//		}
//		QrcodeUtil.produceQR(response.getOutputStream(), (String)objectRestResponse.getData(), 350, 350);
//		return new ObjectRestResponse();
//	}


	/**
	 * 验证充币地址是否为平台所返回
	 * 0 是  1否
	 * @param model
	 * @return
	 */
	@PostMapping("checkAddress")
	public Object checkAddress(@RequestBody @Valid CheckRechargeAddModel model) {
		ObjectRestResponse result = new ObjectRestResponse();
		FrontTokenAddress tokenUser = new FrontTokenAddress();
		Long userId = BaseContextHandler.getUserID();
		tokenUser.setUserId(userId);
		Map<String, Object> symbolMap = CacheBizUtil.getTokenListCache(cacheBiz);
		if( StringUtil.mapIsBlank(symbolMap)) {
			result.data(1);
			return result;
		}
		DictData data = CacheBizUtil.getRechargeProtocol(cacheBiz).get(model.getProtocolType());
		String dcCode = data == null ? model.getSymbol() : data.getDictValue();

		tokenUser.setSymbol(dcCode);
		if (symbolMap.containsKey(model.getSymbol())) {
			tokenUser.setSymbol(Constants.SYMBOL_ETH);
		}
		if(model.getSymbol().equals("XRP")&& StringUtils.isNotBlank(model.getTag()) && !model.getUserAddress().contains("_")){
			model.setUserAddress(model.getUserAddress() + "_" + model.getTag());
		}

		log.info("充值地址:" + model.getUserAddress() + ";代币:" + dcCode);
		tokenUser.setUserAddress(SecurityUtil.encryptDes(model.getUserAddress(), config.getWalletKey().getBytes()));
		tokenUser = blockChainBiz.selectOne(tokenUser);
		if (tokenUser != null && tokenUser.getId() != null) {
			result.data(0); // 查询到后根据该值判断地址为平台地址
		} else {
		    result.data(1);
        }
		return result;
	}

	/**
	 * 用户得到代币对应的手续费规则以及可用余额
	 * 
	 * @param model
	 * @return
	 */
	// @UserOperationAuthority("WITHDRAW_COIN") //判断当前用户提币权限是否被禁用
	@PostMapping("/withdrawInfo")
	public ObjectRestResponse withdrawReq(@RequestBody @Valid DcCodeModel model) {
		//验证代币
		String symbol = model.getDcCode();
		if(StringUtils.isNotBlank(symbol)){
			boolean flag = ServiceUtil.validDcCode(symbol,cacheBiz);
			if(!flag) return new ObjectRestResponse<>().status(ResponseCode.DC_INVALID);
		}

		ObjectRestResponse<WithdrawInfoModel> result = new ObjectRestResponse<WithdrawInfoModel>();
		WithdrawInfoModel withdrawInfo = new WithdrawInfoModel();
		DcAssetAccount accountAssert = new DcAssetAccount();
		accountAssert.setUserId(BaseContextHandler.getUserID());
		accountAssert.setSymbol(symbol);
		accountAssert = userAccountBiz.selectOne(accountAssert);
		if (accountAssert != null && accountAssert.getId() != null) {
			withdrawInfo.setAvailableAmount(accountAssert.getAvailableAmount());
		}

		String protocolType = model.getProtocolType();
		CfgCurrencyCharge currencyCharge = CacheBizUtil.getSymbolCharge(cacheBiz,symbol,BaseContextHandler.getAppExId(),protocolType);
		CfgDcRechargeWithdraw symbolConfig = CacheBizUtil.getSymbolConfigShow(symbol,protocolType,BaseContextHandler.getAppExId(),cacheBiz);
		if( currencyCharge == null || symbolConfig == null){
			result.status(ResponseCode.WITHDRAW_50004);
			return result;
		}
		String language = BaseContextHandler.getLanguage();
		CfgSymbolDescription symbolDescription =  CacheBizUtil.getCfgSymbolDescription(cacheBiz,model.getDcCode(),BaseContextHandler.getAppExId(),language,protocolType);

		withdrawInfo.setChargeType(currencyCharge.getDcWithdrawCharge().getChargeType());// 设置手续费规则，按1：固定金额or 2:按比列
		withdrawInfo.setChargeAmount(currencyCharge.getDcWithdrawCharge().getChargeValue());
		withdrawInfo.setWithdrawQuota(symbolConfig.getMaxWithdrawAmount());
		withdrawInfo.setLimitWithdraw(symbolConfig.getMinWithdrawAmount());
		if(symbolDescription != null){
			withdrawInfo.setWithdrawDesp(symbolDescription.getWithdrawDesp());
		}
		//XRP单独处理
		withdrawInfo.setNeedTag(EnableType.DISABLE.value());
		withdrawInfo.setAddressSplitParam("");
		if(model.getDcCode().equals("XRP")){
			withdrawInfo.setNeedTag(EnableType.ENABLE.value());
			withdrawInfo.setAddressSplitParam("_");
		}
		result.setData(withdrawInfo);
		return result;
	}

	/**
	 * 处理提币申请
	 * 
	 * @param withdrawModel
	 * @return
	 * @throws Exception
	 */
	@UserOperationAuthority("WITHDRAW_COIN") //判断当前用户提币权限是否被禁用
	@PostMapping("withdraw")
	public Object withdrawals(@RequestBody WithdrawModel withdrawModel) throws Exception {

		FrontUser frontUser = ServiceUtil.selectUnionUserInfoById(BaseContextHandler.getUserID(),frontUserBiz);
		// 调用验证码公共方法校验验证码
		CommonBiz.commonVerifyMethod(frontUser, SendMsgType.WITHDRAW_REQUEST.value(), withdrawModel.getMobileCode(),
				withdrawModel.getEmailCode());
		ObjectRestResponse response = commonBiz.withdraw(withdrawModel,frontUser,FrontWithdrawType.NORMAL,true);
		if(!response.isRel()){
			return response;
		}
		response.data(null);
		CommonBiz.clearVerifyMethod(frontUser);
		return response;
	}

	/**
	 * 查询充值记录
	 * 
	 * @return
	 */
	@PostMapping("rechargeRecords")
	public TableResultResponse queryRecords(@RequestBody PageInfo pageInfo) throws Exception {
		ObjectRestResponse result = new ObjectRestResponse().rel(true);
		Long userId = BaseContextHandler.getUserID();
		Query query = new Query();
		query.setOrderByInfo("create_time desc");
		query.setPage(pageInfo.getPage());
		query.setLimit(pageInfo.getLimit());
		query.put("userId", userId);
		TableResultResponse pageResult = pageQueryTransToVo(query, FrontRecharge.class,RechargeRecordModel.class, frontRechargeBiz);
		return pageResult;
	}

	/**
	 * 查询提币记录
	 * 
	 * @param pageInfo
	 * @return
	 */
	@PostMapping("withdrawRecords")
	public TableResultResponse withdrawRecords(@RequestBody PageInfo pageInfo) throws Exception {
		Long userId = BaseContextHandler.getUserID();
		Query query = new Query();
		query.setOrderByInfo("create_time desc");
		query.setPage(pageInfo.getPage());
		query.setLimit(pageInfo.getLimit());
		query.put("userId", userId);
		TableResultResponse<FrontWithdraw> pageResult = pageQueryTransToVo(query, FrontWithdraw.class,WithdrawRecordModel.class, frontWithdrawBiz);
		return pageResult;
	}



	/**
	 * 查询用户提币地址
	 *
	 * @param model
	 * @return
	 */
	@PostMapping("withdrawaddList")
	public TableResultResponse withdrawaddList(@RequestBody @Valid WithdrawAddListModel model) throws Exception {
		Long userId = BaseContextHandler.getUserID();
		Query query = new Query();
		query.setPage(model.getPage());
		query.setLimit(model.getLimit());
		query.put("symbol",model.getSymbol());
		query.put("userId", userId);
		query.put("enable",EnableType.ENABLE.value());
		query.put("type", FrontWithdrawType.NORMAL.value());//用户提币类型
		TableResultResponse<FrontWithdraw> pageResult = pageQueryTransToVo(query, FrontWithdrawAdd.class,WithdrawAddListRsp.class, frontWithdrawAddBiz);
		return pageResult;
	}


	/**
	 * 添加提币地址
	 *
	 * @param model
	 * @return
	 */
	@PostMapping("addWithdrawadd")
	public Object addWithdrawadd(@RequestBody @Valid AddWithdrawModel model) throws Exception {
		if(StringUtils.isNotBlank(model.getRemark()) && model.getRemark().length() > 500){
			return new ObjectRestResponse<>().status(ResponseCode.WITHDRAWADD_REMARK_TOOLONG);
		}
		FrontWithdrawAdd queryParam = new FrontWithdrawAdd();
		queryParam.setUserId(BaseContextHandler.getUserID());
		queryParam.setWithdrawAdd(model.getAddress());
		queryParam.setSymbol(model.getSymbol());
		FrontWithdrawAdd frontWithdrawAdd = frontWithdrawAddBiz.selectOne(queryParam);
		if(frontWithdrawAdd == null){
			frontWithdrawAdd = new FrontWithdrawAdd();
			frontWithdrawAdd.setWithdrawAdd(model.getAddress());
			frontWithdrawAdd.setEnable(EnableType.ENABLE.value());
			frontWithdrawAdd.setUserId(BaseContextHandler.getUserID());
			frontWithdrawAdd.setSymbol(model.getSymbol());
			frontWithdrawAdd.setType(FrontWithdrawType.NORMAL.value());
			frontWithdrawAdd.setCreateTime(new Date());
			frontWithdrawAdd.setRemark(model.getRemark());
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

		return new ObjectRestResponse<>().status(ResponseCode.ADD_DUP);
	}

	/**
	 * 删除用户提币地址
	 *
	 * @param id
	 * @return
	 */
	@PostMapping("delWithadd")
	public Object delWithadd(@RequestParam Long id) throws Exception {
		if( id == null || id.longValue() < 1){
			return  new ObjectRestResponse<>().status(ResponseCode.PARAM_ERROR);
		}
        FrontWithdrawAdd queryParam = new FrontWithdrawAdd();
		queryParam.setUserId(BaseContextHandler.getUserID());
		queryParam.setEnable(EnableType.ENABLE.value());
		queryParam.setId(id);
		FrontWithdrawAdd frontWithdrawAdd = frontWithdrawAddBiz.selectOne(queryParam);
        if(frontWithdrawAdd == null){
			return  new ObjectRestResponse<>().status(ResponseCode.PARAM_ERROR);
		}
		FrontWithdrawAdd updateParam = new FrontWithdrawAdd();
		updateParam.setId(id);
		updateParam.setEnable(EnableType.DISABLE.value());
		frontWithdrawAddBiz.updateSelectiveById(updateParam);
		return new ObjectRestResponse<>();
	}


	//分成基本信息
	@RequestMapping(value = "/cms/info", method = RequestMethod.GET)
	@ResponseBody
	public ObjectRestResponse info() throws Exception{
		//第一排：推广总人数（下线3代）      总收益：0
		//邀请记录信息显示：注册时间、邀请人手机号（图标显示）、邀请人邮箱（图标显示）、属于当前推广人发展的第几代（显示第几代）
		Map<String,Object> rspMap = InstanceUtil.newHashMap();
		FrontUser user = CacheBizUtil.getFrontUserCache(BaseContextHandler.getUserID(),frontUserBiz);
		if(user == null){
			return new ObjectRestResponse().status(ResponseCode.PARAM_ERROR);
		}
		Example example = new Example(HCommissionRelation.class);
		example.createCriteria().andEqualTo("receiveUserId",user.getId())
				.andLessThanOrEqualTo("level",user.getUserInfo().getLevel() + 3);
		//推广总人数
		int count =  cmsRelationBiz.selectCountByExample(example);
        //总收益
		BigDecimal totalCms  = cmsLogBiz.getUserTotalCms(user.getId());

		rspMap.put("count",count);//推广总人数
		rspMap.put("totalCms",Optional.ofNullable(totalCms).orElseGet(()->{return BigDecimal.ZERO;}).setScale(2,BigDecimal.ROUND_UP).stripTrailingZeros().toPlainString() + " "+Constants.QUOTATION_DCCODE);//总收益



		return new ObjectRestResponse().data(rspMap);
	}


	//查看邀请记录
	@RequestMapping(value = "/cms/inviteRecord", method = RequestMethod.GET)
	@ResponseBody
	//@RequestBody @Valid QueueListModel model
	public TableResultResponse currentQueue(@RequestParam int page,@RequestParam int limit) throws Exception{
		Query query = new Query();
		query.setLimit(limit > 50 ? 10 : limit);
		query.setPage(page);
		FrontUser user = CacheBizUtil.getFrontUserCache(BaseContextHandler.getUserID(),frontUserBiz);
		query.put("receiveUserId",BaseContextHandler.getUserID());
		query.put("level:<=",user.getUserInfo().getLevel() + 3);//3代以内的用户
		TableResultResponse<HCommissionRelation> result = cmsRelationBiz.pageQuery(query);
		List<JSONObject> rspList = result.getData().getRows().stream().collect(
				Collectors.mapping( l -> {
					JSONObject ob = new JSONObject();
					FrontUser temp = new FrontUser();
					FrontUser u = Optional.ofNullable(CacheBizUtil.getFrontUserCache(l.getUserId(),frontUserBiz)).orElse(temp);
					ob.put("userName",Optional.ofNullable(u.getUserName()).orElseGet(() -> {return "";}));
					ob.put("mobile",Optional.ofNullable(u.getMobile()).orElseGet(() -> {return "";}));
					ob.put("email",Optional.ofNullable(u.getEmail()).orElseGet(() ->{return "";}));
					ob.put("createTime",u.getUserInfo().getCreateTime().getTime());//订
					ob.put("level",u.getUserInfo().getLevel() - user.getUserInfo().getLevel());//第几代
					return ob;
				}, Collectors.toList())
		);

		TableResultResponse rspPage =  new TableResultResponse<>();
		rspPage.getData().setRows(rspList);
		rspPage.getData().setTotal(result.getData().getTotal());
		rspPage.setRel(result.isRel());
		rspPage.setStatus(result.getStatus());
		rspPage.setMessage(result.getMessage());
		return rspPage;


	}


	//查看分成的历史
	@RequestMapping(value = "/cms/record", method = RequestMethod.GET)
	@ResponseBody
	//@RequestBody @Valid QueueListModel model
	public TableResultResponse record(@RequestParam int page,@RequestParam int limit) throws Exception{
		//查询自己已经结算的分成
		Map<String,Object> param = InstanceUtil.newHashMap();
		param.put("receiveUserId",BaseContextHandler.getUserID());
		param.put("settleStatus",EnableType.ENABLE.value());
		param.put("orderByInfo","orderTime desc");
		Query query = new Query(param);
		TableResultResponse<CommissionLog> pageResult =  cmsLogBiz.pageQuery(query);
		Map<Long,String> userIdMap = InstanceUtil.newHashMap();
		List<JSONObject> rspList = pageResult.getData().getRows().stream().collect(
				Collectors.mapping( l -> {

					JSONObject ob = new JSONObject();
					if(userIdMap.get(l.getTradeuserId()) == null){
						FrontUser temp = new FrontUser();
						FrontUser user = Optional.ofNullable(CacheBizUtil.getFrontUserCache(l.getTradeuserId(),frontUserBiz)).orElse(temp);
						ob.put("userName",user.getUserName());
						userIdMap.put(l.getTradeuserId(),user.getUserName());
					} else {
						ob.put("userName", userIdMap.get(l.getTradeuserId()));
					}

					ob.put("orderTime",l.getOrderTime());//订单时间

					ob.put("type", Resources.getMessage(l.getOrderType() == 1 ? "TRANSFER":"TRANS_COIN"));//类型  1 转账  2转币
					ob.put("cms",l.getSettleAmount().stripTrailingZeros().toPlainString() + " " + l.getSettleSymbol());//返利币种
					return ob;
				}, Collectors.toList())
		);

		TableResultResponse rspPage =  new TableResultResponse<>();
		rspPage.getData().setRows(rspList);
		rspPage.getData().setTotal(pageResult.getData().getTotal());
		rspPage.setRel(pageResult.isRel());
		rspPage.setStatus(pageResult.getStatus());
		rspPage.setMessage(pageResult.getMessage());
		return rspPage;
	}




}

