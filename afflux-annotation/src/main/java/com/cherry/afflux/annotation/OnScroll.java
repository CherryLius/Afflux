package com.cherry.afflux.annotation;

import com.cherry.afflux.annotation.internal.ListenerClass;
import com.cherry.afflux.annotation.internal.ListenerMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by LHEE on 2017/5/12.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@ListenerClass(
        targetType = "android.widget.AbsListView",
        setter = "setOnScrollListener",
        type = "android.widget.AbsListView.OnScrollListener",
        callbacks = OnScroll.Callback.class
)
public @interface OnScroll {
    int[] value() default 0;

    Callback callback() default Callback.ON_SCROLL;

    enum Callback {
        @ListenerMethod(
                name = "onScrollStateChanged",
                parameters = {
                        "android.widget.AbsListView",
                        "int"
                }
        )
        ON_SCROLL_STATE_CHANGED,
        @ListenerMethod(
                name = "onScroll",
                parameters = {
                        "android.widget.AbsListView",
                        "int",
                        "int",
                        "int"
                }
        )
        ON_SCROLL
    }
}
