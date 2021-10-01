package writer;

import annotations.ClassInfoContainer;

import java.io.PrintWriter;
import java.util.List;

//TODO: make this thing make files dammit!
//use this standard: http://ctan.math.illinois.edu/graphics/pgf/contrib/pgf-umlcd/pgf-umlcd-manual.pdf
public class LatexWriter {
    private static List<ClassInfoContainer> context;
    private static PrintWriter out;

    public LatexWriter(List<ClassInfoContainer> in) {
        context = in;
    }

    public void write() {
        //create the actual file
        makeFile();

        //write to file
        writeToFile();

        //clean up
        conclude();
    }

    private void makeFile() {

    }

    private void writeToFile() {

    }

    private void conclude() {

    }
}
