package com.cherry.afflux.api;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by LHEE on 2017/3/14.
 */

public final class Afflux {

    private static final String TAG = "Afflux";
    private static final Map<Class<?>, Constructor<? extends Unbinder>> BINDERS = new LinkedHashMap<>();

    private Afflux() {

    }

    public static Unbinder bind(Activity target) {
        View source = target.getWindow().getDecorView();
        return createBinder(target, source);
    }

    public static Unbinder bind(Object target, View source) {
        return createBinder(target, source);
    }

    private static Unbinder createBinder(Object target, View source) {
        Class<?> targetClass = target.getClass();
        Constructor<? extends Unbinder> constructor = findBinderConstructor(targetClass);
        if (constructor == null) {
            Log.e(TAG, "No Constructor Find for " + targetClass.getName());
            return null;
        }
        try {
            return constructor.newInstance(target, source);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException("Unable to create binding instance.", cause);
        }
    }

    private static Constructor<? extends Unbinder> findBinderConstructor(Class<?> clazz) {
        Constructor<? extends Unbinder> constructor = BINDERS.get(clazz);
        if (constructor != null)
            return constructor;
        String className = clazz.getName();
        Log.i(TAG, "className=" + className);
        try {
            Class<?> binderClass = Class.forName(className + "_Binder");
            constructor = (Constructor<? extends Unbinder>) binderClass.getConstructor(clazz, View.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            constructor = findBinderConstructor(clazz.getSuperclass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find binding constructor for " + className, e);
        }
        BINDERS.put(clazz, constructor);
        return constructor;
    }
}
