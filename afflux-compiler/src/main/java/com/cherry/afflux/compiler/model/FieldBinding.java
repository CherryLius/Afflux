package com.cherry.afflux.compiler.model;

import com.squareup.javapoet.TypeName;

/**
 * Created by Administrator on 2017/5/11.
 */

public class FieldBinding {
    private String name;
    private TypeName typeName;

    public FieldBinding(String name, TypeName typeName) {
        this.name = name;
        this.typeName = typeName;
    }

    public String getName() {
        return name;
    }

    public TypeName getTypeName() {
        return typeName;
    }
}
