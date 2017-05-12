package com.cherry.afflux.compiler.model;

import com.cherry.afflux.annotation.internal.ListenerClass;
import com.cherry.afflux.annotation.internal.ListenerMethod;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;

import static com.cherry.afflux.compiler.common.Type.bestGuess;

/**
 * Created by Administrator on 2017/5/12.
 */

public class MethodBinding {
    private int viewId;
    private Map<ListenerClass, List<BindingViewMethod>> methodMap;


    public MethodBinding(int viewId) {
        this.viewId = viewId;
        methodMap = new HashMap<>();
    }

    public void addMethod(BindingViewMethod method) {
        ListenerClass listenerKey = method.getListenerClass();
        List<BindingViewMethod> methodList = methodMap.get(listenerKey);
        if (methodList == null) {
            methodList = new ArrayList<>();
            methodMap.put(listenerKey, methodList);
        }
        methodList.add(method);
    }

    public int getViewId() {
        return viewId;
    }

    public Map<ListenerClass, List<BindingViewMethod>> getMethodMap() {
        return methodMap;
    }

    public TypeSpec generateListener(ListenerClass listenerClass, List<BindingViewMethod> methods) {
        //listener
        TypeSpec.Builder listener = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(ClassName.bestGuess(listenerClass.type()));

        for (ListenerMethod listenerMethod : getListenerMethods(listenerClass)) {
            //listener method
            MethodSpec.Builder method = MethodSpec.methodBuilder(listenerMethod.name())
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(bestGuess(listenerMethod.returnType()));
            //method params
            String[] params = listenerMethod.parameters();
            for (int i = 0; i < params.length; i++) {
                method.addParameter(bestGuess(params[i]), ("arg" + i));
            }
            method.addCode(generateStateCode(listenerMethod, methods));
            listener.addMethod(method.build());
        }
        return listener.build();
    }

    private CodeBlock generateStateCode(ListenerMethod listenerMethod, List<BindingViewMethod> methods) {
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        boolean hasReturn = !"void".equals(listenerMethod.returnType());
        if (hasReturn) {
            codeBuilder.add("return ");
        }
        boolean matched = false;
        for (BindingViewMethod binding : methods) {
            if (binding.getListenerMethod().equals(listenerMethod)) {
                List<Parameter> parameters = binding.getParameters();
                codeBuilder.add("target.$N(", binding.getSimpleName());
                for (int i = 0; i < parameters.size(); i++) {
                    Parameter parameter = parameters.get(i);
                    int listenerPosition = parameter.getPosition();
                    if (i > 0) {
                        codeBuilder.add(", ");
                    }
                    codeBuilder.add("arg$L", listenerPosition);
                }
                codeBuilder.add(");\n");
                matched = true;
                break;
            }
        }
        if (!matched && hasReturn) {
            codeBuilder.add("$S;\n", listenerMethod.defaultReturn());
        }
        return codeBuilder.build();
    }

    private List<ListenerMethod> getListenerMethods(ListenerClass listenerClass) {
        ListenerMethod[] listenerMethods = listenerClass.method();
        if (listenerMethods.length > 1) {
            throw new IllegalStateException(String.format("Multiple listener methods specified on @%s."
                    /*mAnnotationClass.getSimpleName()*/));
        } else if (listenerMethods.length == 1) {
            if (listenerClass.callbacks() != ListenerClass.NONE.class) {
                throw new IllegalStateException(
                        String.format("Both method() and callback() defined on @%s."
                                /*mAnnotationClass.getSimpleName()*/));
            }
            return Arrays.asList(listenerClass.method());
        } else {

            try {
                List<ListenerMethod> methodList = new ArrayList<>();
                Class<? extends Enum<?>> callbacks = listenerClass.callbacks();
                for (Enum<?> callback : callbacks.getEnumConstants()) {
                    Field field = callback.getDeclaringClass().getDeclaredField(callback.name());
                    ListenerMethod method = field.getAnnotation(ListenerMethod.class);
                    if (method == null) {
                        throw new IllegalStateException(String.format("@%s's %s.%s missing @%s annotation.",
                                callbacks.getEnclosingClass().getSimpleName(), callbacks.getSimpleName(),
                                callback.name(), ListenerMethod.class.getSimpleName()));
                    }
                    methodList.add(method);
                }
                return methodList;
            } catch (NoSuchFieldException e) {
                throw new AssertionError(e);
            }

        }
    }
}
