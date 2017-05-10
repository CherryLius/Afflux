package com.cherry.afflux.compiler.model;

import com.cherry.afflux.compiler.common.Method;
import com.cherry.afflux.compiler.common.Type;
import com.cherry.afflux.compiler.log.Logger;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by Administrator on 2017/5/2.
 */

public class BindingClass extends AnnotatedClass {

    private List<BindingViewField> mBindingFieldLists;
    private List<BindingViewMethod> mBindingMethodLists;

    public BindingClass(Elements elementUtils, TypeElement element) {
        super(elementUtils, element);
        mBindingFieldLists = new ArrayList<>();
        mBindingMethodLists = new ArrayList<>();
    }

    public void addBindingViewField(BindingViewField field) {
        mBindingFieldLists.add(field);
    }

    public void addBindingViewMethod(BindingViewMethod method) {
        mBindingMethodLists.add(method);
    }

    @Override
    public JavaFile generateFile() {
        bindFieldMethod();
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(getClassName() + "_Binder")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(Type.UNBINDER)
                .addField(getTypeName(), "target")
                .addMethod(buildConstructorMethod())
                .addMethod(buildUnbindMethod());
        return JavaFile.builder(getPackageName(), typeBuilder.build()).build();
    }

    private MethodSpec buildConstructorMethod() {
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(getTypeName(), "target")
                .addParameter(Type.VIEW, "source")
                .addStatement("this.target = target");
        for (BindingViewField field : mBindingFieldLists) {
            constructor.addStatement("target.$N = ($T)source.findViewById($L)",
                    field.getSimpleName(),
                    field.getTypeName(),
                    field.getViewId());
        }
        for (BindingViewMethod method : mBindingMethodLists) {
            if (method.getFieldName() != null) {
                TypeSpec listener = method.generateListener();
                constructor.addStatement("target.$N.$N($L)",
                        method.getFieldName(),
                        method.setter(),
                        listener);
            }
        }
        return constructor.build();
    }

    private MethodSpec buildUnbindMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder(Method.UNBIND)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID);
        for (BindingViewField field : mBindingFieldLists) {
            method.addStatement("this.target.$N = null",
                    field.getSimpleName());
        }
        method.addStatement("this.target = null");
        return method.build();
    }

    private void bindFieldMethod() {
        for (BindingViewField field : mBindingFieldLists) {
            int id = field.getViewId();
            for (BindingViewMethod method : mBindingMethodLists) {
                if (id == method.getViewId()) {
                    method.setFieldName(field.getSimpleName());
                    break;
                }
            }
        }
    }
}
