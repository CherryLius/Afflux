package com.cherry.afflux.compiler;

import com.cherry.afflux.annotation.Parcelable;
import com.cherry.afflux.compiler.log.Logger;
import com.cherry.afflux.compiler.model.AnnotatedClass;
import com.cherry.afflux.compiler.model.ParcelableClass;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by Administrator on 2017/4/27.
 */
@AutoService(Processor.class)
public class ParceableProcessor extends AbstractProcessor {

    private Elements mElementUtil;
    private Filer mFiler;
    private Logger mLogger;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtil = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mLogger = new Logger(processingEnv.getMessager());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedTypes = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            supportedTypes.add(annotation.getCanonicalName());
        }
        return supportedTypes;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(Parcelable.class);
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            parseParcelableElement(roundEnv);
        }
        return false;
    }

    private void parseParcelableElement(RoundEnvironment roundEnv) {
        try {
            for (Element element : roundEnv.getElementsAnnotatedWith(Parcelable.class)) {
                if (element.getKind() == ElementKind.CLASS) {
                    AnnotatedClass annotatedClass = new ParcelableClass((TypeElement) element);
                    annotatedClass.generateFile().writeTo(mFiler);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.err("IOExceptions %s", e);
        }
    }
}