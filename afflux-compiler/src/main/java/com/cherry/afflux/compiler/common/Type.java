package com.cherry.afflux.compiler.common;

import com.squareup.javapoet.ClassName;

/**
 * Created by Administrator on 2017/4/28.
 */

public interface Type {
    ClassName PARCELABLE = ClassName.get("android.os", "Parcelable");
    ClassName PARCEL = ClassName.get("android.os", "Parcel");
    ClassName CREATOR = ClassName.get("android.os", "Parcelable", "Creator");
}
