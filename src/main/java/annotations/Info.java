package annotations;

import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;

class Info {
    private final Name name;
    private final TypeMirror type;

    Info(Name name, TypeMirror type) {
        this.name = name;
        this.type = type;
    }

    Name getName() {
        return this.name;
    }

    TypeMirror getType() {
        return this.type;
    }
}
