package com.udax.front.bizmodel;

import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.enums.SendMsgType;
import com.github.wxiaoqi.security.common.enums.ValidCodeType;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.support.Assert;
import com.github.wxiaoqi.security.common.util.SendUtil;
import com.udax.front.biz.FrontUserBiz;
import com.udax.front.service.ServiceUtil;
import com.udax.front.util.CacheBizUtil;
import com.udax.front.vo.reqvo.SmsModel;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 手机注册相关业务逻辑类
 * @ClassName: PhoneRegister
 * @Desc: TODO
 * @author: bermaker
 * @date: 2018年5月25日 下午1:34:14
 * @version 1.0
 */
@Service
public class PhoneSmsBiz extends SendSmsBiz {

	public PhoneSmsBiz() {
		super();
	}

	public ObjectRestResponse sendValidCode(SmsModel model, FrontUserBiz biz,Long exchId) {
		//validParam(model,request);
       //validCountryCode(model,provider);//验证点有些是从数据库查询国家
		Assert.mobile(model.getUserName());
		ObjectRestResponse objectRestResponse = validSmsType(model,biz);
		if(!objectRestResponse.isRel()){
			return objectRestResponse;
		}
		String mobile = model.getUserName();
		//手机区号
		String locationCode = model.getLocationCode();
		//通过手机号查询用户
		FrontUser user = ServiceUtil.selectUnionUserInfoByUserName(model.getUserName(),biz);
		//如果入口是注册页面
		if(model.getSendMsgType().equals(SendMsgType.USER_REG.value())){
			//如果是注册,判断用户是否存在
			if (user != null) {
				return new ObjectRestResponse().status(ResponseCode.USER_EXIST);
			}
		} else if(model.getSendMsgType().equals(SendMsgType.CHANGE_INFO.value())) {
			if (user != null && StringUtils.isNotBlank(user.getUserInfo().getLocationCode())) {
				locationCode = user.getUserInfo().getLocationCode();
			}
			//修改手机必须带  新手机的区号
			if(StringUtils.isBlank(locationCode)){
				objectRestResponse.status(ResponseCode.COUNTRYCODE_VALID);
				return objectRestResponse;
			}
		} else {
			//其他情况,用户必须存在,并且手机号  手机区号必须存在
			if (user == null || StringUtils.isBlank(user.getUserInfo().getLocationCode())) {
				return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
			}
			locationCode = user.getUserInfo().getLocationCode();
		}

		//发送
		SendUtil.sendVerifyCodeBySms(model.getSendMsgType(),locationCode,mobile,exchId);
		return new ObjectRestResponse();
	}



//	/** 验证国籍和区号 */
//	private  void validCountryCode(SmsModel model , BaseProvider provider) {
//		String countryCode = String.valueOf(model.getCountryCode());//国籍编号对应的区号
//		Assert.isNotBlank(countryCode,"COUNTRYCODE");
//		List<FrontCountry> countryList = (List<FrontCountry>) CacheUtil.getCache()
//				.get(Constants.CacheServiceType.FRONTCOUNTRY + ":list");
//		//缓存没有去db查
//		if(countryList == null || countryList.size() < 1) {
//			Parameter parameter = new Parameter("frontCountryService", "queryList").setParam(new HashMap<String,Object>());
//			countryList = (List<FrontCountry>) provider.execute(parameter).getResult();
//		}
//		//验证区号是否匹配
//		for(FrontCountry country : countryList) {
//			if(country.getCountryCode().equals(countryCode)) {
//				return ;
//			}
//		}
//		throw new IllegalArgumentException(getMessage("NO_MATCH_COUNTRYCODE")+getMessage("NO_MATCH_COUNTRY"));
//
//	}

	
}
