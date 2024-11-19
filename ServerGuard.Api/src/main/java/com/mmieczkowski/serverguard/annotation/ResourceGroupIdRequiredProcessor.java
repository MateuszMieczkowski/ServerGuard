package com.mmieczkowski.serverguard.annotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("com.mmieczkowski.serverguard.annotation.ResourceGroupAccess")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class ResourceGroupIdRequiredProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(ResourceGroupAccess.class)) {
            if (element.getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement) element;
                ResourceGroupAccess annotation = method.getAnnotation(ResourceGroupAccess.class);

                boolean found = method.getParameters().stream()
                        .anyMatch(param -> param.getSimpleName().contentEquals(annotation.value()));

                if (!found) {
                    processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.ERROR,
                            "Method " + method.getSimpleName() + " must have a parameter named: " + annotation.value(),
                            element
                    );
                }
            }
        }
        return true;
    }
}
