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
        targetType = "android.support.v4.view.ViewPager",
        setter = "addOnPageChangeListener",
        remover = "removeOnPageChangeListener",
        type = "android.support.v4.view.ViewPager.OnPageChangeListener"
)
public @interface OnPageChange {
    int[] value() default 0;

    Callback callback() default Callback.ON_PAGE_SELECTED;

    enum Callback {
        @ListenerMethod(
                name = "onPageScrolled",
                parameters = {
                        "int",
                        "float",
                        "int"
                }
        )
        ON_PAGE_SCROLLED,
        @ListenerMethod(
                name = "onPageSelected",
                parameters = "int"
        )
        ON_PAGE_SELECTED,
        @ListenerMethod(
                name = "onPageScrollStateChanged",
                parameters = "int"
        )
        ON_PAGE_SCROLL_STATE_CHANGED
    }
}
