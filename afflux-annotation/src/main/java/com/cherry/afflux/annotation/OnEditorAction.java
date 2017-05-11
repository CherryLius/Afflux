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
        targetType = "android.widget.TextView",
        setter = "setOnEditorActionListener",
        type = "android.widget.TextView.OnEditorActionListener",
        method = @ListenerMethod(
                name = "onEditorAction",
                parameters = {
                        "android.widget.TextView",
                        "int",
                        "android.view.KeyEvent"
                },
                returnType = "boolean",
                defaultReturn = "false"
        )
)
public @interface OnEditorAction {
    int[] value() default 0;
}
