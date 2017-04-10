package com.cherry.afflux.compiler;

import static java.lang.System.out;

public class MainTest {

    public static void main(String[] args) {
        String s = "Hello";
        out.println("s===" + Integer.toHexString(s.hashCode()));

        s += " World";

        out.println("s : " + Integer.toHexString(s.hashCode()) + "--" + s);

        System.err.println("Afflux Compiler main.");

    }


}
