package com.udax.front.bizmodel;


import com.github.wxiaoqi.security.common.entity.common.SendMsg;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.enums.ValidCodeType;
import com.github.wxiaoqi.security.common.enums.VerificationType;
import com.github.wxiaoqi.security.common.support.Assert;
import com.udax.front.util.CacheBizUtil;

public class CommonBiz  {


    /**
     * 验证码校验公共方法
     *
     * @param sendMsgType
     *            发送验证码类型,在哪种功能下发送的验证码,可参考枚举类SendMsgType
     * @param mobileCode
     *            页面传过来校验的手机验证码。
     * @param emailCode
     *            页面传过来校验的邮箱验证码。
     * @return
     */
    public static void commonVerifyMethod(FrontUser user, String sendMsgType, String mobileCode, String emailCode) {

        if (VerificationType.OPEN.value() == user.getUserInfo().getIsValidPhone()) {// 如果开启了手机验证
            SendMsg mobileMsg = CacheBizUtil.getCacheSmsMsg(user.getMobile());
            Assert.notNull(mobileMsg, "MSGINFO");
            // 验证发送类型
            Assert.equals(mobileMsg.getMsgType().toString(), ValidCodeType.PHONE_CODE.value().toString(),"SYS_VERIFICATION_CODE");
            // 验证短信类型
            Assert.equals(mobileMsg.getBizType(), sendMsgType, "SYS_VERIFICATION_CODE");
            // 验证手机验证码短信是否相等
            Assert.equals(mobileCode, mobileMsg.getSmsCode(), "SYS_VERIFICATION_CODE");
        }

        if (VerificationType.OPEN.value() == user.getUserInfo().getIsValidEmail()) {// 如果开启了邮箱验证
            SendMsg emailMsg = CacheBizUtil.getCacheSmsMsg(user.getEmail());
            Assert.notNull(emailMsg, "MSGINFO");
            // 验证发送类型
            Assert.equals(emailMsg.getMsgType().toString(), ValidCodeType.EMAIL_CODE.value().toString(),"SYS_VERIFICATION_CODE");
            // 验证验证码类型
            Assert.equals(emailMsg.getBizType(), sendMsgType,"SYS_VERIFICATION_CODE");
            Assert.equals(emailCode, emailMsg.getSmsCode(), "SYS_VERIFICATION_CODE");
        }
    }

    public static void clearVerifyMethod(FrontUser user) {

        if (VerificationType.OPEN.value() == user.getUserInfo().getIsValidPhone()) {// 如果开启了手机验证
           CacheBizUtil.clearSmsMsg(user.getMobile());

        }

        if (VerificationType.OPEN.value() == user.getUserInfo().getIsValidEmail()) {// 如果开启了邮箱验证
           CacheBizUtil.clearSmsMsg(user.getEmail());

        }
    }


}
