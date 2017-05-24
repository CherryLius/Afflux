package com.cherry.afflux.compiler.model;

import com.cherry.afflux.compiler.log.Logger;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * Created by Administrator on 2017/4/27.
 */

public abstract class AnnotatedClass {
    /**
     *  ExecutableElement    表示某个类或接口的方法、构造方法或初始化程序（静态或实例），包括注释类型元素。
     *  PackageElement 表示一个包程序元素。
     *  TypeElement    表示一个类或接口程序元素。
     *  TypeParameterElement   表示一般类、接口、方法或构造方法元素的形式类型参数。
     *  VariableElement    表示一个字段、enum 常量、方法或构造方法参数、局部变量或异常参数
     */
    /**
     * 注解元素相关辅助类
     */
    private Elements mElementUtils;
    /**
     * 类名
     */
    private TypeElement mClassElement;
    /**
     * 类的构造函数
     */
    private List<ExecutableElement> mConstructorElementList;
    /**
     * 字段
     */
    private List<VariableElement> mFieldElementList;
    /**
     * 方法
     */
    private List<ExecutableElement> mMethodElementList;

    public AnnotatedClass(Elements elementUtils, TypeElement element) {
        mElementUtils = elementUtils;
        mClassElement = element;
        mConstructorElementList = new ArrayList<>();
        mFieldElementList = new ArrayList<>();
        mMethodElementList = new ArrayList<>();
        init();
    }

    private void init() {
        Logger.err("element %s", mClassElement.getKind());
        Logger.err("element simple name %s", mClassElement.getSimpleName());
        Logger.err("element enclosing %s", mClassElement.getEnclosingElement());
        Logger.err("element packageName %s", getFullClassName());
        Logger.err("element asType %s", mClassElement.asType());
        Logger.err("element TypeName %s", TypeName.get(mClassElement.asType()));
        List<? extends Element> childElementList = mClassElement.getEnclosedElements();

        for (int i = 0; i < childElementList.size(); i++) {
            Element childElement = childElementList.get(i);

            Logger.err("%d element %s kind %s", i, childElement.toString(), childElement.getKind().toString());

            if (childElement.getKind().isField()) {
                Logger.err("VariableElement %s", (childElement instanceof VariableElement));
                addField(childElement);
            } else if (childElement.getKind() == ElementKind.METHOD) {
                addMethod(childElement);
            } else if (childElement.getKind() == ElementKind.CONSTRUCTOR) {
                Logger.err("ExecutableElement %s", (childElement instanceof ExecutableElement));
                addConstructor(childElement);
            }
        }
    }

    public TypeElement getClassElement() {
        return mClassElement;
    }

    public List<VariableElement> getAllFieldElements() {
        return mFieldElementList;
    }

    public List<ExecutableElement> getAllMethodElements() {
        return mMethodElementList;
    }

    public List<ExecutableElement> getAllConstructorElements() {
        return mConstructorElementList;
    }

    public String getSimpleName() {
        return mClassElement.getSimpleName().toString();
    }

    public TypeName getTypeName() {
        return TypeName.get(mClassElement.asType());
    }

    public String getPackageName() {
        //return mClassElement.getEnclosingElement().toString();
        return mElementUtils.getPackageOf(mClassElement).getQualifiedName().toString();
    }

    public String getClassName() {
        String packageName = getPackageName();
        String fullClassName = getFullClassName();
        int packageLen = packageName.length() + 1;
        return fullClassName.substring(packageLen).replace(".", "$");
    }

    public String getFullClassName() {
        return mClassElement.getQualifiedName().toString();
    }

    private void addField(Element element) {
        mFieldElementList.add((VariableElement) element);
    }

    private void addMethod(Element element) {
        mMethodElementList.add((ExecutableElement) element);
    }

    private void addConstructor(Element element) {
        mConstructorElementList.add((ExecutableElement) element);
    }

    public abstract JavaFile generateFile();
}
