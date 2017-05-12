package com.cherry.afflux.compiler;

import com.cherry.afflux.annotation.BindBoolean;
import com.cherry.afflux.annotation.BindColor;
import com.cherry.afflux.annotation.BindDrawable;
import com.cherry.afflux.annotation.BindFloat;
import com.cherry.afflux.annotation.BindInt;
import com.cherry.afflux.annotation.BindString;
import com.cherry.afflux.annotation.BindView;
import com.cherry.afflux.annotation.OnCheckedChanged;
import com.cherry.afflux.annotation.OnClick;
import com.cherry.afflux.annotation.OnDrag;
import com.cherry.afflux.annotation.OnEditorAction;
import com.cherry.afflux.annotation.OnFocusChange;
import com.cherry.afflux.annotation.OnItemClick;
import com.cherry.afflux.annotation.OnItemLongClick;
import com.cherry.afflux.annotation.OnItemSelected;
import com.cherry.afflux.annotation.OnLongClick;
import com.cherry.afflux.annotation.OnPageChange;
import com.cherry.afflux.annotation.OnScroll;
import com.cherry.afflux.annotation.OnTextChanged;
import com.cherry.afflux.annotation.OnTouch;
import com.cherry.afflux.compiler.log.Logger;
import com.cherry.afflux.compiler.model.BindingClass;
import com.cherry.afflux.compiler.model.BindingResourceField;
import com.cherry.afflux.compiler.model.BindingViewField;
import com.cherry.afflux.compiler.model.BindingViewMethod;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
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

    private static final Class<? extends Annotation>[] LISTENERS = new Class[]{
            OnCheckedChanged.class,
            OnClick.class,
            OnDrag.class,
            OnEditorAction.class,
            OnFocusChange.class,
            OnItemClick.class,
            OnItemLongClick.class,
            OnItemSelected.class,
            OnLongClick.class,
            OnPageChange.class,
            OnScroll.class,
            OnTextChanged.class,
            OnTouch.class,
    };

    private static final Class<? extends Annotation>[] RESOURCE_ANNOTATIONS = new Class[]{
            BindBoolean.class,
            BindColor.class,
            BindDrawable.class,
            BindFloat.class,
            BindInt.class,
            BindString.class,
    };

    private static final String[] RESOURCE_TYPE = new String[]{
            BindingResourceField.ResourceType.GET_BOOLEAN,
            BindingResourceField.ResourceType.GET_COLOR,
            BindingResourceField.ResourceType.GET_DRAWABLE,
            BindingResourceField.ResourceType.GET_FLOAT,
            BindingResourceField.ResourceType.GET_INT,
            BindingResourceField.ResourceType.GET_STRING,

    };

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mLogger = Logger.instance();
        mLogger.setMessager(processingEnv.getMessager());
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
        annotations.addAll(Arrays.asList(LISTENERS));
        annotations.addAll(Arrays.asList(RESOURCE_ANNOTATIONS));

        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            findAndParseTargets(roundEnv);
        }
        return false;
    }

    private void findAndParseTargets(RoundEnvironment roundEnv) {
        Map<String, BindingClass> bindingClassMap = new LinkedHashMap<>();
        for (int i = 0; i < RESOURCE_ANNOTATIONS.length; i++) {
            for (Element element : roundEnv.getElementsAnnotatedWith(RESOURCE_ANNOTATIONS[i])) {
                TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
                BindingClass binding = getBindingClass(bindingClassMap, enclosingElement);
                BindingResourceField field = new BindingResourceField(element,
                        RESOURCE_ANNOTATIONS[i],
                        RESOURCE_TYPE[i]);
                binding.addBindingResourceField(field);
            }
        }
        for (Element element : roundEnv.getElementsAnnotatedWith(BindView.class)) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            BindingClass binding = getBindingClass(bindingClassMap, enclosingElement);
            BindingViewField field = new BindingViewField(element);
            binding.addBindingViewField(field);
        }
        for (Class<? extends Annotation> annotationClass : LISTENERS) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotationClass)) {
                TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
                BindingClass binding = getBindingClass(bindingClassMap, enclosingElement);
                BindingViewMethod method = new BindingViewMethod(element, annotationClass);
                binding.addBindingViewMethod(method);
            }
        }
        for (BindingClass binding : bindingClassMap.values()) {
            try {
                binding.generateFile().writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        throw new IllegalArgumentException("222");
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
