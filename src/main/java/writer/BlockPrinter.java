package writer;

import annotations.ClassInfoContainer;
import annotations.ExecutableInfo;
import annotations.FieldInfo;
import annotations.Info;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.io.PrintWriter;
import java.util.Set;
import java.util.stream.Collectors;

class BlockPrinter {
    private static PrintWriter out;
    private int indentLevel;
    private final int textSize;

    BlockPrinter(ClassInfoContainer info, int currentIndentLevel, int textSize) {
        indentLevel = currentIndentLevel;
        this.textSize = textSize;
        write(info);
    }

    private void write(ClassInfoContainer c) {
        beginClass(c);
        for(ElementKind type : c.getFieldElements().keySet()) {
            for(FieldInfo fieldInfo : c.getFieldElements().get(type)) {
                printField(fieldInfo);
            }
        }
        println("");
        for(ElementKind type : c.getExecutableElements().keySet()) {
            for(ExecutableInfo exEl : c.getExecutableElements().get(type)) {
                if(!exEl.getName().toString().equals("<init>")) {
                    printExecutable(exEl);
                }
            }
        }
        endClass();
    }
    private void beginClass(ClassInfoContainer c) {
        indent();
        int x = 0; int y = 0;
        println("\\begin{class}[text width=" + textSize + "cm]" +
                "{" + c.getClassName().toString() + "}{" + x + "," + y + "}");
    }
    private void endClass() {
        println("\\end{class}");
    }
    private void printField(FieldInfo f) {
        indent();
        println(
                appendVisibilityModifier(new StringBuilder("\\attribute{"), f.getModifiers())
                    .append(f.getName().toString())
                    .append(" : ")
                    .append(removePackageInfo(f.getType().toString()))
                    .append("}")
                    .toString()
        );
        deIndent();
    }
    private void printExecutable(ExecutableInfo ex) {
        indent();
        println(appendVisibilityModifier(new StringBuilder("\\operation{"), ex.getModifiers())
                .append(ex.getName().toString())
                .append("(")
                .append(getExecutableParams(ex))
                .append(")")
                .append(getReturnType(ex))
                .append("}")
                .toString()
        );
        deIndent();
    }
    private String getReturnType(ExecutableInfo info) {
        if(info.getReturnType().toString().equals("void")) {
            return "";
        }
        else return " : " + removePackageInfo(info.getReturnType().toString());
    }
    private String getExecutableParams(ExecutableInfo info) {
        StringBuilder stringOut = new StringBuilder();
        for(int i = 0; i < info.getInfo().size(); i++) {
            stringOut.append(info.getInfo().get(i).getName().toString());
            stringOut.append(" : ");
            stringOut.append(removePackageInfo(info.getInfo().get(i).getType().toString()));
            if(i < info.getInfo().size() - 1) {
                stringOut.append(", ");
            }
        }
        return stringOut.toString();
    }
    private String removePackageInfo(String in) {
        int cursor = 0;
        String current = in;
        while(current.contains(".")) {
            current = in.substring(cursor);
            cursor++;
        }
        return current;
    }
    private StringBuilder appendVisibilityModifier(StringBuilder currentString, Set<Modifier> modifiers) {
        //collect visibility modifiers
        Set<Modifier> modifierSet = modifiers.stream()
                .filter(n -> (n == Modifier.PUBLIC || n == Modifier.PRIVATE || n == Modifier.PROTECTED))
                .collect(Collectors.toSet());
        if(modifierSet.isEmpty()) {
            currentString.append("$\\sim$ ");
        } else {
            for (Modifier m : modifierSet) {
                switch (m) {
                    case PUBLIC:
                        currentString.append("+ ");
                        break;
                    case PRIVATE:
                        currentString.append("- ");
                        break;
                    case PROTECTED:
                        currentString.append("\\# ");
                        break;
                }
            }
        }
        return currentString;
    }
    static void setOut(PrintWriter fileOut) {
        out = fileOut;
    }
    private void println(String s) {
        for(int i = indentLevel; i > 0; i--) {
            out.print("    ");
        }
        out.println(s);
    }
    private void indent() {
        indentLevel++;
    }
    private void deIndent() {
        if(indentLevel > 0) indentLevel--;
    }
    private void nl() {
        out.println();
    }
}
