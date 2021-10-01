package annotations;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

public class ClassInfoContainer implements Serializable {
    private final Name className;
    private final Map<ElementKind, List<ExecutableInfo>> executableElements = new HashMap<>();
    private final Map<ElementKind, List<FieldInfo>> fieldElements = new HashMap<>();

    ClassInfoContainer(Name className) {
        this.className = className;
    }

    void registerExecutableElement(ElementKind kind, Name name, List<Info> info, TypeMirror returnType, Set<Modifier> modifiers) {
        ExecutableInfo ci = new ExecutableInfo(name, info, returnType, modifiers);
        if(!executableElements.containsKey(kind)) {
            executableElements.put(kind, new ArrayList<>());
        }
        executableElements.get(kind).add(ci);
    }
    void registerFieldElement(ElementKind kind, FieldInfo info) {
        if(!fieldElements.containsKey(kind)) {
            fieldElements.put(kind, new ArrayList<>());
        }
        fieldElements.get(kind).add(info);
    }
    public Map<ElementKind, List<ExecutableInfo>> getExecutableElements() {
        return executableElements;
    }
    public Map<ElementKind, List<FieldInfo>> getFieldElements() {
        return fieldElements;
    }
    public Name getClassName() {
        return className;
    }

}
