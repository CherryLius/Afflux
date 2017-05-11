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

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
@ListenerClass(
        targetType = "android.view.View",
        setter = "setOnTouchListener",
        type = "android.view.View.OnTouchListener",
        method = @ListenerMethod(
                name = "onTouch",
                parameters = {
                        "android.view.View",
                        "android.view.MotionEvent"
                },
                returnType = "boolean",
                defaultReturn = "false"
        )
)
public @interface OnTouch {
    int[] value() default 0;
}
