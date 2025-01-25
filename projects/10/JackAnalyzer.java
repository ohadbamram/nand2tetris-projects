import java.io.*;

public class JackAnalyzer {
    private CompilationEngine engine;

    // Constructor
    public JackAnalyzer(String inputFile) throws IOException {
        File input = new File(inputFile);
        if(input.isDirectory()){
            File[] jackFiles = input.listFiles(((dir, name) -> name.endsWith(".jack")));
            if(jackFiles == null || jackFiles.length == 0){
                throw new IOException("No .jack files in directory " + inputFile);
            }

            for(File jackFile : jackFiles){
                // Create the output file
                String outputFile = jackFile.getPath().replace(".jack", ".xml");
                // Initialize the CompilationEngine
                engine = new CompilationEngine(jackFile.getPath(), outputFile);
                analyze();
            }
        }
        else if (inputFile.endsWith(".jack")){
            String outputFile = inputFile.replace(".jack", ".xml");
            engine = new CompilationEngine(inputFile, outputFile);
            analyze();
        }

    }

    // Analyze the input file and generate XML output
    public void analyze() throws IOException {
        // Start the compilation process
        engine.compileClass();
    }

}