package com.udax.front.interceptor;

import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.udax.front.annotation.UserOperationAuthority;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import com.udax.front.service.ServiceUtil;
import com.github.wxiaoqi.security.common.enums.FreezeFunctionType;

import java.lang.reflect.Method;

/**
 * 此类为一个切面类，主要作用就是对接口的请求进行拦截
 * 拦截的方式，只需要在指定接口方法上面加上@MonitorRequest注解即可
 *
 * @author Tang
 * @since 2019/07/29
 */
@Aspect
@Component
public class OperationAuthorityAspect {

    protected final static Logger logger = LogManager.getLogger();

    //定义切点方法
    @Pointcut("@annotation(com.udax.front.annotation.UserOperationAuthority)")
    public  void pointCut(){}

    //环绕通知
    @Around("pointCut()")
    public Object operationAuthorityCheck (ProceedingJoinPoint joinPoint) throws Exception{
        Object result = null;
        try{
            Long userId = BaseContextHandler.getUserID();
            Signature signature = joinPoint.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();

            UserOperationAuthority authority =(UserOperationAuthority)method.getAnnotation(UserOperationAuthority.class);
            String[] values = authority.value();
            if(values != null && values.length > 0){
                // 校验当前用户提币功能是否被禁用
                boolean flag = ServiceUtil.checkOperationAuthority(FreezeFunctionType.functionTypeToValue(values[0]), userId);
                if (!flag) {
                    logger.info("用户"+userId+" "+values[0]+"操作权限已被禁用！");
                    return new ObjectRestResponse<>().status(ResponseCode.NO_OPERATION_AUTHORITY);
                }
            }else{
                return new ObjectRestResponse<>().status(ResponseCode.NO_OPERATION_AUTHORITY);
            }

            result =joinPoint.proceed();
        }catch (Throwable  e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }

        return result;
    }
}
