package com.udax.front.bizmodel;

import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.common.SendMsg;
import com.github.wxiaoqi.security.common.enums.EmailTemplateType;
import com.github.wxiaoqi.security.common.enums.SendMsgType;
import com.github.wxiaoqi.security.common.enums.ValidCodeType;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.support.Assert;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.SendUtil;
import com.github.wxiaoqi.security.common.util.VerifyCodeUtils;
import com.udax.front.biz.FrontUserBiz;
import com.udax.front.util.CacheBizUtil;
import com.udax.front.vo.reqvo.SmsModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 发送邮件验证码相关业务逻辑
 */
public class EmailSmsBiz extends SendSmsBiz{
	protected final Logger logger = LogManager.getLogger(this.getClass());

	public EmailSmsBiz() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public ObjectRestResponse sendValidCode(SmsModel model, FrontUserBiz userBiz,Long exchId) {

			Assert.email(model.getUserName());
			ObjectRestResponse objectRestResponse = new ObjectRestResponse();
			//validParam(model,request);//验证图形验证码
			objectRestResponse = validSmsType(model,userBiz);
			if(!objectRestResponse.isRel()){
				return objectRestResponse;
			}
			String emailTo = model.getUserName();
			Assert.email(emailTo);
		    String verifyCode = VerifyCodeUtils.generateValidCode();

			if((Boolean) CacheUtil.getCache().get(Constants.CommonType.IF_TEST_ENV)){
				verifyCode = "111111"; //测试环境验证码默认为111111
			}
		    logger.info("邮箱验证码为:" + verifyCode);
			//调用邮件公共方法
		    SendUtil.sendEmail(EmailTemplateType.USER_REG.value(),emailTo,exchId,null,verifyCode);
			//缓存邮箱验证码
			SendMsg sendMsg = new SendMsg();
			sendMsg.setMsgType(ValidCodeType.EMAIL_CODE.value());
			sendMsg.setBizType(SendMsgType.valueOfMsgType(model.getSendMsgType()).value());
			sendMsg.setPhone(emailTo);
			sendMsg.setContent(verifyCode);
            sendMsg.setSmsCode(verifyCode);
			CacheBizUtil.setCacheSmgMsg(emailTo,sendMsg);
			return new ObjectRestResponse();

	}
}
