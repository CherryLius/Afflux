package com.cherry.afflux.api.internal;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by LHEE on 2017/5/12.
 */

public final class Utils {
    private static final TypedValue VALUE = new TypedValue();

    public static boolean getBoolean(Object target, View source, int resId) {
        Context context = getContext(target, source);
        return context.getResources().getBoolean(resId);
    }

    public static int getColor(Object target, View source, int resId) {
        return ContextCompat.getColor(getContext(target, source), resId);
    }

    public static Drawable getDrawable(Object target, View source, int resId) {
        return ContextCompat.getDrawable(getContext(target, source), resId);
    }

    public static int getInt(Object target, View source, int resId) {
        Context context = getContext(target, source);
        return context.getResources().getInteger(resId);
    }

    public static float getFloat(Object target, View source, int resId) {
        TypedValue value = VALUE;
        Context context = getContext(target, source);
        context.getResources().getValue(resId, value, true);
        if (value.type == TypedValue.TYPE_FLOAT) {
            return value.getFloat();
        }
        throw new Resources.NotFoundException("Resource ID #0x" + Integer.toHexString(resId)
                + " type #0x" + Integer.toHexString(value.type) + " is not valid");
    }

    public static String getString(Object target, View source, int resId) {
        return getContext(target, source).getString(resId);
    }

    private static Context getContext(Object target, View source) {
        if (target instanceof Activity) {
            Activity activity = (Activity) target;
            return activity.getBaseContext();
        } else if (target instanceof ContextWrapper) {
            ContextWrapper wrapper = (ContextWrapper) target;
            return wrapper.getBaseContext();
        } else if (target instanceof Context) {
            Context context = (Context) target;
            return context;
        } else if (target instanceof View) {
            View view = (View) target;
            return view.getContext();
        } else {
            if (source != null) {
                return source.getContext();
            }
            throw new IllegalArgumentException("target cannot getContext: " + target.getClass());
        }
    }
}
