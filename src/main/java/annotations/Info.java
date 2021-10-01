package annotations;

import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;

public class Info {
    private final Name name;
    private final TypeMirror type;

    Info(Name name, TypeMirror type) {
        this.name = name;
        this.type = type;
    }

    public Name getName() {
        return this.name;
    }

    public TypeMirror getType() {
        return this.type;
    }

}
