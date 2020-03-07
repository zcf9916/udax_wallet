package com.udax.front.interceptor;


import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.Param;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.jwt.IJWTInfo;
import com.udax.front.biz.CacheBiz;
import com.udax.front.configuration.UserConfiguration;
import com.udax.front.util.CacheBizUtil;
import com.udax.front.util.user.JwtTokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.RequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.github.wxiaoqi.security.common.constant.Constants.BaseParam.TOKEN_PARAM;

/**
 * Created by ace on 2017/9/10.
 */
public class UserAuthRestInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(UserAuthRestInterceptor.class);
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserConfiguration userConfiguration;

    @Autowired
    private CacheBiz cacheBiz;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try{

         //   printParam(request);
            BaseContextHandler.set("beginTime",System.currentTimeMillis());
            // HandlerMethod handlerMethod = (HandlerMethod) handler;
            String token = request.getHeader(userConfiguration.getUserTokenHeader());
            if(StringUtils.isBlank(token)){
                sendJsonMessage(response,new ObjectRestResponse<>().status(ResponseCode.TOKEN_VALID));
                return false;
            }

            IJWTInfo infoFromToken = jwtTokenUtil.getInfoFromToken(token);
            //通过token获取用户
            FrontUser frontUser = (FrontUser) CacheUtil.getCache().get(token);
            if(frontUser == null ){
                sendJsonMessage(response,new ObjectRestResponse<>().status(ResponseCode.TOKEN_VALID));
                return false;
            }
            //获取后台token相关参数
            //一个用户可能会产生两条token,所以登陆的时候保存了他们双向的关系.
            String real_token = (String) CacheUtil.getCache().get(infoFromToken.getUid());
            if(StringUtils.isBlank(real_token)||!real_token.equals(token)){
                CacheUtil.getCache().del(token);
                sendJsonMessage(response,new ObjectRestResponse<>().status(ResponseCode.TOKEN_VALID));
                return false;
            }

            Param token_param = (Param) CacheUtil.getCache().get(TOKEN_PARAM);
            int expire = 600;//600秒
            if(token_param != null){
                expire = Integer.parseInt(token_param.getParamValue());
            }
            CacheUtil.getCache().expire(real_token, expire);

            BaseContextHandler.setUsername(infoFromToken.getUsername());
            BaseContextHandler.setUid(infoFromToken.getUid());
            BaseContextHandler.setUserID(infoFromToken.getId());
            BaseContextHandler.setToken(token);
            BaseContextHandler.setExId(infoFromToken.getExchangeId());
        } catch (Exception e){
            logger.error(e.getMessage(),e);
            sendJsonMessage(response,new ObjectRestResponse<>().status(ResponseCode.TOKEN_VALID));
            return false;
        }

        return super.preHandle(request, response, handler);
    }


    /**
     * 将某个对象转换成json格式并发送到客户端
     * @param response
     * @param obj
     * @throws Exception
     */
    public  void sendJsonMessage(HttpServletResponse response, Object obj) throws Exception {response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.print(JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat));
        writer.close();
        response.flushBuffer();
    }
//
//
//    private  void printParam(HttpServletRequest request,HttpServletResponse response){
//        RequestWrapper requestWrapper = new RequestWrapper(request);
//        // 获取@RequestBody注解参数和post请求参数
//        String body = requestWrapper.getBody();
//        System.out.println("拦截器输出body：" + body);
//        System.out.println("uri=" + request.getRequestURI());
//        // 获取get请求参数
//        Map<String, String[]> ParameterMap = request.getParameterMap();
//        System.out.println("参数个数：" + ParameterMap.size());
//        Map reqMap = new HashMap();
//        Set<Map.Entry<String, String[]>> entry = ParameterMap.entrySet();
//        Iterator<Map.Entry<String, String[]>> it = entry.iterator();
//        while (it.hasNext()) {
//            Map.Entry<String, String[]> me = it.next();
//            String key = me.getKey();
//            String value = me.getValue()[0];
//            reqMap.put(key, value);
//        }
//        String queryString = JSONUtils.JsonToString(JSONUtils.MapToJson(reqMap));
//        System.out.println(queryString);
//        // 不做拦截的地址
//        if (request.getRequestURI().equals("/api/code/get/pageCode")) {
//            return true;
//        }
//        // 验证session是否存在
//        Object obj = request.getSession().getAttribute("_session_user");
//        if (obj == null) {
//            response.sendRedirect("/user/login_view");//重定向
//            return false;
//        }
//        return true;
//    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Long endTime = System.currentTimeMillis();
        Long startTime = (Long)BaseContextHandler.get("beginTime");
        logger.info(request.getServletPath() + " >> http请求结束,耗时：" + (endTime - startTime) + "ms");
        BaseContextHandler.remove();
        super.afterCompletion(request, response, handler, ex);
    }
}
