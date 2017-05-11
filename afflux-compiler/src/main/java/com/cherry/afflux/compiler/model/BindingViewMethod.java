package com.cherry.afflux.compiler.model;

import com.cherry.afflux.annotation.internal.ListenerClass;
import com.cherry.afflux.annotation.internal.ListenerMethod;
import com.cherry.afflux.compiler.log.Logger;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;

/**
 * Created by Administrator on 2017/5/2.
 */

public class BindingViewMethod {
    private ExecutableElement mExecutableElement;
    private List<? extends VariableElement> mMethodParams;
    private ListenerClass mListenerClass;
    private ListenerMethod mListenerMethod;
    private Class<? extends Annotation> mAnnotationClass;

    private int[] mViewIds;
    private Map<Integer, String> mFieldNameMap;

    public BindingViewMethod(Element element, Class<? extends Annotation> annotationClass) {
        if (element.getKind() != ElementKind.METHOD)
            throw new IllegalStateException("must be method!");
        try {
            mExecutableElement = (ExecutableElement) element;
            mAnnotationClass = annotationClass;
            //method params
            mMethodParams = mExecutableElement.getParameters();
            // view id
            Annotation annotation = mExecutableElement.getAnnotation(annotationClass);
            Method method = annotationClass.getDeclaredMethod("value");
            mViewIds = (int[]) method.invoke(annotation);
            mFieldNameMap = new HashMap<>(mViewIds.length);
            //listener class info
            mListenerClass = annotationClass.getAnnotation(ListenerClass.class);
            mListenerMethod = mListenerClass.method();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSimpleName() {
        return mExecutableElement.getSimpleName().toString();
    }

    public int[] getViewIds() {
        return mViewIds;
    }

    public String setter() {
        return mListenerClass.setter();
    }

    public TypeSpec generateListener() {
        if (!checkInvalid()) return null;
        TypeSpec.Builder listener = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(ClassName.bestGuess(mListenerClass.type()));
        MethodSpec.Builder m = MethodSpec.methodBuilder(mListenerMethod.name())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(bestGuess(mListenerMethod.returnType()));

        //method params
        String[] params = mListenerMethod.parameters();
        for (int i = 0; i < params.length; i++) {
            m.addParameter(ClassName.bestGuess(params[i]), ("arg" + i));
        }
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        boolean hasReturn = !"void".equals(mListenerMethod.returnType());
        if (hasReturn) {
            codeBuilder.add("return ");
        }
        codeBuilder.add("target.$N(", getSimpleName());
        for (int i = 0; i < mMethodParams.size(); i++) {
            if (i > 0) {
                codeBuilder.add(", ");
            }
            codeBuilder.add("arg$L", i);
        }
        codeBuilder.add(");\n");
        m.addCode(codeBuilder.build());
        listener.addMethod(m.build());
        return listener.build();
    }

    private boolean checkInvalid() {
        TypeElement enclosingElement = (TypeElement) mExecutableElement.getEnclosingElement();
        if (mMethodParams.size() > mListenerMethod.parameters().length) {
            Logger.instance().error(mExecutableElement, "@%s methods can have most %s parameter(s). (%s.%s)",
                    mAnnotationClass.getSimpleName(),
                    mListenerMethod.parameters().length,
                    enclosingElement.getQualifiedName().toString(),
                    getSimpleName());
            return false;
        }
        TypeMirror returnType = mExecutableElement.getReturnType();
        if (returnType instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) returnType;
            returnType = typeVariable.getUpperBound();
            Logger.out("typeVariable %s, %s", typeVariable, returnType);
        }
        if (!returnType.toString().equals(mListenerMethod.returnType())) {
            Logger.instance().error(mExecutableElement, "@%s methods must return %s. (%s.%s)",
                    mAnnotationClass.getSimpleName(),
                    mListenerMethod.returnType(),
                    enclosingElement.getQualifiedName().toString(),
                    getSimpleName());
            return false;
        }
        return true;
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
