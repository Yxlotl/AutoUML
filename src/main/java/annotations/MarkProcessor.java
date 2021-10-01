package annotations;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("annotations.Mark")
@AutoService(Processor.class)
public class MarkProcessor extends AbstractProcessor {
    Messager messager;
    private List<ClassInfoContainer> annotatedClassesInfo = new ArrayList<>();
    private static ClassInfoContainer dataBuffer;
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

            if (pass.isEmpty()) {
                continue;
            }

            for (Element p : pass) {
                Map<ElementKind, List<Element>> sortedInfo = getClassInfo(p);
                dataBuffer = new ClassInfoContainer(p.getSimpleName());
                try {
                    writeToFile(sortedInfo);
                    annotatedClassesInfo.add(dataBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private Map<ElementKind, List<Element>> getClassInfo(Element classElement) {
        List<? extends Element> go = classElement.getEnclosedElements();
        Map<ElementKind, List<Element>> out = new HashMap<>();
        for (Element e : go) {
            if (!out.containsKey(e.getKind())) {
                out.put(e.getKind(), new ArrayList<>());
            }
            out.get(e.getKind()).add(e);
        }
        return out;
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

    private void writeToFile(Map<ElementKind, List<Element>> classInfo) throws IOException {
        for (ElementKind e : classInfo.keySet()) {
            if (e.isField()) {
                writeSection(e, classInfo);
            }
            if (e == ElementKind.METHOD || e == ElementKind.CONSTRUCTOR) {
                writeParamSection(e, classInfo);
            }
        }
    }

    private void writeSection(ElementKind e, Map<ElementKind, List<Element>> info) {
        List<Element> names = info.get(e);
        names.forEach(n -> dataBuffer.registerFieldElement(e, new FieldInfo(n.getSimpleName(), n.asType(), n.getModifiers())));
    }

    private void writeParamSection(ElementKind e, Map<ElementKind, List<Element>> info) {
        List<Element> names = info.get(e);
        names.forEach(n -> {
            List<Info> fieldInfo = new ArrayList<>();
            List<Name> params = ((ExecutableElement) n).getParameters().stream().map(VariableElement::getSimpleName).collect(Collectors.toList());
            List<? extends TypeMirror> types = ((ExecutableType) n.asType()).getParameterTypes();
            for(int i = 0; i < params.size(); i++) {
                fieldInfo.add(new Info(params.get(i), types.get(i)));
            }
            dataBuffer.registerExecutableElement(e, n.getSimpleName(), fieldInfo, ((ExecutableElement) n).getReturnType(), n.getModifiers());
        });

    }

    private List<String> getFormattedParams(Element el) {
        ExecutableType e = ((ExecutableType) el.asType());
        ArrayList<String> paramTypes = new ArrayList<>();

        e.getParameterTypes().stream().map(TypeMirror::toString).forEach(paramTypes::add);

        for (int i = 0; i < paramTypes.size(); i++) {
            paramTypes.set(i, paramTypes.get(i) + " [" + i + "]");
        }
        return paramTypes;
    }

}
