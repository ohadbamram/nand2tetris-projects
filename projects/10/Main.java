import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            JackAnalyzer j = new JackAnalyzer(args[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
