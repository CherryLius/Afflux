package com.cherry.afflux.annotation.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2017/5/2.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ListenerClass {
    String targetType();

    String setter();

    String remover() default "";

    String type();

    ListenerMethod[] method() default {};

    Class<? extends Enum<?>> callbacks() default NONE.class;

    enum NONE {

    }
}
