package com.cherry.afflux.compiler.util;

import com.cherry.afflux.compiler.log.Logger;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Created by LHEE on 2017/5/12.
 */

public class Utils {
    public static boolean isTypeEquals(TypeMirror typeMirror, String otherType) {
        return otherType.equals(typeMirror.toString());
    }

    public static boolean isInterface(TypeMirror typeMirror) {
        if (typeMirror instanceof DeclaredType) {
            DeclaredType type = (DeclaredType) typeMirror;
            return type.asElement().getKind() == ElementKind.INTERFACE;
        }
        return false;
    }

    public static boolean isSubTypeOfType(TypeMirror typeMirror, String otherType) {
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
        //Logger.err("element %s, %s %s %s", element, declaredType, typeElement, superType);
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

    public static TypeName bestGuess(String type) {
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
                int index = type.indexOf('<');
                if (index != -1) {
                    String typeName = type.substring(0, index);
                    Logger.out("typeName=%s, %s", typeName, WildcardTypeName.subtypeOf(Object.class));
                    List<TypeName> typeArguments = new ArrayList<>();
                    do {
                        //通配符
                        typeArguments.add(WildcardTypeName.subtypeOf(Object.class));
                        index = type.indexOf('<', index + 1);
                    } while (index != -1);
                    ClassName typeClassName = ClassName.bestGuess(typeName);
                    return ParameterizedTypeName.get(typeClassName,
                            typeArguments.toArray(new TypeName[typeArguments.size()]));
                }
                return ClassName.bestGuess(type);
        }
    }
}
