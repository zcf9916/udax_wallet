package com.udax.front.annotation;

import javax.validation.Constraint;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DcCodeValid.class)  // 关联解析类
@Target(ElementType.FIELD)  // 注解作用于方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface DcCode {
    String message() default "{DC_INVALID}";
    boolean required() default true;

    Class<?>[] groups() default {};
    Class<? extends javax.validation.Payload>[] payload() default {};
}