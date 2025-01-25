import java.io.IOException;

public class Main {

    public static void main(String[] args){
        try {
            VMTranslator v = new VMTranslator("./files");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // must add comments



    }
}
