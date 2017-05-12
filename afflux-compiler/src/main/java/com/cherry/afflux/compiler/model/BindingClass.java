package com.cherry.afflux.compiler.model;

import com.cherry.afflux.annotation.internal.ListenerClass;
import com.cherry.afflux.compiler.common.Method;
import com.cherry.afflux.compiler.common.Type;
import com.squareup.javapoet.ClassName;
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
    //private List<BindingViewMethod> mBindingMethodLists;

    private Map<Integer, FieldBinding> mListenerFieldMap = new HashMap<>();
    private Map<Integer, MethodBinding> mListenerMethodMap = new HashMap<>();

    public BindingClass(Elements elementUtils, TypeElement element) {
        super(elementUtils, element);
        mBindingFieldLists = new ArrayList<>();
    }

    public void addBindingViewField(BindingViewField field) {
        mBindingFieldLists.add(field);
    }

    public void addBindingViewMethod(BindingViewMethod method) {
        for (int id : method.getViewIds()) {
            MethodBinding binding = mListenerMethodMap.get(id);
            if (binding == null) {
                binding = new MethodBinding(id);
                binding.addMethod(method);
                mListenerMethodMap.put(id, binding);
            } else {
                binding.addMethod(method);
            }
        }
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
        for (MethodBinding binding : mListenerMethodMap.values())
            for (Map.Entry<ListenerClass, List<BindingViewMethod>> entry : binding.getMethodMap().entrySet()) {
                int viewId = binding.getViewId();
                TypeSpec listener = binding.generateListener(entry.getKey(), entry.getValue());
                TypeName keyType = Type.bestGuess(entry.getKey().targetType());
                boolean requireRemove = !"".equals(entry.getKey().remover());
                BindingViewField field = getBindingViewFiled(viewId);

                //remover type
                TypeName listenerType = Type.bestGuess(entry.getKey().type());
                String listenerField = String.format("view%d%s", viewId, ((ClassName) listenerType).simpleName());
                if (requireRemove) {
                    constructor.addStatement("this.$N = $L",
                            listenerField,
                            listener);
                }
                if (field != null) {
                    String fieldName = field.getSimpleName();
                    if (field.getTypeName().equals(keyType)) {
                        if (requireRemove) {
                            constructor.addStatement("target.$N.$N($N)",
                                    fieldName,
                                    entry.getKey().setter(),
                                    listenerField);
                        } else {
                            constructor.addStatement("target.$N.$N($L)",
                                    fieldName,
                                    entry.getKey().setter(),
                                    listener);
                        }
                    } else {
                        if (requireRemove) {
                            constructor.addStatement("(($T)target.$N).$N($N)",
                                    keyType,
                                    fieldName,
                                    entry.getKey().setter(),
                                    listenerField);
                        } else {
                            constructor.addStatement("(($T)target.$N).$N($L)",
                                    keyType,
                                    fieldName,
                                    entry.getKey().setter(),
                                    listener);
                        }
                    }
                } else {
                    String fieldName = getBindingFieldName(viewId).getName();
                    if (Type.VIEW.equals(keyType)) {
                        if (requireRemove) {
                            constructor.addStatement("this.$N.$N($N)",
                                    fieldName,
                                    entry.getKey().setter(),
                                    listenerField);
                        } else {
                            constructor.addStatement("this.$N.$N($L)",
                                    fieldName,
                                    entry.getKey().setter(),
                                    listener);
                        }
                    } else {
                        if (requireRemove) {
                            constructor.addStatement("(($T)this.$N).$N($N)",
                                    keyType,
                                    fieldName,
                                    entry.getKey().setter(),
                                    listenerField);
                        } else {
                            constructor.addStatement("(($T)this.$N).$N($L)",
                                    keyType,
                                    fieldName,
                                    entry.getKey().setter(),
                                    listener);
                        }
                    }
                }
            }
        return constructor.build();
    }

    private void addListenerField(TypeSpec.Builder typeBuilder) {
        for (FieldBinding field : mListenerFieldMap.values()) {
            typeBuilder.addField(field.getTypeName(), field.getName());
        }

        for (MethodBinding binding : mListenerMethodMap.values()) {
            int id = binding.getViewId();
            for (ListenerClass listenerClass : binding.getMethodMap().keySet()) {
                boolean requireRemover = !"".equals(listenerClass.remover());
                if (requireRemover) {
                    TypeName typeName = Type.bestGuess(listenerClass.type());
                    String fieldName = String.format("view%d%s", id, ((ClassName) typeName).simpleName());
                    typeBuilder.addField(typeName, fieldName, Modifier.PRIVATE);
                }
            }
        }

    }

    private MethodSpec buildUnbindMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder(Method.UNBIND)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID);
        for (MethodBinding binding : mListenerMethodMap.values()) {
            int viewId = binding.getViewId();
            for (ListenerClass listener : binding.getMethodMap().keySet()) {
                TypeName keyType = Type.bestGuess(listener.targetType());
                BindingViewField field = getBindingViewFiled(viewId);

                boolean requireRemover = !"".equals(listener.remover());
                TypeName typeName = Type.bestGuess(listener.type());
                String listenerField = String.format("view%d%s", viewId, ((ClassName) typeName).simpleName());

                if (field != null) {
                    String fieldName = field.getSimpleName();
                    if (field.getTypeName().equals(keyType)) {
                        if (requireRemover) {
                            method.addStatement("this.target.$N.$N($N)",
                                    fieldName,
                                    listener.remover(),
                                    listenerField);
                            method.addStatement("this.$N = null", listenerField);
                        } else {
                            method.addStatement("this.target.$N.$N(null)",
                                    fieldName,
                                    listener.setter());
                        }
                    } else {
                        if (requireRemover) {
                            method.addStatement("($T)this.target.$N).$N($N)",
                                    keyType,
                                    fieldName,
                                    listener.remover(),
                                    listenerField);
                            method.addStatement("this.$N = null", listenerField);
                        } else {
                            method.addStatement("(($T)this.target.$N).$N(null)",
                                    keyType,
                                    fieldName,
                                    listener.setter());
                        }
                    }
                } else {
                    String fieldName = getBindingFieldName(viewId).getName();
                    if (Type.VIEW.equals(keyType)) {
                        if (requireRemover) {
                            method.addStatement("this.$N.$N($N)",
                                    fieldName,
                                    listener.remover(),
                                    listenerField);
                            method.addStatement("this.$N = null", listenerField);
                        } else {
                            method.addStatement("this.$N.$N(null)",
                                    fieldName,
                                    listener.setter());
                        }
                    } else {
                        if (requireRemover) {
                            method.addStatement("(($T)this.$N).$N($N)",
                                    keyType,
                                    fieldName,
                                    listener.remover(),
                                    listenerField);
                            method.addStatement("this.$N = null", listenerField);
                        } else {
                            method.addStatement("(($T)this.$N).$N(null)",
                                    keyType,
                                    fieldName,
                                    listener.setter());
                        }
                    }
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
            for (int viewId : mListenerMethodMap.keySet()) {
                if (id != viewId) {
                    String fieldName = String.format("view%d", viewId);
                    putBindingFieldName(viewId, fieldName);
                }
            }
        }
    }

    private void putBindingFieldName(int viewId, String fieldName) {
        if (!mListenerFieldMap.containsKey(viewId)) {
            FieldBinding field = new FieldBinding(fieldName, Type.VIEW);
            mListenerFieldMap.put(viewId, field);
        }
    }

    private FieldBinding getBindingFieldName(int viewId) {
        return mListenerFieldMap.get(viewId);
    }

    private BindingViewField getBindingViewFiled(int viewId) {
        for (int i = 0; i < mBindingFieldLists.size(); i++) {
            BindingViewField field = mBindingFieldLists.get(i);
            if (viewId == field.getViewId()) {
                return field;
            }
        }
        return null;
    }

}
