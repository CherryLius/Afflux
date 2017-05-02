package com.cherry.afflux.compiler.model;

import com.cherry.afflux.annotation.BindView;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

/**
 * Created by Administrator on 2017/5/2.
 */

public class BindingViewField {
    private VariableElement mFieldElement;
    private int mViewId;

    public BindingViewField(Element element) {
        if (!element.getKind().isField())
            throw new IllegalStateException(
                    String.format("Only field can be annotated with @%s", BindView.class.getSimpleName()));
        mFieldElement = (VariableElement) element;
        mViewId = mFieldElement.getAnnotation(BindView.class).value();
    }

    public int getViewId() {
        return mViewId;
    }

    public TypeName getTypeName() {
        return TypeName.get(mFieldElement.asType());
    }

    public String getSimpleName() {
        return mFieldElement.getSimpleName().toString();
    }
}
