package annotations;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Set;

public class ExecutableInfo {

    private final Name name;
    private final List<Info> info;
    private final TypeMirror returnType;
    private final Set<Modifier> modifiers;

    ExecutableInfo(Name name, List<Info> info, TypeMirror returnType, Set<Modifier> modifiers) {
        this.name = name;
        this.info = info;
        this.returnType = returnType;
        this.modifiers = modifiers;
    }

    public Name getName() {
        return name;
    }

    public List<Info> getInfo() {
        return info;
    }

    public TypeMirror getReturnType() {
        return returnType;
    }

    public Set<Modifier> getModifiers() {
        return modifiers;
    }
}
