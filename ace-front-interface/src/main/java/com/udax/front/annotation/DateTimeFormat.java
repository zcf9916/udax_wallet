package com.udax.front.annotation;

import javax.validation.Constraint;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateTimeFormatValid.class)  // 关联解析类
@Target(ElementType.FIELD)  // 注解作用于方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface DateTimeFormat {
    String message() default "{DATEFORMAT_ERROR}";


    Class<?>[] groups() default {};
    Class<? extends javax.validation.Payload>[] payload() default {};
}