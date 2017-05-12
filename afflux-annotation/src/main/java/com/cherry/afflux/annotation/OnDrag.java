package com.cherry.afflux.annotation;

/**
 * Created by LHEE on 2017/5/12.
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
        setter = "setOnDragListener",
        type = "android.view.View.OnDragListener",
        method = @ListenerMethod(
                name = "onDrag",
                parameters = {
                        "android.view.View",
                        "android.view.DragEvent"
                },
                returnType = "boolean",
                defaultReturn = "false"
        )
)
public @interface OnDrag {
    int[] value() default 0;
}
