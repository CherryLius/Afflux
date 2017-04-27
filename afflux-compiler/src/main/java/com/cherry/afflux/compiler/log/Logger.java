package com.cherry.afflux.compiler.log;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * Created by LHEE on 2017/3/15.
 */

public final class Logger {
    /**
     * log tools
     */
    private Messager mMessager;

    public Logger(Messager messager) {
        this.mMessager = messager;
    }

    public void note(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.NOTE, element, message, args);
    }

    public void warning(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.WARNING, element, message, args);
    }

    public void error(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    private void printMessage(Diagnostic.Kind kind, Element element,
                              String message, Object[] args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        mMessager.printMessage(kind, message, element);
    }

    //System io
    public static void out(String message, Object... args) {
        if (args.length > 0)
            message = String.format(message, args);
        System.out.println(getCallStackTrace() + message);
    }

    public static void err(String message, Object... args) {
        if (args.length > 0)
            message = String.format(message, args);
        System.err.println(getCallStackTrace() + message);
    }


    private static String getCallStackTrace() {
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        StringBuilder builder = new StringBuilder();
        if (stackElements != null) {
//            for (int i = 0; i < stackElements.length; i++) {
//                System.out.println("ClassName: " + stackElements[i].getClassName() + "\t");
//                System.out.println("FileName: " + stackElements[i].getFileName() + "\t");
//                System.out.println("MethodName: " + stackElements[i].getMethodName() + "\t");
//                System.out.println("LineNumber: " + stackElements[i].getLineNumber() + "\t");
//            }
            //Call stackTrace
            StackTraceElement caller = stackElements[2];
            builder.append(caller.getClassName())
                    .append("[")
                    .append(caller.getMethodName())
                    .append("]\t");
        }
        return builder.toString();
    }
}
