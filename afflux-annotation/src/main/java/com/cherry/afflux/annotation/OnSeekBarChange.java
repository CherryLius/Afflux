package com.cherry.afflux.annotation;

import com.cherry.afflux.annotation.internal.ListenerClass;
import com.cherry.afflux.annotation.internal.ListenerMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2017/5/23.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@ListenerClass(
        targetType = "android.widget.SeekBar",
        setter = "setOnSeekBarChangeListener",
        type = "android.widget.SeekBar.OnSeekBarChangeListener",
        callbacks = OnSeekBarChange.Callback.class
)
public @interface OnSeekBarChange {

    int[] value() default 0;

    Callback callback() default Callback.ON_PROGRESS_CHANGED;

    enum Callback {
        @ListenerMethod(
                name = "onProgressChanged",
                parameters = {
                        "android.widget.SeekBar",
                        "int",
                        "boolean"
                }
        )
        ON_PROGRESS_CHANGED,
        @ListenerMethod(
                name = "onStartTrackingTouch",
                parameters = "android.widget.SeekBar"
        )
        ON_START_TRACKING_TOUCH,
        @ListenerMethod(
                name = "onStopTrackingTouch",
                parameters = "android.widget.SeekBar"
        )
        ON_STOP_TRACKING_TOUCH,
    }
}