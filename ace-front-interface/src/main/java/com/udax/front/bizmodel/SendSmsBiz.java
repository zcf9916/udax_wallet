package com.udax.front.bizmodel;


import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.enums.SendMsgType;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.udax.front.biz.FrontUserBiz;
import com.udax.front.service.ServiceUtil;
import com.udax.front.vo.reqvo.SmsModel;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.wxiaoqi.security.common.config.Resources.getMessage;


/**
 * 发送验证码业务逻辑
 * @ClassName:
 * @Desc: TODO
 * @author: zhoucf
 * @date: 2018年5月24日 下午6:09:15
 * @version 1.0
 */
public abstract class SendSmsBiz {



    public SendSmsBiz() {
	}

    //通过短信类型校验各种情况
    protected ObjectRestResponse validSmsType(SmsModel model, FrontUserBiz biz){
    	 //注册
         if(model.getSendMsgType().equals(SendMsgType.USER_REG.value())) {
         	 Map<String,Object> params = new HashMap<String,Object>();
         	 params.put("userName",model.getUserName());
			 FrontUser user = ServiceUtil.getUniFrontUserByName(model.getUserName(),biz);
			 if( user != null){
			 	return new ObjectRestResponse().status(ResponseCode.USER_EXIST);
			 }
		 }
//		 else if(model.getSendMsgType().equals(SendMsgType.USER_LOGIN.value())){
//			 //登陆短信,需要判断用户是否存在
//			 FrontUser user = ServiceUtil.getUniFrontUserByName(model.getUserName(),biz);
//			 if(user == null){
//				 return new ObjectRestResponse().status(ResponseCode.USER_EXIST);
//			 }
//		 }
		return new ObjectRestResponse();
	}

	//发送验证码
	public abstract ObjectRestResponse sendValidCode(SmsModel model, FrontUserBiz userBiz,Long exchId);
	
	/** 验证图形验证码参数*/
	protected void validParam(SmsModel model, FrontUserBiz biz) {
//        //通过用户名查询用户
//		FrontUser user = ServiceUtil.getUniFrontUserByName(model.getUserName(),biz);
//		//如果入口是注册页面或者重新绑定手机邮箱
//		if(model.getSendMsgType().equals(SendMsgType.USER_REG.value())
//				|| model.getSendMsgType().equals(SendMsgType.CHANGE_INFO.value())){
//			//如果是注册,判断用户是否存在
//			if (user != null) {
//				return new ObjectRestResponse().status(ResponseCode.USER_EXIST);
//			}
//			return new ObjectRestResponse().data("");
//		} else {
//			if (user == null) {
//				return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
//			}
//			return new ObjectRestResponse().data(user.getUserInfo().getLocationCode());
//		}
	}
}
