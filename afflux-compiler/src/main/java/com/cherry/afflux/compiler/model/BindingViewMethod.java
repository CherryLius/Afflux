package com.cherry.afflux.compiler.model;

import com.cherry.afflux.annotation.internal.ListenerClass;
import com.cherry.afflux.annotation.internal.ListenerMethod;
import com.cherry.afflux.compiler.log.Logger;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;

/**
 * Created by Administrator on 2017/5/2.
 */

public class BindingViewMethod {
    private ExecutableElement mExecutableElement;
    private String mFieldName;
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
    }

    public int getViewId() {
        return mViewId;
    }

    public String getFieldName() {
        return mFieldName;
    }

    public void setFieldName(String fieldName) {
        mFieldName = fieldName;
    }

    public String setter() {
        return mListenerClass.setter();
    }

    /**
     * textView.setOnClickListener(new View.OnClickListener() {
     *
     * @return
     * @Override public void onClick(View v) {
     * <p>
     * }
     * });
     */
    public TypeSpec generateListener() {
        TypeSpec.Builder listener = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(ClassName.bestGuess(mListenerClass.type()));
        for (ListenerMethod method : mListenerMethods) {
            MethodSpec.Builder m = MethodSpec.methodBuilder(method.name())
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(bestGuess(method.returnType()));

            //method params
            String[] params = method.parameters();
            for (int i = 0; i < params.length; i++) {
                m.addParameter(ClassName.bestGuess(params[i]), ("arg" + i));
            }
            listener.addMethod(m.build());
        }
        return listener.build();
    }

    private static TypeName bestGuess(String type) {
        switch (type) {
            case "void":
                return TypeName.VOID;
            case "boolean":
                return TypeName.BOOLEAN;
            case "byte":
                return TypeName.BYTE;
            case "char":
                return TypeName.CHAR;
            case "double":
                return TypeName.DOUBLE;
            case "float":
                return TypeName.FLOAT;
            case "int":
                return TypeName.INT;
            case "long":
                return TypeName.LONG;
            case "short":
                return TypeName.SHORT;
            default:
                return ClassName.bestGuess(type);
        }
    }
}
