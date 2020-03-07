package com.udax.front.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.github.wxiaoqi.security.common.entity.admin.CfgSymbolDescription;
import com.github.wxiaoqi.security.common.enums.ClientType;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.model.CalType;
import com.github.wxiaoqi.security.common.vo.ValuationModeVo;
import com.udax.front.biz.CacheBiz;
import com.udax.front.service.ServiceUtil;
import com.udax.front.util.CacheBizUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.FrontHelpContent;
import com.github.wxiaoqi.security.common.entity.admin.FrontHelpType;
import com.github.wxiaoqi.security.common.entity.admin.FrontNotice;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.Query;
import com.udax.front.biz.FrontNoticeBiz;
import com.udax.front.biz.FrontUserBiz;
import com.udax.front.util.BizControllerUtil;
import com.udax.front.vo.reqvo.HelpContentModel;
import com.udax.front.vo.reqvo.PageInfo;
import com.udax.front.vo.rspvo.HelpContentVo;
import com.udax.front.vo.rspvo.HelpTypeVo;
import com.udax.front.vo.rspvo.NoticeVo;

@RestController
@RequestMapping("/wallet/other")
public class OtherController extends BaseFrontController<FrontUserBiz, FrontUser>{
	
	@Autowired
	private FrontNoticeBiz noticeBiz;


	@Autowired
	private CacheBiz cacheBiz;
	/**
	 * 获取帮助类型
	 * @return
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 */
	@PostMapping("helptypes")
	public ObjectRestResponse<List<HelpTypeVo>> helptypes() throws IllegalAccessException, InstantiationException{
		String language = BaseContextHandler.getLanguage();
        Long exchangeId = BaseContextHandler.getAppExId();
		List<FrontHelpType> typeList=(List<FrontHelpType>) CacheUtil.getCache().get(Constants.CacheServiceType.FrontHelpType+exchangeId+":"+language);
		Map<String,List<HelpTypeVo>> resultMap=new HashMap<String,List<HelpTypeVo>>();
		Integer clientType= ClientType.APP.value();
		if(StringUtil.listIsBlank(typeList)){
			return new ObjectRestResponse();
		}
		for (FrontHelpType helpTypeVo : typeList) {
			List<FrontHelpContent> contentList=(List<FrontHelpContent>) CacheUtil.getCache().get(Constants.CacheServiceType.FrontHelpContent+helpTypeVo.getId()+":"+language+":"+ clientType+Constants.CacheServiceType.LIST);
			List<HelpTypeVo> voContentList = BizControllerUtil.transferEntityToListVo(HelpTypeVo.class,contentList);
			resultMap.put(helpTypeVo.getTypeName(), voContentList);
		}
		return new ObjectRestResponse().data(resultMap);
	}
	
	/**
	 * 获取帮助内容
	 * @return
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 */
	@PostMapping("helpcontent")
	public ObjectRestResponse<HelpContentVo> helpcontent(@RequestBody HelpContentModel model) throws IllegalAccessException, InstantiationException{
		String language = BaseContextHandler.getLanguage();
		//客户端类型 --> app or pc ClientType枚举类
		Integer clientType= ClientType.APP.value();
		FrontHelpContent content=(FrontHelpContent) CacheUtil.getCache().get(Constants.CacheServiceType.FrontHelpContent+model.getId()+":"+language+":"+clientType);
		HelpContentVo vo=new HelpContentVo();
		BeanUtils.copyProperties(content, vo);
		return new ObjectRestResponse().data(vo);
	}
	
	/**
	 * 获取公告信息
	 * @return
	 * @throws Exception 
	 */
	@PostMapping("notices")
	public TableResultResponse<NoticeVo> notices(@RequestBody @Valid PageInfo pageInfo) throws Exception{
		String language = BaseContextHandler.getLanguage();
		Long exchangeId = BaseContextHandler.getAppExId();
		Map<String,Object> map = BizControllerUtil.modelToMap(pageInfo);
		map.put("exchangeId", exchangeId);
		map.put("clientType", ClientType.APP.value());
		map.put("languageType", language);
		map.put("orderByInfo"," crt_time desc");
		Query query = new Query(map);
    	TableResultResponse<NoticeVo> rsplist = pageQueryTransToVo(query,FrontNotice.class,NoticeVo.class,noticeBiz);
		// 转账
		return rsplist;
	}


	/**
	 * 获取转账描述信息
	 * @return
	 * @throws Exception
	 */
	@PostMapping("transferInfo")
	public ObjectRestResponse<Object> transferInfo(@RequestParam String symbol,@RequestParam(required = false) String protocolType) throws Exception{
		//验证代币

		boolean flag = ServiceUtil.validDcCode(symbol,cacheBiz);
		if(!flag) return new ObjectRestResponse<>().status(ResponseCode.DC_INVALID);


		String language =  BaseContextHandler.getLanguage();
		//todo
		CfgSymbolDescription symbolDescription =  CacheBizUtil.getCfgSymbolDescription(cacheBiz,symbol,BaseContextHandler.getAppExId(),language,protocolType);
		// 转账
		return new ObjectRestResponse<>().data(symbolDescription.getTransferDesp());
	}


	//获取计价类型
	@RequestMapping(value = "/calcType", method = RequestMethod.POST)
	@ResponseBody
	public ObjectRestResponse calcType() throws Exception {
		List<ValuationModeVo> list = CacheBizUtil.getValuationManner(cacheBiz,BaseContextHandler.getAppExId(),BaseContextHandler.getLanguage());
		List<CalType> rspList = BizControllerUtil.transferEntityToListVo(CalType.class,list);
		return new ObjectRestResponse().data(rspList);
	}
}