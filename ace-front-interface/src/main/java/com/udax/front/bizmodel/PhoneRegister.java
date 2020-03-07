package com.udax.front.bizmodel;

import com.github.wxiaoqi.security.common.entity.common.SendMsg;
import com.github.wxiaoqi.security.common.enums.SendMsgType;
import com.github.wxiaoqi.security.common.enums.ValidCodeType;
import com.github.wxiaoqi.security.common.support.Assert;
import com.github.wxiaoqi.security.common.util.SecurityUtil;
import com.github.wxiaoqi.security.common.util.WebUtil;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.udax.front.util.CacheBizUtil;
import com.udax.front.vo.FrontUserRegisterVo;
import com.udax.front.vo.reqvo.RegisterModel;
import org.apache.commons.lang3.RandomStringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 手机注册相关业务逻辑类
 * @ClassName: PhoneRegister
 * @Desc: TODO
 * @author: zhoucf
 * @date: 2018年5月25日 下午1:34:14
 * @version 1.0
 */
public class PhoneRegister extends AbstractRegister {

	RegisterModel registerModel;
	public PhoneRegister(RegisterModel registerModel) {
		this.registerModel = registerModel;
	}
	public PhoneRegister() {
		super();
	}
	/**
	 * 验证字段有效性
	 */
    @Override
	public void validateParam() {
		Assert.mobile(registerModel.getUsername());
		SendMsg msg = CacheBizUtil.getCacheSmsMsg(registerModel.getUsername());
        Assert.notNull(msg,"MSGINFO");
		//验证发送类型
		Assert.equals(msg.getBizType(), ValidCodeType.PHONE_CODE.value().toString(),"SYS_VERIFICATION_CODE");
        //验证短信类型
		Assert.equals(msg.getMsgType().toString(),SendMsgType.USER_REG.value(),"SYS_VERIFICATION_CODE");
		//验证短信是否相等
		Assert.equals(registerModel.getSmsCode(), msg.getSmsCode(),"SYS_VERIFICATION_CODE");

		Assert.isNotBlank(registerModel.getPassword(), "PASSWORD");

	}

	@Override
    public  FrontUserRegisterVo setParam(HttpServletRequest request) {
		FrontUserRegisterVo vo = new FrontUserRegisterVo();
		vo.setSalt(RandomStringUtils.randomAlphabetic(20));
		vo.setUserPwd(SecurityUtil.encryptPassword(registerModel.getPassword() + vo.getSalt()));
		vo.setUid(String.valueOf(IdGenerator.nextId()));
		vo.setLoginIp(WebUtil.getHost(request));
		vo.setBindDomain(request.getServerName());
		vo.setUserName(registerModel.getUsername());
		vo.setRecommondCode(registerModel.getVisitCode());
		vo.setCountryCode(registerModel.getCountryCode());
		return vo;
	}

}
