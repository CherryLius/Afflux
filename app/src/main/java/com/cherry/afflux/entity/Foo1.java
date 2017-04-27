package com.cherry.afflux.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/4/27.
 */

public class Foo1 implements Parcelable{
    private int id;
    private String name;

    public Foo1() {
    }

    public Foo1(int i) {
    }

    protected Foo1(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<Foo1> CREATOR = new Creator<Foo1>() {
        @Override
        public Foo1 createFromParcel(Parcel in) {
            return new Foo1(in);
        }

        @Override
        public Foo1[] newArray(int size) {
            return new Foo1[size];
        }
    };

    private void method() {

    }

    private void method(int j) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }
}
