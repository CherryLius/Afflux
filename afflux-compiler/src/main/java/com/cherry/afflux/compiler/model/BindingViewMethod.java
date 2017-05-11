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
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
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
    private List<Parameter> mParameterList;

    private int[] mViewIds;

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

    public TypeName targetType() {
        return ClassName.bestGuess(mListenerClass.targetType());
    }

    public TypeSpec generateListener() {
        if (!checkInvalid()) return null;
        checkParameters();
        TypeSpec.Builder listener = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(ClassName.bestGuess(mListenerClass.type()));
        MethodSpec.Builder m = MethodSpec.methodBuilder(mListenerMethod.name())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(bestGuess(mListenerMethod.returnType()));

        //method params
        String[] params = mListenerMethod.parameters();
        for (int i = 0; i < params.length; i++) {
            m.addParameter(bestGuess(params[i]), ("arg" + i));
        }
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        boolean hasReturn = !"void".equals(mListenerMethod.returnType());
        if (hasReturn) {
            codeBuilder.add("return ");
        }
        codeBuilder.add("target.$N(", getSimpleName());
        for (int i = 0; i < mParameterList.size(); i++) {
            Parameter parameter = mParameterList.get(i);
            int listenerPosition = parameter.getPosition();
            if (i > 0) {
                codeBuilder.add(", ");
            }
            codeBuilder.add("arg$L", listenerPosition);
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
        if (!checkParameters()) {
            return false;
        }
        return true;
    }

    private boolean checkParameters() {
        String[] listenerParams = mListenerMethod.parameters();
        Parameter[] parameterArray = new Parameter[mMethodParams.size()];
        BitSet bitSet = new BitSet(mMethodParams.size());
        for (int i = 0; i < mMethodParams.size(); i++) {
            VariableElement param = mMethodParams.get(i);
            TypeMirror paramType = param.asType();
            if (paramType instanceof TypeVariable) {
                TypeVariable typeVariable = (TypeVariable) paramType;
                paramType = typeVariable.getUpperBound();
            }
            Logger.out("method %s, %s", param, paramType);
            for (int j = 0; j < listenerParams.length; j++) {
                if (bitSet.get(j))
                    continue;
                String listenerParam = listenerParams[j];
                Logger.out("listener method param %s", listenerParam);
                Logger.err("sub type %s", isSubTypeOfType(paramType, listenerParam));
                if (isTypeEquals(paramType, listenerParam)
                        || isInterface(paramType)) {
                    parameterArray[i] = new Parameter(j, TypeName.get(paramType));
                    bitSet.set(j);
                }
            }
            //check
            Logger.out("param[%d] %s", i, parameterArray[i]);
            if (parameterArray[i] == null) {
                TypeElement enclosingElement = (TypeElement) mExecutableElement.getEnclosingElement();
                StringBuilder builder = new StringBuilder();
                builder.append("Unable match @")
                        .append(mAnnotationClass.getSimpleName())
                        .append(" method arguments. (")
                        .append(enclosingElement.getQualifiedName())
                        .append('.')
                        .append(getSimpleName())
                        .append(')');
                for (int j = 0; j < parameterArray.length; j++) {
                    Parameter p = parameterArray[j];
                    builder.append("\n  Parameter #")
                            .append(j + 1)
                            .append(": ")
                            .append(mMethodParams.get(j).asType().toString())
                            .append("\n ");
                    if (p == null)
                        builder.append("didn't match any listener parameters");
                    else
                        builder.append("matched listener parameter #")
                                .append(p.getPosition() + 1)
                                .append(": ")
                                .append(p.getType());
                }
                builder.append("\nMethods may have up to ")
                        .append(mListenerMethod.parameters().length)
                        .append(" parameter(s):\n");
                for (String parameterType : mListenerMethod.parameters()) {
                    builder.append(parameterType).append("\n  ");
                }
                builder.append(
                        "These may be listed in any order but will be searched for from top to bottom.");
                Logger.instance().error(mExecutableElement, builder.toString());
                return false;
            }
        }
        mParameterList = Arrays.asList(parameterArray);
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

    private static boolean isTypeEquals(TypeMirror typeMirror, String otherType) {
        return otherType.equals(typeMirror.toString());
    }

    private static boolean isInterface(TypeMirror typeMirror) {
        if (typeMirror instanceof DeclaredType) {
            DeclaredType type = (DeclaredType) typeMirror;
            return type.asElement().getKind() == ElementKind.INTERFACE;
        }
        return false;
    }

    private static boolean isSubTypeOfType(TypeMirror typeMirror, String otherType) {
        Logger.out("isSubTypeOfType %s , %s", typeMirror, otherType);
        if (isTypeEquals(typeMirror, otherType))
            return true;
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return false;
        }
        DeclaredType declaredType = (DeclaredType) typeMirror;
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if (typeArguments.size() > 0) {
            StringBuilder builder = new StringBuilder(declaredType.asElement().toString());
            builder.append('<');
            for (int i = 0; i < typeArguments.size(); i++) {
                if (i > 0) {
                    builder.append(',');
                }
                builder.append('?');
            }
            builder.append('>');
            Logger.out("builder %s", builder.toString());
            if (builder.toString().equals(otherType))
                return true;
        }
        Element element = declaredType.asElement();
        if (!(element instanceof TypeElement)) {
            return false;
        }
        TypeElement typeElement = (TypeElement) element;
        TypeMirror superType = typeElement.getSuperclass();
        Logger.err("element %s, %s %s %s", element, declaredType, typeElement, superType);
        if (isSubTypeOfType(superType, otherType)) {
            return true;
        }
        for (TypeMirror interfaceType : typeElement.getInterfaces()) {
            if (isSubTypeOfType(interfaceType, otherType)) {
                return true;
            }
        }
        //listener method params type
        return false;
    }
}
