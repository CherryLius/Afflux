package com.cherry.afflux.annotation;

import com.cherry.afflux.annotation.internal.ListenerClass;
import com.cherry.afflux.annotation.internal.ListenerMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2017/5/23.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
@ListenerClass(
        targetType = "android.widget.RadioGroup",
        setter = "setOnCheckedChangeListener",
        type = "android.widget.RadioGroup.OnCheckedChangeListener",
        method = @ListenerMethod(
                name = "onCheckedChanged",
                parameters = {
                        "android.widget.RadioGroup",
                        "int"
                }
        )
)
public @interface OnRadioGroupCheckedChanged {
    int[] value() default 0;
}
