package com.github.wxiaoqi.security.common.util.merchant;

import org.apache.commons.lang3.StringUtils;

//邀请码生成工具
public class GenerateCodeUtil {

    public interface Data{
        public String VISIT_CODE= "0QWE8AS2DZX1H9C7P5IK3MJUFR4VYLTN6BG";

        /** 自定义进制(0,1没有加入,容易与o,l混淆) */
        public String MERCHANT_NO= "0123456789";
    }



    /**
     * 通过源字符串和步长,算出新的字符串
     * @param ori 传入的数据
     * @param incr_len 增加的步长
     * @param beginIndex 做递增的起始位置
     * @param data  基础数据
     * @return
     */
    public static String generateCode(String ori,int incr_len,int beginIndex,String data){
        //参数校验
        if(StringUtils.isBlank(ori)){
            return "";
        }
        incr_len = incr_len < 1 ? 1 : incr_len;
        beginIndex = beginIndex < 1? 1 : beginIndex;
        //从第几个字符开始做递增,默认1
        int charIndex = ori.length()-beginIndex;
        //需要计算的位数对应的字符
        char c = ori.charAt(charIndex);
        //定位字符所在位置
        int index = data.indexOf(c);
        //算出这个字符递增之后,所在数据的位置
        int nextIndex = (index + incr_len) % data.length();
        //替换位对应位数的字符
        char[] charArr = ori.toCharArray();
        charArr[charIndex] = data.charAt(nextIndex);
        ori = String.valueOf(charArr);
        //如果需要进位,继续往前一位加1,再做一次计算,以此类推
        if(index + incr_len >= data.length()){
            //如果超过长度,自动增长
            if(beginIndex >= ori.length()){
                ori = data.charAt(0) + ori;
            }
            ori =   generateCode(ori,1,++beginIndex,data);
        }
        return ori;
    }



    public static void  main(String[] args) throws InterruptedException {
        String str = "201900000";
        while(true){
            Thread.sleep(100);
            str =  GenerateCodeUtil.generateCode( str,3,1,Data.MERCHANT_NO);
            System.out.println(str);
        }
    }
}
