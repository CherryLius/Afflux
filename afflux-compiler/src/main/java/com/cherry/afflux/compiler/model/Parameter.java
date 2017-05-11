package com.cherry.afflux.compiler.model;

import com.squareup.javapoet.TypeName;

/**
 * Created by Administrator on 2017/5/11.
 */

public class Parameter {
    private int paramPosition;
    private TypeName typeName;

    public Parameter(int paramPosition, TypeName typeName) {
        this.paramPosition = paramPosition;
        this.typeName = typeName;
    }

    public int getPosition() {
        return paramPosition;
    }

    public TypeName getType() {
        return typeName;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "paramPosition=" + paramPosition +
                ", typeName=" + typeName +
                '}';
    }
}
