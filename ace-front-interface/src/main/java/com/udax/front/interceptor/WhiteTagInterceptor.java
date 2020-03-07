package com.udax.front.interceptor;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.LocalUtil;
import com.udax.front.biz.CacheBiz;
import com.udax.front.util.CacheBizUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Locale;

public class WhiteTagInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private CacheBiz cacheBiz;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        String exInfo = request.getHeader("exInfo");
        if(StringUtils.isBlank(exInfo)){
            sendJsonMessage(response,new ObjectRestResponse<>().status(ResponseCode.EXINFO_LENGTH));
            return false;
        }
        Long exchId = CacheBizUtil.getExchId(exInfo,cacheBiz);
        if(exchId == null || exchId.longValue() < 1){
            sendJsonMessage(response,new ObjectRestResponse<>().status(ResponseCode.EXINFO_LENGTH));
            return false;
        }
        BaseContextHandler.setAppExId(exchId);//app所属白标id


        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }


    /**
     * 将某个对象转换成json格式并发送到客户端
     * @param response
     * @param obj
     * @throws Exception
     */
    public  void sendJsonMessage(HttpServletResponse response, Object obj) throws Exception {
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.print(JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat));
        writer.close();
        response.flushBuffer();
    }

}
