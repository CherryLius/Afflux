package com.cherry.afflux.compiler.model;

import com.cherry.afflux.annotation.internal.ListenerClass;
import com.cherry.afflux.annotation.internal.ListenerMethod;
import com.cherry.afflux.compiler.log.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;

/**
 * Created by Administrator on 2017/5/2.
 */

public class BindingViewMethod {
    private ExecutableElement mExecutableElement;
    private ListenerClass mListenerClass;
    private ListenerMethod[] mListenerMethods;

    private int mViewId;

    public BindingViewMethod(Element element, Class<? extends Annotation> annotationClass) {
        if (element.getKind() != ElementKind.METHOD)
            throw new IllegalStateException("must be method!");
        mExecutableElement = (ExecutableElement) element;
        try {
            // view id
            Annotation annotation = mExecutableElement.getAnnotation(annotationClass);
            Method method = annotationClass.getDeclaredMethod("value");
            mViewId = (int) method.invoke(annotation);
            //listener class info
            mListenerClass = annotationClass.getAnnotation(ListenerClass.class);
            mListenerMethods = mListenerClass.method();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.err("id = %d", mViewId);
        throw new IllegalStateException("111");
    }
}
