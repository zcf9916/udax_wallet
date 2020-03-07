package com.udax.front.tencent;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.Deflater;

@Component
public class Configure implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    /**  * 设置spring上下文  *  * @param applicationContext spring上下文  * @throws BeansException  */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    public static ApplicationContext getApplicationContext() {

        return applicationContext;
    }

    public enum Url{
        //用户头像/
        SET_USER_INFO("https://console.tim.qq.com/v4/profile/portrait_set?sdkappid=%s&identifier=%s&usersig=%s&random=%s&contenttype=json"),
        //设置群组信息
        SET_GROUP_INFO("https://console.tim.qq.com/v4/group_open_http_svc/modify_group_base_info?sdkappid=%s&identifier=%s&usersig=%s&random=%s&contenttype=json"),
        SEND_SINGLE_MSG("https://console.tim.qq.com/v4/openim/sendmsg?sdkappid=%s&identifier=%s&usersig=%s&random=%s&contenttype=json"),
        SEND_MULTI_MSG("https://console.tim.qq.com/v4/group_open_http_svc/send_group_msg?sdkappid=%s&identifier=%s&usersig=%s&random=%s&contenttype=json"),
        CREATE_GROUP("https://console.tim.qq.com/v4/group_open_http_svc/create_group?sdkappid=%s&identifier=%s&usersig=%s&random=%s&contenttype=json"),
        //
        ACCOUNT_IMPORT("https://console.tim.qq.com/v4/im_open_login_svc/account_import?sdkappid=%s&identifier=%s&usersig=%s&random=%s&contenttype=json"),
        //查询用户在群组中的身份
        GROUP_OPEN_HTTP_SVC("https://console.tim.qq.com/v4/group_open_http_svc/get_role_in_group?sdkappid=%s&identifier=%s&usersig=%s&random=%s&contenttype=json");
        private String url;

        Url(String url) {
            this.url = url;
        }
        public String toString(){
            return this.url;
        }
    }


    public static final String identifier = "liuzz";


    /**
     * 腾讯云 SDKAppId，需要替换为您自己账号下的 SDKAppId。
     *
     * 进入腾讯云云通信[控制台](https://console.cloud.tencent.com/avc ) 创建应用，即可看到 SDKAppId，
     * 它是腾讯云用于区分客户的唯一标识。
     */
    public static final int SDKAPPID = 1400241688;


    /**
     * 签名过期时间，建议不要设置的过短
     * <p>
     * 时间单位：秒
     * 默认时间：7 x 24 x 60 x 60 = 604800 = 7 天
     */
    private static final int EXPIRETIME = 604800;


    /**
     * 计算签名用的加密密钥，获取步骤如下：
     *
     * step1. 进入腾讯云云通信[控制台](https://console.cloud.tencent.com/avc ) ，如果还没有应用就创建一个，
     * step2. 单击“应用配置”进入基础配置页面，并进一步找到“帐号体系集成”部分。
     * step3. 点击“查看密钥”按钮，就可以看到计算 UserSig 使用的加密的密钥了，请将其拷贝并复制到如下的变量中
     *
     * 注意：该方案仅适用于调试Demo，正式上线前请将 UserSig 计算代码和密钥迁移到您的后台服务器上，以避免加密密钥泄露导致的流量盗用。
     * 文档：https://cloud.tencent.com/document/product/269/32688#Server
     */
    private static final String SECRETKEY = "3c02385a1d22dd91ea5ccfd52ad9a2e1aaf4ef6c5ffb0e224a52152d609fcbc5";

    /**
     * 计算 UserSig 签名
     *
     * 函数内部使用 HMAC-SHA256 非对称加密算法，对 SDKAPPID、userId 和 EXPIRETIME 进行加密。
     *
     * @note: 请不要将如下代码发布到您的线上正式版本的 App 中，原因如下：
     *
     * 本文件中的代码虽然能够正确计算出 UserSig，但仅适合快速调通 SDK 的基本功能，不适合线上产品，
     * 这是因为客户端代码中的 SECRETKEY 很容易被反编译逆向破解，尤其是 Web 端的代码被破解的难度几乎为零。
     * 一旦您的密钥泄露，攻击者就可以计算出正确的 UserSig 来盗用您的腾讯云流量。
     *
     * 正确的做法是将 UserSig 的计算代码和加密密钥放在您的业务服务器上，然后由 App 按需向您的服务器获取实时算出的 UserSig。
     * 由于破解服务器的成本要高于破解客户端 App，所以服务器计算的方案能够更好地保护您的加密密钥。
     *
     * 文档：https://cloud.tencent.com/document/product/269/32688#Server
     */
    public static String genTestUserSig(String userId) {
        return GenTLSSignature(SDKAPPID, userId, EXPIRETIME, null, SECRETKEY);
    }

    /**
     * 生成 tls 票据
     *
     * @param sdkappid    应用的 appid
     * @param userId      用户 id
     * @param expire      有效期，单位是秒
     * @param userbuf     默认填写null
     * @param priKeyContent 生成 tls 票据使用的私钥内容
     * @return 如果出错，会返回为空，或者有异常打印，成功返回有效的票据
     */
    private static String GenTLSSignature(long sdkappid, String userId, long expire, byte[] userbuf, String priKeyContent) {
        if (StringUtils.isEmpty(priKeyContent)) {
            return "";
        }
        long currTime = System.currentTimeMillis() / 1000;
        JSONObject sigDoc = new JSONObject();
        try {
            sigDoc.put("TLS.ver", "2.0");
            sigDoc.put("TLS.identifier", userId);
            sigDoc.put("TLS.sdkappid", sdkappid);
            sigDoc.put("TLS.expire", expire);
            sigDoc.put("TLS.time", currTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String base64UserBuf = null;
        if (null != userbuf) {
            Base64.getEncoder().encodeToString(userbuf);
           // base64UserBuf = Base64.encodeToString(userbuf, Base64.NO_WRAP);
            base64UserBuf =  Base64.getEncoder().encodeToString(userbuf);
            try {
                sigDoc.put("TLS.userbuf", base64UserBuf);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String sig = hmacsha256(sdkappid, userId, currTime, expire, priKeyContent, base64UserBuf);
        if (sig.length() == 0) {
            return "";
        }
        try {
            sigDoc.put("TLS.sig", sig);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Deflater compressor = new Deflater();
        compressor.setInput(sigDoc.toString().getBytes(Charset.forName("UTF-8")));
        compressor.finish();
        byte[] compressedBytes = new byte[2048];
        int compressedBytesLength = compressor.deflate(compressedBytes);
        compressor.end();
        return new String(base64EncodeUrl(Arrays.copyOfRange(compressedBytes, 0, compressedBytesLength)));
    }


    private static String hmacsha256(long sdkappid, String userId, long currTime, long expire, String priKeyContent, String base64Userbuf) {
        String contentToBeSigned = "TLS.identifier:" + userId + "\n"
                + "TLS.sdkappid:" + sdkappid + "\n"
                + "TLS.time:" + currTime + "\n"
                + "TLS.expire:" + expire + "\n";
        if (null != base64Userbuf) {
            contentToBeSigned += "TLS.userbuf:" + base64Userbuf + "\n";
        }
        try {
            byte[] byteKey = priKeyContent.getBytes("UTF-8");
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, "HmacSHA256");
            hmac.init(keySpec);
            byte[] byteSig = hmac.doFinal(contentToBeSigned.getBytes("UTF-8"));
            return new String(Base64.getEncoder().encodeToString(byteSig));
            //return new String(Base64.encode(byteSig, Base64.NO_WRAP));
        } catch (UnsupportedEncodingException e) {
            return "";
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (InvalidKeyException e) {
            return "";
        }
    }

    private static byte[] base64EncodeUrl(byte[] input) {
        byte[] base64 = new String(Base64.getEncoder().encodeToString(input)).getBytes();
        for (int i = 0; i < base64.length; ++i)
            switch (base64[i]) {
                case '+':
                    base64[i] = '*';
                    break;
                case '/':
                    base64[i] = '-';
                    break;
                case '=':
                    base64[i] = '_';
                    break;
                default:
                    break;
            }
        return base64;
    }

//    public static String sendSingleMsgUrl(){
//        String url = String.format(sendSingleMsg,
//                sdkappid,
//                identifier,
//                usersig,
//                999999);
//        return url;
//    }
//
//    public static String getSetGroupInfoUrl(){
//        String url = String.format(setGroupInfo,
//                sdkappid,
//                identifier,
//                usersig,
//                999999);
//        return url;
//    }

    public static String getFormatUrl(Configure.Url type){
        String userSig = Configure.genTestUserSig(identifier);
        String url = String.format(type.toString(),
                SDKAPPID,
                identifier,
                userSig,
                999999);
        return url;

    }
}
