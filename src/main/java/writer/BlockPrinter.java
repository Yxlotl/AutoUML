package writer;

import annotations.ClassInfoContainer;
import annotations.ExecutableInfo;
import annotations.FieldInfo;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import java.io.PrintWriter;
import java.lang.annotation.ElementType;
import java.util.List;

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
        for(ElementKind type : c.getExecutableElements().keySet()) {
            for(ExecutableInfo exEl : c.getExecutableElements().get(type)) {
                printExecutable(exEl);
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

    }
    private void printExecutable(ExecutableInfo ex) {

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
