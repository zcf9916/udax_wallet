package com.github.wxiaoqi.security.admin.util;

import com.github.wxiaoqi.security.common.annotation.Sign;
import com.github.wxiaoqi.security.common.util.VerifySign;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal
        ;
import java.util.TreeMap;

/**
 * Created by ace on 2017/9/12.
 * <p>
 * 一些经常调用的方法
 */

public class verifySignUtil {


    /**
     * 对对象的所有非空字段按字段字母大小顺序并生成签名
     *
     * @param object
     * @param signKey
     * @return
     */
    public static String macSign(Object object, String signKey) throws Exception {
        TreeMap<String, Object> params = new TreeMap<String, Object>();
        Class<?> cl = object.getClass();
        Field[] fields = cl.getDeclaredFields();// 获取model的所有字段
        for (Field field : fields) {
            //字段上是否有sign注解
            Sign sign = field.getAnnotation(Sign.class);
            if (sign == null) {
                continue;
            }
            String getMethodName = "get" + StringUtils.capitalize(field.getName());
            Method method = cl.getDeclaredMethod(getMethodName);
            Object ob = method.invoke(object);
            if (ob == null || (field.getType() == String.class && StringUtils.isBlank((String) ob))) {
                continue;
            }
            if (field.getType() == BigDecimal.class) {
                params.put(field.getName(), ((BigDecimal) ob).toPlainString());
                continue;
            }
            if (ob != null) {
                // 参数不为空,放到map
                params.put(field.getName(), ob);
            }
        }
        return VerifySign.signature(params, signKey);
    }


    /**
     * 验证对象签名
     *
     * @param object
     * @param signKey
     * @param signStr 待验证的签名
     * @return
     */
    public static boolean verifySign(Object object, String signKey, String signStr) throws Exception {
        TreeMap<String, Object> params = new TreeMap<String, Object>();
        Class<?> cl = object.getClass();
        Field[] fields = cl.getDeclaredFields();// 获取model的所有字段
        for (Field field : fields) {
            //字段上是否有sign注解
            Sign sign = field.getAnnotation(Sign.class);
            if (sign == null) {
                continue;
            }
            String getMethodName = "get" + StringUtils.capitalize(field.getName());
            Method method = cl.getDeclaredMethod(getMethodName);
            Object ob = method.invoke(object);
            if (ob == null || (field.getType() == String.class && StringUtils.isBlank((String) ob))) {
                continue;
            }
            if (field.getType() == BigDecimal.class) {
                params.put(field.getName(), ((BigDecimal) ob).toPlainString());
                continue;
            }
            if (ob != null) {
                // 参数不为空,放到map
                params.put(field.getName(), ob);
            }
        }
        return VerifySign.verify(params, signStr, signKey);
    }


}
