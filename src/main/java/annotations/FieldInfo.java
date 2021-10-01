package annotations;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import java.util.Set;

public class FieldInfo extends Info {
    private final Set<Modifier> modifiers;

    FieldInfo(Name name, TypeMirror type, Set<Modifier> modifiers) {
        super(name, type);
        this.modifiers = modifiers;
    }

    public Set<Modifier> getModifiers() {
        return modifiers;
    }
}
