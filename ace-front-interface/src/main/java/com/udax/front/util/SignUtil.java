package com.udax.front.util;

import com.github.wxiaoqi.security.common.util.HashUtil;
import com.github.wxiaoqi.security.common.util.merchant.HmacSHA256Util;
import com.udax.front.vo.reqvo.merchant.MchPreOrderModel;
import com.udax.front.vo.reqvo.merchant.MchSymbolInfoModel;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;

public class SignUtil {

    /**
     * 商户接口签名
     * @param secretKey
     * @return
     */
    public static String sign(Object model,String secretKey) throws Exception{
        SortedMap<String,Object> map = BizControllerUtil.modelToMap(model);
        map.remove("sign");
        StringBuffer signBuffer = new StringBuffer();
        Set<Map.Entry<String, Object>> es = map.entrySet();
        Iterator<Map.Entry<String, Object>> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry<String,Object>) it.next();
            String k = (String) entry.getKey();
            Object v = (Object) entry.getValue();
            if(v != null){
                signBuffer.append(k + "=" + v + "&");

            }
        }
        signBuffer.append("key=" + secretKey);
        String sign = signBuffer.toString();
        String secrectSign = HmacSHA256Util.HMAC_SHA256(sign,secretKey);
        return secrectSign;
    }

    public static void main(String[] args) throws Exception{

//
//        MchSymbolInfoModel model = new MchSymbolInfoModel();
//        model.setMchNo("1000000003");
//        model.setNonceStr("123");

        MchPreOrderModel model = new MchPreOrderModel();
        model.setMchNo("1000000057");
        model.setNonceStr("5K8264ILTKCH16CQ2502SI8ZNMTM67VS");
        model.setMchOrderNo("SO0100017462");
        model.setBody("VMORE Product");
//        model.setSymbol("IFR");
//        model.setTotalAmount(new BigDecimal("356.580"));
        model.setTradeType("APP");
        model.setNotifyUrl("https://dvtrial-api.securevws.com/infinitepay_payment_return.php");
        String  rspSign = SignUtil.sign(model,"cLYLxHDeleSGeTKYRvyEDoRutjBpqEog");
        System.out.println(rspSign);

        BigDecimal temp = new BigDecimal(0.0036);



        String secrectSign = HmacSHA256Util.HMAC_SHA256("test","qoRiuJDgyvPxgWAgKuFBsosYyuqdSlXi");
        System.out.println(secrectSign);
    }


//    /**
//     * 商户接口签名
//     * @param map
//     * @param secretKey
//     * @return
//     */
//    public static String sign(Map<String,Object> map,String secretKey){
//        StringBuffer signBuffer = new StringBuffer();
//        Set<Map.Entry<String, Object>> es = map.entrySet();
//        Iterator<Map.Entry<String, Object>> it = es.iterator();
//        while (it.hasNext()) {
//            Map.Entry<String, Object> entry = (Map.Entry<String,Object>) it.next();
//            String k = (String) entry.getKey();
//            Object v = (Object) entry.getValue();
//            if(v != null){
//                signBuffer.append(k + "=" + v + "&");
//            }
//        }
//        signBuffer.append("key=" + secretKey);
//        String sign = signBuffer.toString();
//        String secrectSign = HmacSHA256Util.HMAC_SHA256(sign,secretKey);
//        return secrectSign;
//    }

    /**
     * IFR充值回调参数验证
     *
     * @param params
     * @return
     */
    public static boolean verifySignature(Map<String, Object> params, LinkedHashMap<String, Object> paramsTwo) {
        String backSign = (String) params.get("sign");
        String sha1Sign = getSignature(paramsTwo);
        if (StringUtils.isBlank(backSign) || StringUtils.isBlank(sha1Sign)) {
            return false;
        }
        return sha1Sign.equals(backSign);
    }

    public static String getSignature(LinkedHashMap<String, Object> params) {
        // Sign=UpperCase(SHA1(“service_version=1.0&billno=2017012048000016&partner_orderid= abc123123456))
        StringBuilder signBuilder = new StringBuilder();
        params.forEach((k, v) -> {
            signBuilder.append(k).append("=").append(v).append("&");
        });
        String str = signBuilder.toString();
        return HashUtil.sha1(str.substring(0, str.length() - 1)).toUpperCase();
    }

}
