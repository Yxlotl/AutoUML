package writer;

import annotations.ClassInfoContainer;
import com.google.common.reflect.ClassPath;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

//TODO: make this thing make files dammit!
//use this standard: http://ctan.math.illinois.edu/graphics/pgf/contrib/pgf-umlcd/pgf-umlcd-manual.pdf
public class LatexWriter {
    private final List<ClassInfoContainer> context;
    private static PrintWriter out;
    private static final String outputDir = "target/output/";
    private static ClassInfoContainer currentBuffer;
    private static int indentBuffer = 0;

    private static final int TEXT_SIZE = 7;

    public LatexWriter(List<ClassInfoContainer> in) {
        context = in;
    }

    public void writeAll() {
        //create the actual file
        makeFile();
        begin();
        for(ClassInfoContainer c : context) {
            write(c);
        }
        conclude();
    }
    private void write(ClassInfoContainer c) {
        currentBuffer = c;
        BlockPrinter nextBlock = new BlockPrinter(currentBuffer, indentBuffer, TEXT_SIZE);
    }

    private void makeFile() {
        String fileName = "test";
        try {
            try {
                Files.createDirectory(Paths.get(outputDir));
                out = new PrintWriter(new BufferedWriter(new FileWriter(outputDir + fileName + "_info.tex")));
            } catch (FileAlreadyExistsException e) {
                String name = e.getFile();
                Files.deleteIfExists(Paths.get(outputDir + name));
                out = new PrintWriter(new BufferedWriter(new FileWriter(outputDir + fileName + "_info.tex")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        timestamp();
    }

    private void timestamp() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        println("%AUTOGENERATED [" + dateTime.format(formatter) + "]");
    }
    private void begin() {
        println("\\usepackage{tikz}");
        println("\\usepackage{pgf-umlcd}");
        println("\\begin{tikzpicture}");
        BlockPrinter.setOut(out);
    }
    private void conclude() {
        indentBuffer = 0;
        println("\\end{tikzpicture}");
        out.close();
    }

    private void indent() {
        indentBuffer ++;
    }

    private void deIndent() {
        if(indentBuffer >= 1) indentBuffer--;
    }

    private void println(String in) {
        for(int i = indentBuffer; i > 0; i--) {
            out.print("    ");
        }
        out.println(in);
    }
}
