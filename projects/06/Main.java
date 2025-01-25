import java.io.IOException;
public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java HackAssembler <inputFile.asm>");
            System.exit(1);
        }

        try {
            new HackAssembler(args[0]);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}