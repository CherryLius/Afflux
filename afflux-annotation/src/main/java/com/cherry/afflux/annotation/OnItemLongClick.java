package com.cherry.afflux.annotation;

import com.cherry.afflux.annotation.internal.ListenerClass;
import com.cherry.afflux.annotation.internal.ListenerMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2017/5/12.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@ListenerClass(
        targetType = "android.widget.AdapterView<?>",
        setter = "setOnItemLongClickListener",
        type = "android.widget.AdapterView.OnItemLongClickListener",
        method = @ListenerMethod(
                name = "onItemLongClick",
                parameters = {
                        "android.widget.AdapterView<?>",
                        "android.view.View",
                        "int",
                        "long"
                },
                returnType = "boolean",
                defaultReturn = "false"
        )
)
public @interface OnItemLongClick {
    int[] value() default 0;
}
