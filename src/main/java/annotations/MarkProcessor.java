package annotations;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("annotations.Mark")
@AutoService(Processor.class)
public class MarkProcessor extends AbstractProcessor {
    Messager messager;

    @Override
    public void init(ProcessingEnvironment env) {
        messager = env.getMessager();
        super.init(env);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement thisAnnotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(thisAnnotation);

            Map<Boolean, List<Element>> sorted = annotatedElements.stream().collect(
                    Collectors.partitioningBy(this::verify));

            List<Element> pass = sorted.get(true);
            List<Element> fail = sorted.get(false);
            notify(fail);

            if(pass.isEmpty()) {
                continue;
            }

            String className = ((TypeElement) pass.get(0).getEnclosingElement()).getQualifiedName().toString();

            Map<String, List<String>> methodMap = pass.stream().collect(Collectors.toMap(
                    method -> method.getSimpleName().toString(),
                    method -> getFormattedParams((ExecutableType) method.asType())
            ));

            try {
                writeToFile(className, methodMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private <T> boolean verify(T e) {
        return true;
    }

    private void notify(List<Element> failures) {
        failures.forEach(e ->
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        "@Mark is being used incorrectly.", e));
    }

    private void writeToFile(String className, Map<String, List<String>> methods) throws IOException {
        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }
        JavaFileObject f = processingEnv.getFiler().createSourceFile(className);
        try(PrintWriter out = new PrintWriter(f.openWriter())) {
            out.println("/*");
            if (packageName != null) {
                out.println("The package was: " + packageName);
            }
            out.println("The class was: " + className);
            out.println("");
            out.println("Methods: ");
            for (String sc : methods.keySet()) {
                out.println("    <" + sc + ">");
                for (String n : methods.get(sc)) {
                    out.println("        " + n);
                }
            }
            out.println("*/");
        }
    }
    private List<String> getFormattedParams(ExecutableType e) {
        ArrayList<String> paramTypes = new ArrayList<>();
        e.getParameterTypes().stream().map(TypeMirror::toString).map(s -> s + " : ").forEach(paramTypes::add);
        for(int i = 0; i < paramTypes.size(); i++) {
            paramTypes.set(i, paramTypes.get(i) + i);
        }
        return paramTypes;
    }
}
