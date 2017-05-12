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

/**
 * listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
 *
 * @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
 * <p>
 * }
 * });
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@ListenerClass(
        targetType = "android.widget.AdapterView<?>",
        setter = "setOnItemClickListener",
        type = "android.widget.AdapterView.OnItemClickListener",
        method = @ListenerMethod(
                name = "onItemClick",
                parameters = {
                        "android.widget.AdapterView<?>",
                        "android.view.View",
                        "int",
                        "long"
                }
        )
)
public @interface OnItemClick {
    int[] value() default 0;
}
