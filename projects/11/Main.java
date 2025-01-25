import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            JackCompiler j = new JackCompiler(args[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
