package com.udax.front.task.jobConfigure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScheduledJob {

    String name();

    String group() default "UDAX_WALLET_GROUP";

    String cronExp();
}
