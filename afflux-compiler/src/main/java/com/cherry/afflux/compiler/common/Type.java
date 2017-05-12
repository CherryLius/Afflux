package com.cherry.afflux.compiler.common;

import com.cherry.afflux.compiler.log.Logger;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/28.
 */

public interface Type {
    ClassName PARCELABLE = ClassName.get("android.os", "Parcelable");
    ClassName PARCEL = ClassName.get("android.os", "Parcel");
    ClassName CREATOR = ClassName.get("android.os", "Parcelable", "Creator");
    ClassName UNBINDER = ClassName.get("com.cherry.afflux.api", "Unbinder");
    ClassName VIEW = ClassName.get("android.view", "View");
    ClassName AFFLUX_UTILS = ClassName.get("com.cherry.afflux.api.internal", "Utils");

    static TypeName bestGuess(String type) {
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
