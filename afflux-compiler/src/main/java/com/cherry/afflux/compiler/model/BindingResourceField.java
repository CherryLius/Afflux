package com.cherry.afflux.compiler.model;

import com.cherry.afflux.compiler.common.Type;
import com.cherry.afflux.compiler.log.Logger;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.lang.model.element.Element;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Created by LHEE on 2017/5/12.
 */

public class BindingResourceField {

    private static final String GET_STRING_ARRAY = "getStringArray";
    private static final String GET_INT_ARRAY = "getIntArray";
    private static final String GET_TEXT_ARRAY = "getTextArray";

    private static final String GET_INT_DIMEN = "getDimensionPixelSize";
    private static final String GET_FLOAT_DIMEN = "getDimension";

    public interface ResourceType {
        String GET_ARRAY = "getArray";
        String GET_BITMAP = "getBitmap";
        String GET_BOOLEAN = "getBoolean";
        String GET_COLOR = "getColor";
        String GET_DIMEN = "getDimen";
        String GET_DRAWABLE = "getDrawable";
        String GET_FLOAT = "getFloat";
        String GET_INT = "getInt";
        String GET_STRING = "getString";
    }

    private int resId;
    private String name;
    private String type;

    private String resourceType;

    public BindingResourceField(Element element, Class<? extends Annotation> annotationClass, String type) {
        try {
            Annotation annotation = element.getAnnotation(annotationClass);
            Method method = annotationClass.getDeclaredMethod("value");
            resId = (int) method.invoke(annotation);
            this.name = element.getSimpleName().toString();
            resourceType = type;
            if (type.equals(ResourceType.GET_ARRAY)) {
                this.type = getResourceArrayType(element);
            } else if (type.equals(ResourceType.GET_DIMEN)) {
                this.type = getResourceDimenType(element);
            } else {
                this.type = type;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public CodeBlock generateCode() {
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        codeBuilder.add("this.target.$N = $T.$N(target, source, $L);\n",
                name, Type.AFFLUX_UTILS, type, resId);
        return codeBuilder.build();
    }

    public CodeBlock generateUnbindCode() {
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        String args;
        switch (resourceType) {
            case ResourceType.GET_BOOLEAN:
                args = "false";
                break;
            case ResourceType.GET_INT:
            case ResourceType.GET_FLOAT:
            case ResourceType.GET_COLOR:
            case ResourceType.GET_DIMEN:
                args = "0";
                break;
            case ResourceType.GET_BITMAP:
                codeBuilder.beginControlFlow("if(this.target.$N != null && !this.target.$N.isRecycled())",
                        name, name);
                codeBuilder.add("this.target.$N.recycle();\n", name);
                codeBuilder.endControlFlow();
            case ResourceType.GET_ARRAY:
            case ResourceType.GET_DRAWABLE:
            case ResourceType.GET_STRING:
            default:
                args = "null";
                break;
        }
        String code = String.format("this.target.$N = %s;\n", args);
        codeBuilder.add(code, name);
        return codeBuilder.build();
    }

    private static String getResourceArrayType(Element element) {
        TypeMirror typeMirror = element.asType();
        if (typeMirror.getKind() == TypeKind.ARRAY) {
            ArrayType arrayType = (ArrayType) typeMirror;
            TypeMirror componentType = arrayType.getComponentType();
            String component = componentType.toString();
            if (component.equals(String.class.getCanonicalName())) {
                return GET_STRING_ARRAY;
            } else if (component.equals(int.class.getCanonicalName())) {
                return GET_INT_ARRAY;
            } else if (component.equals(CharSequence.class.getCanonicalName())) {
                return GET_TEXT_ARRAY;
            }
        }
        throw new IllegalArgumentException("Array type not impl: " + typeMirror);
    }

    private static String getResourceDimenType(Element element) {
        TypeMirror typeMirror = element.asType();
        if (typeMirror.getKind() == TypeKind.INT) {
            return GET_INT_DIMEN;
        } else if (typeMirror.getKind() == TypeKind.FLOAT) {
            return GET_FLOAT_DIMEN;
        } else {
            throw new IllegalArgumentException("Dimen type not impl: " + typeMirror);
        }
    }
}
