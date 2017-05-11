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

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@ListenerClass(
        targetType = "android.view.View",
        setter = "setOnLongClickListener",
        type = "android.view.View.OnLongClickListener",
        method = @ListenerMethod(
                name = "onLongClick",
                parameters = "android.view.View",
                returnType = "boolean",
                defaultReturn = "false"
        )
)
public @interface OnLongClick {
    int[] value() default 0;
}
