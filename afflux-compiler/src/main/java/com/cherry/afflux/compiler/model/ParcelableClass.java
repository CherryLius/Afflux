package com.cherry.afflux.compiler.model;

import com.cherry.afflux.compiler.common.Method;
import com.cherry.afflux.compiler.common.Type;
import com.cherry.afflux.compiler.log.Logger;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;

/**
 * Created by Administrator on 2017/4/27.
 */

public class ParcelableClass extends AnnotatedClass {

    public ParcelableClass(Elements elementUtils, TypeElement element) {
        super(elementUtils, element);
    }

    @Override
    public JavaFile generateFile() {
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(getClassName() + "_debug")
                .addSuperinterface(Type.PARCELABLE)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(buildConstructorMethod())
                .addMethod(buildDescribeContentsMethod())
                .addMethod(buildWriteToParcelMethod())
                .addField(buildCREATORField());

        //add original field
        //java 8
        getAllFieldElements().stream().forEach(variableElement -> {
            TypeName fieldType = TypeName.get(variableElement.asType());
            typeSpecBuilder.addField(fieldType,
                    variableElement.getSimpleName().toString(),
                    variableElement.getModifiers().toArray(new Modifier[]{}));

        });
//        List<VariableElement> fieldElementList = getAllFieldElements();
//        for (int i = 0; i < fieldElementList.size(); i++) {
//            VariableElement field = fieldElementList.get(i);
//            TypeName fieldType = TypeName.get(field.asType());
//            typeSpecBuilder.addField(fieldType, field.getSimpleName().toString(), field.getModifiers().toArray(new Modifier[]{}));
//        }
        //add original method
        List<ExecutableElement> methodElementList = getAllMethodElements();
        for (int i = 0; i < methodElementList.size(); i++) {
            ExecutableElement method = methodElementList.get(i);
            //typeSpecBuilder.addMethod(executableToMethod(method));
        }

        //add original constructor
        List<ExecutableElement> constructorElementList = getAllConstructorElements();
        for (int i = 0; i < constructorElementList.size(); i++) {
            ExecutableElement constructor = constructorElementList.get(i);
        }


        TypeSpec typeSpec = typeSpecBuilder.build();
        return JavaFile.builder(getPackageName(), typeSpec).build();
    }

    static List<ParameterSpec> parametersOf(ExecutableElement method) {
        List<ParameterSpec> result = new ArrayList<>();
        for (VariableElement parameter : method.getParameters()) {
            Logger.err("params %s", parameter);
            result.add(ParameterSpec.get(parameter));
        }
        return result;
    }

    private MethodSpec executableToMethod(ExecutableElement method) {
        String methodName = method.getSimpleName().toString();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName)
                .addModifiers(method.getModifiers());

        //泛型
        for (TypeParameterElement typeParameterElement : method.getTypeParameters()) {
            TypeVariable var = (TypeVariable) typeParameterElement.asType();
            Logger.err("typeParameterElement %s %s", typeParameterElement);
            methodBuilder.addTypeVariable(TypeVariableName.get(var));
        }
        methodBuilder.returns(TypeName.get(method.getReturnType()));
        methodBuilder.addParameters(parametersOf(method));
        methodBuilder.varargs(method.isVarArgs());
        Logger.err("varargs %s ", method.isVarArgs());

        for (TypeMirror thrownType : method.getThrownTypes()) {
            methodBuilder.addException(TypeName.get(thrownType));
        }

        return methodBuilder.build();
    }

    private MethodSpec buildConstructorMethod() {
        MethodSpec.Builder method = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Type.PARCEL, "in");
        List<VariableElement> fieldElementList = getAllFieldElements();
        for (int i = 0; i < fieldElementList.size(); i++) {
            VariableElement field = fieldElementList.get(i);
            TypeName typeName = TypeName.get(field.asType());
            String statement = null;
            if (typeName.equals(TypeName.INT)) {
                statement = "this.$N = in.readInt()";
            } else if (typeName.equals(ClassName.get(String.class))) {
                statement = "this.$N = in.readString()";
            } else if (typeName.equals(TypeName.BOOLEAN)) {
                statement = "this.$N = in.readByte() != 0";
            } else if (typeName.equals(TypeName.BYTE)) {
                statement = "this.$N = in.readByte()";
            } else if (typeName.equals(TypeName.FLOAT)) {
                statement = "this.$N = in.readFloat()";
            } else if (typeName.equals(TypeName.DOUBLE)) {
                statement = "this.$N = in.readDouble()";
            } else if (typeName.equals(TypeName.LONG)) {
                statement = "this.$N = in.readLong()";
            } else if (typeName.equals(TypeName.SHORT)
                    || typeName.equals(TypeName.CHAR)
                    || typeName.equals(TypeName.OBJECT)) {
                //no support in Parcel
            }
            if (statement != null) {
                method.addStatement(statement, field.getSimpleName().toString());
            }
        }
        return method.build();
    }

    private MethodSpec buildDescribeContentsMethod() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Method.DESCRIBE_CONTENTS)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("return $L", 0)
                .returns(TypeName.INT);
        return methodBuilder.build();
    }

    private MethodSpec buildWriteToParcelMethod() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Method.WRITE_TO_PARCEL)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Type.PARCEL, "dest")
                .addParameter(TypeName.INT, "flags")
                .returns(TypeName.VOID);
        // add statement
        List<VariableElement> fieldElementList = getAllFieldElements();
        for (int i = 0; i < fieldElementList.size(); i++) {
            VariableElement field = fieldElementList.get(i);
            TypeName typeName = TypeName.get(field.asType());
            // $L $T $N $S
            String statement = null;
            if (typeName.equals(TypeName.INT)) {
                statement = "dest.writeInt($N)";
            } else if (typeName.equals(ClassName.get(String.class))) {
                statement = "dest.writeString($N)";
            } else if (typeName.equals(TypeName.BOOLEAN)) {
                statement = "dest.writeByte((byte) ($N ? 1 : 0))";
            } else if (typeName.equals(TypeName.BYTE)) {
                statement = "dest.writeByte($N)";
            } else if (typeName.equals(TypeName.FLOAT)) {
                statement = "dest.writeFloat($N)";
            } else if (typeName.equals(TypeName.DOUBLE)) {
                statement = "dest.writeDouble($N)";
            } else if (typeName.equals(TypeName.LONG)) {
                statement = "dest.writeLong($N)";
            } else if (typeName.equals(TypeName.SHORT)
                    || typeName.equals(TypeName.CHAR)
                    || typeName.equals(TypeName.OBJECT)) {
                //no support in Parcel
            }
            if (statement != null) {
                methodBuilder.addStatement(statement, field.getSimpleName().toString());
            }
            //Logger.err("typeName: %s  %s)", typeName, typeName.equals(ClassName.get(String.class)));
        }
        return methodBuilder.build();
    }

    /**
     * static final field init with anonymousClass
     *
     * @return
     */
    private FieldSpec buildCREATORField() {
        ParameterizedTypeName typeName = ParameterizedTypeName.get(Type.CREATOR, getTypeNameDebug());
        TypeSpec.Builder typeBuilder = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(typeName)
                .addMethod(buildCreateFromParcelMethod())
                .addMethod(buildNewArrayMethod());
        FieldSpec.Builder field = FieldSpec.builder(typeName, "CREATOR")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", typeBuilder.build());
        return field.build();
    }

    private MethodSpec buildCreateFromParcelMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder(Method.CREATE_FROM_PARCEL)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(Type.PARCEL, "in")
                .addStatement("return new $T(in)", getTypeNameDebug())
                .returns(getTypeNameDebug());
        return method.build();
    }

    private MethodSpec buildNewArrayMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder(Method.NEW_ARRAY)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(int.class, "size")
                .addStatement("return new $T[size]", getTypeNameDebug())
                .returns(ArrayTypeName.of(getTypeNameDebug()));
        return method.build();
    }

    private TypeName getTypeNameDebug() {
        return ClassName.get(getPackageName(), getClassName() + "_debug");
    }
}