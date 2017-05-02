package com.cherry.afflux.annotation.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2017/5/2.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ListenerMethod {
    String name();

    String[] parameters() default {};

    String returnType() default "void";

    String defaultReturn() default "null";
}
