package com.cherry.afflux.compiler.model;

import com.cherry.afflux.compiler.common.Type;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.lang.model.element.Element;

/**
 * Created by LHEE on 2017/5/12.
 */

public class BindingResourceField {

    public interface ResourceType {
        String GET_BOOLEAN = "getBoolean";
        String GET_COLOR = "getColor";
        String GET_DRAWABLE = "getDrawable";
        String GET_FLOAT = "getFloat";
        String GET_INT = "getInt";
        String GET_STRING = "getString";
    }

    private int resId;
    private String name;
    private String type;

    public BindingResourceField(Element element, Class<? extends Annotation> annotationClass, String type) {
        try {
            Annotation annotation = element.getAnnotation(annotationClass);
            Method method = annotationClass.getDeclaredMethod("value");
            resId = (int) method.invoke(annotation);
            this.name = element.getSimpleName().toString();
            this.type = type;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public BindingResourceField(int resId, String name, String type) {
        this.resId = resId;
        this.name = name;
        this.type = type;
    }

    public CodeBlock generateCode() {
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        codeBuilder.add("this.target.$N=$T.$N(target,source,$L);\n",
                name, Type.AFFLUX_UTILS, type, resId);
        return codeBuilder.build();
    }

    public CodeBlock generateUnbindCode() {
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        String args;
        switch (type) {
            case ResourceType.GET_BOOLEAN:
                args = "false";
                break;
            case ResourceType.GET_INT:
            case ResourceType.GET_FLOAT:
            case ResourceType.GET_COLOR:
                args = "0";
                break;
            case ResourceType.GET_DRAWABLE:
            case ResourceType.GET_STRING:
            default:
                args = "null";
                break;
        }
        String code = String.format("this.target.$N=%s;\n", args);
        codeBuilder.add(code, name);
        return codeBuilder.build();
    }
}
