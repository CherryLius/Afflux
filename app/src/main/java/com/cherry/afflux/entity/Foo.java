package com.cherry.afflux.entity;

import com.cherry.afflux.annotation.Parcelable;

/**
 * Created by Administrator on 2017/4/27.
 */
@Parcelable
public class Foo {

    private int id;
    private String name;
    private long time;
    private byte bt;
    private float length;
    private double radius;
    private boolean flag;

    public Foo() {
    }

    public Foo(int i) {
    }

    private void method() {
        int i = 2;
        i++;
    }

//    private int method(int j) {
//        return 0;
//    }
//
//    private <T> T method(T t) {
//        return t;
//    }
}
