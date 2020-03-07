package com.udax.front.controller.fund;

import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.fund.FundManageInfo;
import com.github.wxiaoqi.security.common.entity.fund.FundProductInfo;
import com.github.wxiaoqi.security.common.entity.fund.FundProductProfiltInfo;
import com.github.wxiaoqi.security.common.entity.fund.FundStrategy;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.enums.ValidType;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.LocalDateUtil;
import com.github.wxiaoqi.security.common.util.Query;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.udax.front.biz.FrontUserBiz;
import com.udax.front.biz.fund.FundManageInfoBiz;
import com.udax.front.biz.fund.FundProductBiz;
import com.udax.front.biz.fund.FundProductProfiltInfoBiz;
import com.udax.front.biz.fund.FundPurchaseInfoBiz;
import com.udax.front.controller.BaseFrontController;
import com.udax.front.service.ServiceUtil;
import com.udax.front.util.BizControllerUtil;
import com.udax.front.util.CacheBizUtil;
import com.udax.front.vo.reqvo.PageInfo;
import com.udax.front.vo.reqvo.fund.BuyFundModel;
import com.udax.front.vo.reqvo.fund.FundDetailVo;
import com.udax.front.vo.rspvo.fund.FundProductInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author zhoucf
 */
@RestController
@RequestMapping("/wallet/fund/")
@Api(value = "基金交易相关接口", description = "基金交易相关接口")
public class FundController extends BaseFrontController<FundProductBiz,FundProductInfo> {



	@Autowired
	private FundManageInfoBiz fundManageInfoBiz;

	@PostMapping("fundlist")
	@ApiOperation(value = "基金首页/分頁展示基金", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object index(@RequestBody  PageInfo model) throws  Exception{

		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("enable", EnableType.ENABLE.value());// 可用的
		queryParams.put("exchangeId", BaseContextHandler.getAppExId()); //交易所Id
		queryParams.put("orderBy","publish_time desc"); //按发布时间倒序

		TableResultResponse pageResult = baseBiz.selectUnionPage(queryParams,new PageInfo(model.getLimit(),model.getPage()));

		List<FundProductInfoVo> resultList = new ArrayList<>(pageResult.getData().getRows().size());
		//复制信息给vo
		pageResult.getData().getRows().stream().forEach((p) ->{
			if(p != null){
				FundProductInfo productInfo = (FundProductInfo) p;
				FundProductInfoVo rspVo = new FundProductInfoVo();
				BeanUtils.copyProperties(productInfo,rspVo);
				if(productInfo.getManageInfo() != null){
					FundManageInfo infoResult = productInfo.getManageInfo();
					BeanUtils.copyProperties(infoResult,rspVo);
				}
				if(productInfo.getStrategy() != null){
					FundStrategy strategy = productInfo.getStrategy();
					BeanUtils.copyProperties(strategy,rspVo);
				}
				resultList.add(rspVo);
			}
		});
		pageResult = new TableResultResponse(pageResult.getData().getTotal(),resultList);
		dealFundProductInfoPage(pageResult);
		

		return pageResult;

	}


	@SuppressWarnings("unchecked")
	@PostMapping("funddetail")
	@ApiOperation(value = "基金明细", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object funddetail (@RequestBody @Valid FundDetailVo model) {

		Map<String, Object> queryParams = InstanceUtil.newHashMap("fundId",model.getId());
		FundProductInfo productInfo = baseBiz.selectUnion(queryParams);
		if( productInfo == null){
			return new ObjectRestResponse<>().status(ResponseCode.FUND_NOT_EXIST);
		}
		//复制信息给Vo
		FundProductInfoVo responseVo = new FundProductInfoVo();
		//设置管理人信息

		BeanUtils.copyProperties(productInfo,responseVo);
		dealFundProductInfo(responseVo);
		//设置管理人信息
		if(productInfo.getManageInfo() != null){
			FundManageInfo infoResult = productInfo.getManageInfo();
			BeanUtils.copyProperties(infoResult,responseVo);
		}
		if(productInfo.getStrategy() != null){
			FundStrategy strategy = productInfo.getStrategy();
			BeanUtils.copyProperties(strategy,responseVo);
		}

		//查询产品收益表当前净值
//		FundProductProfiltInfo productParam = new FundProductProfiltInfo();
//		productParam.setFundId(model.getId());
//		productParam.setExchangeId(BaseContextHandler.getExId());
//		FundProductProfiltInfo productProfiltInfo = fundProductProfiltInfoBiz.selectOne(productParam);
//		if(productProfiltInfo != null){
//			responseVo.setCurrOneWorth(productProfiltInfo.getCurrOneWorth());
//		}

        return new ObjectRestResponse<>().data(responseVo);
	}


	private void dealFundProductInfoPage(TableResultResponse<FundProductInfoVo> page){
		if(page != null  && !StringUtil.listIsBlank(page.getData().getRows())){
			List<FundProductInfoVo> list = page.getData().getRows();
			list.forEach((l)->{
				dealFundProductInfo(l);
			});
		}
	}
	private void dealFundProductInfo(FundProductInfoVo l){
		if(l.getOverRange() != 1){
			//如果不允许超额认购
			//实际规模+最小认购数量 > =预计规模  设置比例为100
			if(l.getActualScale().add(l.getMinBuyNum()).compareTo(l.getExpectScale()) >=0){
				l.setRate(new BigDecimal(100));
			}
		}
		//募集比例
		if(l.getActualScale().add(l.getMinBuyNum()).compareTo(l.getExpectScale()) >= 0 && l.getActualScale().compareTo(l.getExpectScale()) <= 0){
			l.setRate(new BigDecimal(100));
		}else{
			l.setRate(l.getActualScale().multiply(new BigDecimal(100)).divide(l.getExpectScale(),2,BigDecimal.ROUND_UP));
		}

		///锁定时间
		LocalDateTime begin = LocalDateUtil.date2LocalDateTime(l.getCycleStarttime());
		LocalDateTime end = LocalDateUtil.date2LocalDateTime(l.getCycleEndtime());
		Duration duration = Duration.between(begin,end);
		l.setLockDate(duration.toDays());//天数;

   }



}
