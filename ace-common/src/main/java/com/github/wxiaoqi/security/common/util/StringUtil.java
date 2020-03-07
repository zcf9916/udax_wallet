package com.github.wxiaoqi.security.common.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 一些字符串操作类
 */
public class StringUtil {


    public static String midleReplaceStar(String phone){
        String result=null;
        if (!StringUtils.isEmpty(phone)){
            if (phone.length()<5){
                result=phone;
            }else{
                int length = phone.length();//手机号长度
                int endStartIndex = (length - 4) / 2;
                int beginEndIndex = endStartIndex + 4;
                String start = phone.substring(0,endStartIndex);
                String end = phone.substring(beginEndIndex);
                StringBuilder sb=new StringBuilder();
                sb.append(start).append("****").append(end);
                result=sb.toString();
            }
        }
        return result;
    }


    /**
     * 补齐指定的字符串
     * @param s
     * @param fix
     * @param length
     * @return
     */
    public static String completedWithFixString(String s,String fix,int length){
            if(s.length() >= length){
               return s;
           }
           int needToFix = length - s.length();
           for(int i = 0 ; i < needToFix ;i ++){
               s = fix + s;
           }
           return s;
    }

    /**
     * 首字母小写
     * @param name
     * @return
     */
    public static String LowerCaseString(String name) {
        char[] cs=name.toCharArray();
        cs[0]+=32;
        return String.valueOf(cs);

    }

    /**
     * 首字母大写
     * @param name
     * @return
     */
    public static String UpperCaseString(String name) {
        char[] cs=name.toCharArray();
        cs[0]-=32;
        return String.valueOf(cs);

    }
    /**
     * list是否为空
     * @param list
     * @return
     */
    public static boolean  listIsBlank(List list){
        return list == null || list.size()  < 1;
    }
    /**
     * list是否非空
     * @param list
     * @return
     */
    public static boolean  listIsNotBlank(List list){
        return list != null && list.size()  > 0;
    }
    /**
     * map是否非空
     * @param map
     * @return
     */
    public static boolean  mapIsNotBlank(Map map){
        return map != null && map.size()  > 0;
    }
    /**
     * map是否非空
     * @param map
     * @return
     */
    public static boolean  mapIsBlank(Map map){
        return map == null || map.size()  < 1;
    }

    /**
     * 用户身份证号码的打码隐藏加星号加*
     * <p>18位和非18位身份证处理均可成功处理</p>
     * <p>参数异常直接返回null</p>
     *
     * @param idCardNum 身份证号码
     * @param front     需要显示前几位
     * @param end       需要显示末几位
     * @return 处理完成的身份证
     */
    public static String idMask(String idCardNum, int front, int end) {
        //身份证不能为空
        if (StringUtils.isEmpty(idCardNum)) {
            return idCardNum;
        }
        if(front>idCardNum.length()) {
        	return "*****";
        }
        //需要截取的长度不能大于身份证号长度
        if ((front + end) > idCardNum.length()) {
            end=0;
        }
        //需要截取的不能小于0
        if (front < 0 || end < 0) {
            return idCardNum;
        }
        //计算*的数量
        int asteriskCount = idCardNum.length() - (front + end);
        StringBuffer asteriskStr = new StringBuffer();
        for (int i = 0; i < asteriskCount; i++) {
            asteriskStr.append("*");
        }
        String regex = "(\\w{" + String.valueOf(front) + "})(\\w+)(\\w{" + String.valueOf(end) + "})";
        return idCardNum.replaceAll(regex, "$1" + asteriskStr + "$3");
    }

}
