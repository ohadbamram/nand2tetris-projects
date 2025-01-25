import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class VMWriter {
    private final BufferedWriter writer;

    // Constructor: Opens the output file for writing
    public VMWriter(String outputFile) throws IOException {
        writer = new BufferedWriter(new FileWriter(outputFile));
    }

    // Writes a VM push command
    public void writePush(String segment, int index) throws IOException {
        writer.write("    push " + segment + " " + index + "\n");
    }

    // Writes a VM pop command
    public void writePop(String segment, int index) throws IOException {
        writer.write("    pop " + segment + " " + index + "\n");
    }

    // Writes a VM arithmetic command
    public void writeArithmetic(String command) throws IOException {
        switch (command){
            case "+":
                writer.write("    add" + "\n");
                break;
            case "-":
                writer.write("    sub" + "\n");
                break;
            case "*":
                writeCall("Math.multiply", 2);
                break;
            case "/":
                writeCall("Math.divide", 2);
                break;
            case "=":
                writer.write("    eq" + "\n");
                break;
            default:
                writer.write("    "+ command + "\n");
                break;
        }

    }

    // Writes a VM label command
    public void writeLabel(String label) throws IOException {
        writer.write("label " + label + "\n");
    }

    // Writes a VM goto command
    public void writeGoto(String label) throws IOException {
        writer.write("    goto " + label + "\n");
    }

    // Writes a VM if-goto command
    public void writeIf(String label) throws IOException {
        writer.write("    if-goto " + label + "\n");
    }

    // Writes a VM call command
    public void writeCall(String name, int nArgs) throws IOException {
        writer.write("    call " + name + " " + nArgs + "\n");
    }

    // Writes a VM function command
    public void writeFunction(String name, int nLocals) throws IOException {
        writer.write("function " + name + " " + nLocals + "\n");
    }

    // Writes a VM return command
    public void writeReturn() throws IOException {
        writer.write("    return");
    }

    public void writeNot() throws IOException {
        writer.write("    not\n");
    }

    public void writeNeg() throws IOException {
        writer.write("    neg\n");
    }

    public void writeNewline() throws IOException {
        writer.write("\n");
    }

    // Closes the output file
    public void close() throws IOException {
        writer.close();
    }
}