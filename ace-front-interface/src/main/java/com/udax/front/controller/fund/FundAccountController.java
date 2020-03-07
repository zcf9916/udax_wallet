package com.udax.front.controller.fund;


import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.fund.FundAccountAssert;
import com.github.wxiaoqi.security.common.entity.fund.FundAccountAssertLog;
import com.github.wxiaoqi.security.common.entity.fund.FundProductInfo;
import com.github.wxiaoqi.security.common.entity.fund.FundPurchaseInfo;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.enums.ValidType;
import com.github.wxiaoqi.security.common.enums.fund.FundChangeType;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.Query;
import com.udax.front.annotation.UserOperationAuthority;
import com.udax.front.biz.CacheBiz;
import com.udax.front.biz.FrontUserBiz;
import com.udax.front.biz.fund.FundAccountAssertBiz;
import com.udax.front.biz.fund.FundAccountAssertLogBiz;
import com.udax.front.biz.fund.FundPurchaseInfoBiz;
import com.udax.front.controller.BaseFrontController;
import com.udax.front.service.ServiceUtil;
import com.udax.front.util.BizControllerUtil;
import com.udax.front.util.CacheBizUtil;
import com.udax.front.vo.reqvo.DcCodeModel;
import com.udax.front.vo.reqvo.fund.BuyFundModel;
import com.udax.front.vo.reqvo.fund.FundAccountLogModel;
import com.udax.front.vo.reqvo.fund.FundAccountTradeModel;
import com.udax.front.vo.reqvo.fund.UserFundListModel;
import com.udax.front.vo.rspvo.fund.FundAccountAssertLogVo;
import com.udax.front.vo.rspvo.fund.FundAccountAssertVo;
import com.udax.front.vo.rspvo.fund.FundPurchaseInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户登录
 * 
 * @author zhoucf
 */
@RestController
@RequestMapping("/wallet/fundaccount/")
@Api(value = "基金账户相关接口", description = "基金账户相关接口")
public class FundAccountController extends BaseFrontController<FundAccountAssertBiz,FundProductInfo> {

    @Autowired
    private CacheBiz cacheBiz;

    @Autowired
    private FundPurchaseInfoBiz fundPurchaseInfoBiz;

    @Autowired
    private FundAccountAssertLogBiz fundAccountAssertLogBiz;


    @Autowired
    private FrontUserBiz frontUserBiz;


    //@SuppressWarnings("unchecked")
	@PostMapping("/assert")
	@ApiOperation(value = "查询用户基金账户币种可用数量", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object fundassertByDccode(@RequestBody @Valid DcCodeModel model) {
	    String dcCode = model.getDcCode();
        //验证代币是否合法
        if(StringUtils.isNotBlank(dcCode)){
            boolean flag = ServiceUtil.validDcCode(dcCode,cacheBiz);
            if(!flag) return new ObjectRestResponse<>().status(ResponseCode.DC_INVALID);
        }
        //FrontUser user = CacheBizUtil.getFrontUserCache(BaseContextHandler.getUserID(), frontUserBiz);//获取最新用户常用信息缓存
        //基金账户代币对应的可用数量
        FundAccountAssert paramAccount = new FundAccountAssert();
        paramAccount.setUserId(BaseContextHandler.getUserID());
        paramAccount.setDcCode(dcCode);
        FundAccountAssert account = baseBiz.selectOne(paramAccount);
        String avaibleAmount = BigDecimal.ZERO.toPlainString();
        if( account != null && account.getAvailableAmount() != null){
            avaibleAmount = account.getAvailableAmount().toPlainString();
        }
        return new ObjectRestResponse<>().data(avaibleAmount);

	}

	@SuppressWarnings("unchecked")
	@PostMapping("assertList")
	@ApiOperation(value = "展示用户基金账户所有币种信息", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object fundAssert() throws Exception {

            //查询用户的所有资产信息
            FundAccountAssert paramAccount = new FundAccountAssert();
            paramAccount.setUserId(BaseContextHandler.getUserID());
            List<FundAccountAssert> assertList = baseBiz.selectList(paramAccount);
            List<FundAccountAssertVo> rspVoList = BizControllerUtil.transferEntityToListVo(FundAccountAssertVo.class,assertList);
            return new ObjectRestResponse<>().data(rspVoList);
//            //查询资产净值
//            //查询不是结算的购买记录
//            Map<String,Object> purchaseInfoParam = InstanceUtil.newHashMap("lessStatus",FundPurchaseStatus.SETTLED.value());
//            purchaseInfoParam.put("userId",user.getId());
//            //购买记录查询
//            parameter = executeMethod(getService(),"queryDetailList",purchaseInfoParam);
//            List<FundPurchaseInfo> purchaseList = (List<FundPurchaseInfo>)parameter.getResultList();
//
//            //查询申购跟单产品的数量(非已清盘的,已清盘的数据已经结算到账户了)以及浮动盈亏
//            //key是代币,value是浮动盈亏
//            Map<String,String> profitMap = InstanceUtil.newHashMap();
//            if(!StringUtil.listIsBlank(purchaseList)){
//                purchaseList.forEach((p)->{
//                    FundProductProfiltInfo info = p.getFundProductProfiltInfo();
//                    if(info == null || info.getCurrOneWorth() == null) {
//                        return;
//                    }
//                    //当前净值不为空
//                    //1-當前净值 )*认购额度  = 24小时盈亏
//                   if(info.getCurrOneWorth().compareTo(new BigDecimal(1)) != 0){
//                        //保存非0的浮动盈亏
//                        BigDecimal profilt = info.getCurrOneWorth().subtract(new BigDecimal(1)).multiply(p.getOrderVolume()).setScale(8,BigDecimal.ROUND_DOWN );
//                       if(profitMap.get(p.getDcCode()) == null){
//                           profitMap.put(p.getDcCode(),profilt.toPlainString());
//                       }else{
//                           BigDecimal oldProfilt = new BigDecimal(profitMap.get(p.getDcCode()));
//                           profitMap.put(p.getDcCode(),oldProfilt.add(profilt).setScale(8,BigDecimal.ROUND_DOWN).toPlainString());
//                       }
//                    }
//
//                });
//             }
//                //获取资产净值列表
//            getWorthList(assertList,purchaseList,profitMap);
//            //Set<String>  worthList = getWorthList(list,profitMap);
////				Set<String>  worthList = getWorthList(list,purchaseList,profitMap);
////				Map<String,Object> returnResult = InstanceUtil.newHashMap("assert",assertList);
////				returnResult.put("profitMap",profitMap.size() == 0 ? null:profitMap
////				);
////				returnResult.put("worthList",worthList);
//
//            Map<String, Object> resultMap = InstanceUtil.newHashMap();
//            resultMap.put("sellteMarket", BizControllerUtil.getSellteMarket(sysProvider,request));//基准市场
//            resultMap.put("rateList", BizControllerUtil.getRateList());//汇率
//            resultMap.put("lastPrices", BizControllerUtil.getLastPrices());//最新价格
//            resultMap.put("assert", assertList);
//            resultMap.put("currencyRate", BizControllerUtil.getCurrencyRate());//当前显示的法币代码



	}

//	/**
//	 * 资产净值总量等于 账户可用数量+购买数量 + 浮动盈亏
//	 * @param assertList
//	 * @param profiltMap
//	 * @return
//	 */
//	private Set<String> getWorthList(List<FundAccountAssertVo> assertList , List<FundPurchaseInfo> purchaseList, Map<String,String> profiltMap) {
//		Set<String> worthInfo = new HashSet<String>();
////		});
//		assertList.forEach((a)->{
//			//设置资产净值
//			if(StringUtils.isBlank(a.getAssertValue())){
//				a.setAssertValue("0");
//			}
//		  //a.setTotolAmount(a.getTotolAmount().add(p.getOrderVolume()));
//			purchaseList.forEach((p) ->{
//				if(p.getDcCode().equals(a.getDcCode())){
//                    //账户数量+购买的基金的数量(未结算的)
//
//					a.setAssertValue(new BigDecimal(a.getAssertValue()).add(p.getOrderVolume()).setScale(8,BigDecimal.ROUND_DOWN).toPlainString());
//					//a.setAvailableAmount(a.getAvailableAmount().add(p.getOrderVolume()).setScale(8,BigDecimal.ROUND_DOWN));
//				}
//			});
//			//可用数量
//			BigDecimal availble = new BigDecimal(a.getAvailableAmount());
//			//浮动盈亏
//			BigDecimal profilt = profiltMap.get(a.getDcCode()) == null ? new BigDecimal(0) : new BigDecimal(profiltMap.get(a.getDcCode()));
////			if( availble.compareTo(BigDecimal.valueOf(0)) == 0 && profilt.compareTo(BigDecimal.valueOf(0)) == 0){
////				a.setAssertValue(BigDecimal.valueOf(0).toPlainString());
////			}else{
//				//资产净值等于 可用+购买+浮动盈亏
//			a.setAssertValue(new BigDecimal(a.getAssertValue()).add(availble).add(profilt).setScale(8,BigDecimal.ROUND_DOWN).toPlainString());
////			}
//			//资产净值
//			a.setProfit(profilt.toPlainString());
//////			//净值不为空  加上净值
////           if(a.getAvailableAmount().compareTo(new BigDecimal(0)) > 0){
////				worthInfo.add(a.getAvailableAmount().toPlainString() + " " + a.getDcCode());
////			}
//		});
//		return worthInfo;
//	}
//
	@SuppressWarnings("unchecked")
	@PostMapping("fundlist")
	@ApiOperation(value = "分页展示用户申购的基金", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object index(@RequestBody @Valid UserFundListModel model) throws Exception{
        Map<String,Object> param = InstanceUtil.newHashMap();
        Long userId = BaseContextHandler.getUserID();
        param.put("userId", userId);
        param.put("exchangeId", BaseContextHandler.getAppExId());
        param.put("page", model.getPage());
        param.put("limit", model.getLimit());
        param.put("fundName:like",model.getFundName());//模糊查詢
        param.put("orderTime:>=",model.getBeginDate());
        param.put("orderTime:<=",model.getEndDate());
        Query query = new Query(param);
        query.setOrderByInfo("order_time desc");
        TableResultResponse<FundPurchaseInfo> pageResult = pageQueryTransToVo(query, FundPurchaseInfo.class,
                FundPurchaseInfoVo.class, fundPurchaseInfoBiz);
        return pageResult;
	}
//	private void dealFundPurchaseInfoVoPage(Page<FundPurchaseInfoVo> responseVoPage,Page<FundPurchaseInfo> page){
//		if(responseVoPage != null  && !StringUtil.listIsBlank(responseVoPage.getRecords())){
//			List<FundPurchaseInfoVo> responseVolist = responseVoPage.getRecords();
//            List<FundPurchaseInfo> list = page.getRecords();
//            responseVolist.forEach((l)->{
//				BigDecimal bigDecimal = l.getOrderChrge().multiply(new BigDecimal(100)).divide(l.getOrderChrge().add(l.getOrderVolume()),2,BigDecimal.ROUND_DOWN);
//			    l.setRate(bigDecimal.toPlainString());//(费率)
////                if(l.getYield() != null){
////                    l.setActualProfilt(l.getOrderVolume().multiply(l.getYield()).setScale(8,BigDecimal.ROUND_DOWN).toPlainString());//实际盈亏
////                }
//                list.forEach((p)->{
//                    if(p.getFundId().longValue() ==  l.getFundId().longValue()){
//                        FundProductProfiltInfo info = p.getFundProductProfiltInfo();
//                        if(info != null && info.getCurrOneWorth() != null){
//                            //当前净值不为空
//                            //1-當前净值 )*认购额度  = 24小时盈亏
//                            l.setOneDayProfilt(info.getCurrOneWorth().subtract(new BigDecimal(1)).multiply(l.getOrderVolume()).setScale(8,BigDecimal.ROUND_DOWN ).toPlainString());//24小时盈亏
//                        }
//                     }
//                });
//			});
//		}
//	}

    @SuppressWarnings("unchecked")
	@PostMapping("fundLoglist")
	@ApiOperation(value = "分页展示用户基金账户的明细", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object index(@RequestBody @Valid FundAccountLogModel model) throws Exception {

        Map<String,Object> param = InstanceUtil.newHashMap();
        param.put("userId", BaseContextHandler.getUserID());
        param.put("page", model.getPage());
        param.put("limit", model.getLimit());
        param.put("updateTime:>=",model.getBeginDate());
        param.put("updateTime:<=",model.getEndDate());
        Query query = new Query(param);
        query.setOrderByInfo("update_time desc");
        TableResultResponse<FundAccountAssertLog> pageResult = pageQueryTransToVo(query, FundAccountAssertLog.class,
                FundAccountAssertLogVo.class, fundAccountAssertLogBiz);
        return pageResult;

	}


    @UserOperationAuthority("FUND_TRADE") //判断当前用户跟单权限是否被禁用
    @PostMapping("purchase")
    @ApiOperation(value = "申购基金", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object buyfund(@RequestBody @Valid BuyFundModel model) throws Exception {

//		//校验当前用户跟单交易功能是否被禁用
//		boolean flag = ServiceUtil.checkTradeFunctionIsFreeze(FreezeFunctionType.FREEZE_FUND_TRADE.value(),user.getId(), sysProvider);
//		if(!flag){
//			return setErrorModelMap(modelMap, HttpCode.BAD_REQUEST, getMessage("FUND_TRADE_ISFREEZED"));
//		}

        FrontUser user = CacheBizUtil.getFrontUserCache(BaseContextHandler.getUserID(), frontUserBiz);//获取最新常用缓存
        //判断是否实名认证
        if (!user.getUserInfo().getIsValid().equals(ValidType.AUTH.value())) {
            return new ObjectRestResponse<>().status(ResponseCode.UNAUTH);
        }
        //model转换成查询map
        boolean flag = ServiceUtil.judgePayPassword(model.getPayPassword(),user.getId(),frontUserBiz);
        if(!flag){
            return new ObjectRestResponse<>().status(ResponseCode.TRADE_PWD_ERROR);
        }
        //申购基金
        return  fundPurchaseInfoBiz.generateFund(model.getFundId(),model.getOrderVolume());
    }

//
//
//
	@SuppressWarnings("unchecked")
	@PostMapping("transfer")
	@ApiOperation(value = "處理币币账户于基金账户的转入转出", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object fundAccountTrade(@RequestBody @Valid FundAccountTradeModel model) throws Exception{
	    boolean flag = ServiceUtil.judgePayPassword(model.getPayPassword(),BaseContextHandler.getUserID(),frontUserBiz);
        if (!flag) {
            return new ObjectRestResponse().status(ResponseCode.TRADE_PWD_ERROR);
        }
        //币币转基金
        if(model.getTradeType() == FundChangeType.DC_IN.value()){
             baseBiz.transInFundAccount(model.getDcCode(),model.getAmount());
        }else if(model.getTradeType() == FundChangeType.DC_OUT.value()){
            //基金转币币
             baseBiz.transToBBAccount(model.getDcCode(),model.getAmount());
        }else {
            return new ObjectRestResponse().status(ResponseCode.FUND_TRADETYPE_ERROR);
        }
        return new ObjectRestResponse();
	}
}
