package com.cherry.afflux.annotation;

import com.cherry.afflux.annotation.internal.ListenerClass;
import com.cherry.afflux.annotation.internal.ListenerMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2017/5/11.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
@ListenerClass(
        targetType = "android.widget.CompoundButton",
        setter = "setOnCheckedChangeListener",
        type = "android.widget.CompoundButton.OnCheckedChangeListener",
        method = @ListenerMethod(
                name = "onCheckedChanged",
                parameters = {
                        "android.widget.CompoundButton",
                        "boolean"
                }
        )
)
public @interface OnCheckedChanged {
    int[] value() default 0;
}
