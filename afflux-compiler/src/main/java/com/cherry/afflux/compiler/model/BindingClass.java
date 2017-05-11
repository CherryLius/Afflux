package com.cherry.afflux.compiler.model;

import com.cherry.afflux.compiler.common.Method;
import com.cherry.afflux.compiler.common.Type;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by Administrator on 2017/5/2.
 */

public class BindingClass extends AnnotatedClass {

    private List<BindingViewField> mBindingFieldLists;
    private List<BindingViewMethod> mBindingMethodLists;

    private Map<Integer, FieldBinding> mListenerFieldMap = new HashMap<>();
    private Map<Integer, String> mTargetFieldMap = new HashMap<>();

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
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(getClassName() + "_Binder")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(Type.UNBINDER)
                .addField(getTypeName(), "target");
        //field id and listener id compare
        bindFieldMethod();
        // add field in bindingView
        addListenerField(typeBuilder);

        typeBuilder.addMethod(buildConstructorMethod())
                .addMethod(buildUnbindMethod());
        return JavaFile.builder(getPackageName(), typeBuilder.build()).build();
    }

    private MethodSpec buildConstructorMethod() {
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(getTypeName(), "target", Modifier.FINAL)
                .addParameter(Type.VIEW, "source")
                .addStatement("this.target = target");

        // init view
        for (BindingViewField field : mBindingFieldLists) {
            constructor.addStatement("target.$N = ($T)source.findViewById($L)",
                    field.getSimpleName(),
                    field.getTypeName(),
                    field.getViewId());
        }

        for (Map.Entry<Integer, FieldBinding> entry : mListenerFieldMap.entrySet()) {
            constructor.addStatement("this.$N = ($T)source.findViewById($L)",
                    entry.getValue().getName(),
                    entry.getValue().getTypeName(),
                    entry.getKey());
        }

        //set listener
        for (BindingViewMethod method : mBindingMethodLists) {
            TypeSpec listener = method.generateListener();
            if (listener == null)
                break;
            int[] viewIds = method.getViewIds();
            for (int viewId : viewIds) {
                String fieldName = getTargetFieldName(viewId);
                if (fieldName != null) {
                    constructor.addStatement("target.$N.$N($L)",
                            fieldName,
                            method.setter(),
                            listener);
                } else {
                    fieldName = getBindingFieldName(viewId).getName();
                    constructor.addStatement("this.$N.$N($L)",
                            fieldName,
                            method.setter(),
                            listener);
                }
            }
        }
        return constructor.build();
    }

    private void addListenerField(TypeSpec.Builder typeBuilder) {
        for (FieldBinding field : mListenerFieldMap.values()) {
            typeBuilder.addField(field.getTypeName(), field.getName());
        }
    }

    private MethodSpec buildUnbindMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder(Method.UNBIND)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID);
        for (BindingViewMethod m : mBindingMethodLists) {
            int[] ids = m.getViewIds();
            for (int viewId : ids) {
                String fieldName = getTargetFieldName(viewId);
                if (fieldName != null) {
                    method.addStatement("this.target.$N.$N(null)",
                            fieldName,
                            m.setter());
                } else {
                    fieldName = getBindingFieldName(viewId).getName();
                    method.addStatement("this.$N.$N(null)",
                            fieldName,
                            m.setter());
                }
            }
        }
        for (BindingViewField field : mBindingFieldLists) {
            method.addStatement("this.target.$N = null",
                    field.getSimpleName());
        }

        for (FieldBinding field : mListenerFieldMap.values()) {
            method.addStatement("this.$N = null", field.getName());
        }
        method.addStatement("this.target = null");
        return method.build();
    }

    private void bindFieldMethod() {
        for (BindingViewField field : mBindingFieldLists) {
            int id = field.getViewId();
            for (BindingViewMethod method : mBindingMethodLists) {
                int[] ids = method.getViewIds();
                for (int viewId : ids) {
                    if (id == viewId) {
                        putTargetFieldName(viewId, field.getSimpleName().toString());
                    } else {
                        String fieldName = String.format("view%d", viewId);
                        putBindingFieldName(viewId, fieldName, method.targetType());
                    }
                }
            }
        }
    }

    private void putBindingFieldName(int viewId, String fieldName, TypeName fieldType) {
        if (!mListenerFieldMap.containsKey(viewId)) {
            FieldBinding field = new FieldBinding(fieldName, fieldType);
            mListenerFieldMap.put(viewId, field);
        }
    }

    private FieldBinding getBindingFieldName(int viewId) {
        return mListenerFieldMap.get(viewId);
    }

    private void putTargetFieldName(int viewId, String fieldName) {
        if (!mTargetFieldMap.containsKey(viewId)) {
            mTargetFieldMap.put(viewId, fieldName);
        }
    }

    private String getTargetFieldName(int viewId) {
        return mTargetFieldMap.get(viewId);
    }


}
