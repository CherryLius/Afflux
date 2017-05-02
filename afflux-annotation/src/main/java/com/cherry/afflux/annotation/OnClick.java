package com.cherry.afflux.annotation;

import com.cherry.afflux.annotation.internal.ListenerClass;
import com.cherry.afflux.annotation.internal.ListenerMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2017/5/2.
 */
@ListenerClass(
        targetType = "android.view.View",
        setter = "setOnClickListener",
        type = "android.view.View.OnClickListener",
        method = @ListenerMethod(
                name = "onClick",
                parameters = "android.view.View"
        )
)
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface OnClick {
    int value() default 0;
}
