package com.cherry.afflux.compiler.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by Administrator on 2017/4/27.
 */

public class ParcelableClass extends AnnotatedClass {

    private static final String METHOD_DESCRIBE_CONTENTS = "describeContents";

    private static final String ANDROID_OS = "android.os";
    private static final String PARCELABLE = "Parcelable";

    public ParcelableClass(TypeElement element) {
        super(element);
    }

    @Override
    public JavaFile generateFile() {
        ClassName parcelable = ClassName.get(ANDROID_OS, PARCELABLE);
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(mClassElement.getSimpleName().toString())
                .addSuperinterface(ParameterizedTypeName.get(parcelable));
        typeSpecBuilder.addMethod(buildDescribeContentsMethod());
        return JavaFile.builder(getPackageName(), typeSpecBuilder.build()).build();
    }

    private MethodSpec buildDescribeContentsMethod() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(METHOD_DESCRIBE_CONTENTS)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("return $L", 0)
                .returns(TypeName.INT);
        return methodBuilder.build();
    }

    /**
     * @Override public void writeToParcel(Parcel dest, int flags) {
     * dest.writeInt(id);
     * dest.writeString(name);
     * }
     */
    private MethodSpec buildWriteToParcelMethod() {
        ClassName parcel = ClassName.get("android.os", "Parcel");
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("writeToParcel")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(parcel, "dest")
                .addParameter(TypeName.INT, "flags")
                .returns(TypeName.VOID);
        return methodBuilder.build();
    }
}