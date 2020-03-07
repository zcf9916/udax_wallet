package com.udax.front.annotation.aspect;

import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.udax.front.annotation.RedisLockTable;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.Signature;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.stereotype.Component;


//@Aspect
//@Component
public class RedisLockTableAspect {
//    @Pointcut("execution(public * com.udax.front.biz.*.*(..)) && @annotation(com.udax.front.annotation.RedisLockTable)" )
//    public void addAdvice(){}
//    @Around("addAdvice()")
//    public Object arround(ProceedingJoinPoint pjp){
//        Signature signature = pjp.getSignature();//此处joinPoint的实现类是MethodInvocationProceedingJoinPoint
//        MethodSignature methodSignature = (MethodSignature) signature;//获取参数名
//        RedisLockTable annotation = methodSignature.getMethod().getAnnotation(RedisLockTable.class);
//        String tableName = annotation.name();
//        try{
//            if (CacheUtil.tryLock(tableName)) {
//                Object  result =pjp.proceed();
//                return result;
//            }
//        }catch (Throwable t){
//            if(t instanceof RuntimeException)
//                throw  (RuntimeException)t;
//            else if(t instanceof Error)
//                throw (Error)t;
//            else throw new RuntimeException(t);
//        }finally {
//            CacheUtil.unlock(tableName);
//        }
//        return null;
//    }


}