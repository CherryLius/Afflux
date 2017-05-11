package com.cherry.afflux.annotation;

/**
 * Created by Administrator on 2017/5/11.
 */

import com.cherry.afflux.annotation.internal.ListenerClass;
import com.cherry.afflux.annotation.internal.ListenerMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@ListenerClass(
        targetType = "android.view.View",
        setter = "setOnFocusChangeListener",
        type = "android.view.View.OnFocusChangeListener",
        method = @ListenerMethod(
                name = "onFocusChange",
                parameters = {
                        "android.view.View",
                        "boolean"
                }
        )
)
public @interface OnFocusChange {
    int[] value() default 0;
}
