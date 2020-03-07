package com.udax.front.annotation;

import java.lang.annotation.*;

/**
 * 对用户操作(提币,转账，跟单交易等)的拦截
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UserOperationAuthority {

    String[] value();
}
