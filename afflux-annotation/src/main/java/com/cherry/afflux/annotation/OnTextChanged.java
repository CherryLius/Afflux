package com.cherry.afflux.annotation;

/**
 * Created by Administrator on 2017/5/12.
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
        setter = "addTextChangedListener",
        remover = "removeTextChangedListener",
        type = "android.text.TextWatcher",
        callbacks = OnTextChanged.Callback.class
)
public @interface OnTextChanged {
    int[] value() default 0;

    Callback callback() default Callback.ON_TEXT_CHANGED;

    enum Callback {
        @ListenerMethod(
                name = "beforeTextChanged",
                parameters = {
                        "java.lang.CharSequence",
                        "int",
                        "int",
                        "int"
                }
        )
        BEFORE_TEXT_CHANGED,

        @ListenerMethod(
                name = "onTextChanged",
                parameters = {
                        "java.lang.CharSequence",
                        "int",
                        "int",
                        "int"
                }
        )
        ON_TEXT_CHANGED,

        @ListenerMethod(
                name = "afterTextChanged",
                parameters = "android.text.Editable"
        )
        AFTER_TEXT_CHANGED
    }
}
