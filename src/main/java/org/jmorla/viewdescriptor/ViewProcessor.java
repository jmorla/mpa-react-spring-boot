package org.jmorla.viewdescriptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.google.auto.service.AutoService;

@AutoService(Processor.class)
@SupportedAnnotationTypes("org.jmorla.viewdescriptor.View")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class ViewProcessor extends AbstractProcessor {

    private final Map<Name, View> views = new HashMap<>();
    private Elements elementUtils;
    private PackageNameExtractor packageNameExtractor;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        packageNameExtractor = new PackageNameExtractor(elementUtils);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            processAnnotatedElements(roundEnv.getElementsAnnotatedWith(View.class));
        }
        return false;
    }

    private void processAnnotatedElements(Set<? extends Element> annotatedElements) {
        String packageName = "";
        for (Element annotated : annotatedElements) {
            packageName = packageNameExtractor.getPackageName(annotated);
            View view = annotated.getAnnotation(View.class);
            views.put(((TypeElement) annotated).getQualifiedName(), view);
        }
        String descriptor = new DescriptorGen().generate(views);
        writeDescriptor(descriptor);
        writeControllerAdvice(packageName);
    }

    private void writeControllerAdvice(String packageName) {
        String controllerAdviceTemplate = Templates.CONTROLLER_ADVICE_TEMPLATE.formatted(packageName);
        writeSourceFile(packageName + ".PageAttributesController", controllerAdviceTemplate);
    }

    private void writeSourceFile(String qualifiedClassName, String source) {
        try {
            FileObject fileObject = processingEnv.getFiler().createSourceFile(qualifiedClassName);
            try (PrintWriter writer = new PrintWriter(fileObject.openWriter())) {
                writer.print(source);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to write source file: " + qualifiedClassName, ex);
        }
    }

    private void writeDescriptor(String source) {
        try {
            FileObject fileObject = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "view_descriptor.json");
            try (Writer writer = fileObject.openWriter()) {
                writer.write(source);
            }
        } catch (IOException ex) {
            processingEnv.getMessager().printError("Unable to create view descriptor: " + ex.getMessage());
        }
    }

}
