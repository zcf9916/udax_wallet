package com.udax.front.bizmodel;

import com.alibaba.fastjson.JSON;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.common.SendMsg;
import com.github.wxiaoqi.security.common.enums.SendMsgType;
import com.github.wxiaoqi.security.common.enums.ValidCodeType;
import com.github.wxiaoqi.security.common.support.Assert;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.SecurityUtil;
import com.github.wxiaoqi.security.common.util.WebUtil;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.udax.front.vo.FrontUserRegisterVo;
import com.udax.front.vo.reqvo.RegisterModel;
import org.apache.commons.lang3.RandomStringUtils;

import javax.servlet.http.HttpServletRequest;


public class EmailRegister extends AbstractRegister{

	public EmailRegister() {
		super();
		// TODO Auto-generated constructor stub
	}
    private RegisterModel registerModel;
	public EmailRegister(RegisterModel registerModel) {
		this.registerModel = registerModel;
	}

	/**
	 * 验证字段有效性
	 */
	@Override
	public void validateParam() {

		Assert.email(registerModel.getUsername());
		String msgInfo = String.valueOf(CacheUtil.getCache().get(Constants.CommonType.SMSCODE
				+ registerModel.getUsername()));
		SendMsg msg = JSON.parseObject(msgInfo,SendMsg.class);
		Assert.notNull(msg,"MSGINFO");

		//验证发送类型
		Assert.equals(msg.getMsgType().toString(), ValidCodeType.EMAIL_CODE.value().toString(),"SYS_VERIFICATION_CODE");
		//验证验证码类型
		Assert.equals(msg.getBizType(), SendMsgType.USER_REG.value(),"SYS_VERIFICATION_CODE");
		//验证短信是否相等
		Assert.equals(registerModel.getSmsCode(), msg.getSmsCode(),"SYS_VERIFICATION_CODE");


		Assert.notNull(registerModel.getPassword(), "PASSWORD");


		//国家代码以及区号
//		validCountryCodeAndSet(provider);
		
	}

	//设置参数
	@Override
    public FrontUserRegisterVo setParam(HttpServletRequest request) {
		FrontUserRegisterVo vo = new FrontUserRegisterVo();
		vo.setSalt(RandomStringUtils.randomAlphabetic(20));
		vo.setUserPwd(SecurityUtil.encryptPassword(registerModel.getPassword() + vo.getSalt()));
		vo.setUid(String.valueOf(IdGenerator.nextId()));
		vo.setLoginIp(WebUtil.getHost(request));
		vo.setBindDomain(request.getServerName());
		vo.setUserName(registerModel.getUsername());
		vo.setRecommondCode(registerModel.getVisitCode());
        return vo;
	}


	public static void main(String[] args) {
		Assert.email("fasdf.asdf@qq.com");
	}

}
