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
        targetType = "android.widget.AdapterView<?>",
        setter = "setOnItemSelectedListener",
        type = "android.widget.AdapterView.OnItemSelectedListener",
        callbacks = OnItemSelected.Callback.class
)
public @interface OnItemSelected {
    int[] value() default 0;

    Callback callback() default Callback.ITEM_SELECTED;

    enum Callback {
        @ListenerMethod(
                name = "onItemSelected",
                parameters = {
                        "android.widget.AdapterView<?>",
                        "android.view.View",
                        "int",
                        "long"
                }
        )
        ITEM_SELECTED,
        @ListenerMethod(
                name = "onNothingSelected",
                parameters = "android.widget.AdapterView<?>"
        )
        NOTHING_SELECTED
    }
}
