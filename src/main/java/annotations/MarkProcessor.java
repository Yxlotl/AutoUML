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
import java.lang.annotation.ElementType;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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

            if (pass.isEmpty()) {
                continue;
            }

            for(Element p : pass) {
                Map<ElementKind, List<Element>> sortedInfo = getClassInfo(p);
                try {
                    writeToFile((p.getEnclosingElement()).getSimpleName().toString(), p,  sortedInfo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private Map<ElementKind, List<Element>> getClassInfo(Element classElement) {
        List<? extends Element> go  = classElement.getEnclosedElements();
        Map<ElementKind, List<Element>> out = new HashMap<>();
        for(Element e : go) {
            if(!out.containsKey(e.getKind())) {
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

    private static PrintWriter out;
    private void writeToFile(String packageName, Element classElement, Map<ElementKind, List<Element>> classInfo) throws IOException {
        String className = classElement.getSimpleName().toString();
        try {
            Files.createDirectory(Paths.get("target/output"));
            out = new PrintWriter(new BufferedWriter(new FileWriter("target/output/" + className + "_info.txt")));
        } catch (FileAlreadyExistsException e) {
            String name = e.getFile();
            Files.deleteIfExists(Paths.get("target/output/"+name));
            out = new PrintWriter(new BufferedWriter(new FileWriter("target/output/" + className + "_info.txt")));
        }
        out.println("/*");
        if (packageName != null) {
            out.println("PACKAGE:");
            out.println("    " + packageName);
            out.println();
        }
        out.println("CLASS:");
        out.println("    " + className);
        out.println();

        for(ElementKind e : classInfo.keySet()) {
            if(e.isField()) {
                writeSection(e, classInfo);
            }
            if(e == ElementKind.METHOD || e == ElementKind.CONSTRUCTOR) {
                writeParamSection(e, classInfo);
            }
        }

        out.println("*/");
        out.close();
    }
    private void writeSection(ElementKind e, Map<ElementKind, List<Element>> info) {
        out.println(e.toString() + ":");
        List<Element> names = info.get(e);
        names.forEach(m -> out.println("    " + m.getSimpleName().toString() + " : " + m.asType().toString()));
        out.println("");
    }

    private void writeParamSection(ElementKind e, Map<ElementKind, List<Element>> info) {
        out.println(e.toString() + ":");
        List<Element> names = info.get(e);
        for(Element method : names) {
            out.println("    " + method.getSimpleName().toString());

            //bad fix, will probably break later
            if(!((ExecutableType) method.asType()).getReturnType().toString().equals("void"))
            out.println("    RETURNS TYPE: " + ((ExecutableType) method.asType()).getReturnType().toString());

            if(!((ExecutableType) method.asType()).getParameterTypes().isEmpty()) {
                out.println("    PARAMS: ");
                ((ExecutableType) method.asType()).getParameterTypes().forEach(
                        n -> out.println("        " + n.toString())
                );
            }
            out.println("");
        }
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
