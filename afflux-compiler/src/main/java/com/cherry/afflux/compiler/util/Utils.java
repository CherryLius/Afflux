package com.cherry.afflux.compiler.util;

import com.cherry.afflux.compiler.log.Logger;

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
}
