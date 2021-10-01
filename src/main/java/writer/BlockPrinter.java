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

    BlockPrinter(ClassInfoContainer info, int currentIndentLevel) {
        write(info);
        indentLevel = currentIndentLevel;
    }

    private void write(ClassInfoContainer c) {
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
    }
    private void printField(FieldInfo f) {

    }
    private void printExecutable(ExecutableInfo ex) {

    }
    static void setOut(PrintWriter fileOut) {
        out = fileOut;
    }

    private void print(String s) {
        for(int i = indentLevel; i > 0; i--) {
            out.print("    ");
        }
        out.print(s);
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
