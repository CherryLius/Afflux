package com.cherry.afflux.compiler;

import com.cherry.afflux.annotation.BindString;
import com.cherry.afflux.annotation.BindView;
import com.cherry.afflux.annotation.OnClick;
import com.cherry.afflux.compiler.log.Logger;
import com.cherry.afflux.compiler.model.BindingClass;
import com.cherry.afflux.compiler.model.BindingViewField;
import com.cherry.afflux.compiler.model.BindingViewMethod;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by LHEE on 2017/3/14.
 */
@AutoService(Processor.class)
public class AffluxProcessor extends AbstractProcessor {

    /**
     * 注解元素相关辅助类
     */
    private Elements mElementUtils;

    /**
     * Java文件生成辅助类
     */
    private Filer mFiler;

    /**
     * 编译日志辅助类
     */
    private Logger mLogger;

    /**
     * Initializes the processor with the processing environment by
     * setting the {@code processingEnv} field to the value of the
     * {@code processingEnv} argument.  An {@code
     * IllegalStateException} will be thrown if this method is called
     * more than once on the same object.
     *
     * @param processingEnv environment to access facilities the tool framework
     *                      provides to the processor
     * @throws IllegalStateException if this method is called more than once.
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mLogger = new Logger(processingEnv.getMessager());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedTypes = getSupportedAnnotations().stream()
                .map(Class::getCanonicalName)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return supportedTypes;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();

        annotations.add(BindView.class);
        annotations.add(BindString.class);
        annotations.add(OnClick.class);

        return annotations;
    }

    /**
     * {@inheritDoc}
     *
     * @param annotations
     * @param roundEnv
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            findAndParseTargets(roundEnv);
        }
        return false;
    }

    private void findAndParseTargets(RoundEnvironment roundEnv) {
        Map<String, BindingClass> bindingClassMap = new LinkedHashMap<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(BindView.class)) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            BindingClass binding = getBindingClass(bindingClassMap, enclosingElement);
            BindingViewField field = new BindingViewField(element);
            binding.addBindingViewField(field);
        }
        for (Element element : roundEnv.getElementsAnnotatedWith(OnClick.class)) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            BindingClass binding = getBindingClass(bindingClassMap, enclosingElement);
            BindingViewMethod method = new BindingViewMethod(element, OnClick.class);
            binding.addBindingViewMethod(method);
        }
        for (BindingClass binding : bindingClassMap.values()) {
            try {
                binding.generateFile().writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private BindingClass getBindingClass(Map<String, BindingClass> map, TypeElement enclosingElement) {
        String className = enclosingElement.getQualifiedName().toString();
        Logger.err("qualified: %s simple: %s", enclosingElement.getQualifiedName().toString(), enclosingElement.getSimpleName().toString());
        BindingClass binding = map.get(className);
        if (binding == null) {
            binding = new BindingClass(mElementUtils, enclosingElement);
            map.put(className, binding);
        }
        return binding;
    }
}
