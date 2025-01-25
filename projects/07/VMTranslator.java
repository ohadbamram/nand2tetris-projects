import java.io.*;

public class VMTranslator  {
    private Parser parser;
    private CodeWriter writer;


    public VMTranslator(String path) throws IOException{
        File input = new File(path);

        //If given path file is a dir, loop through its files and process them
        if(input.isDirectory()){
            File[] vmFiles = input.listFiles((dir, name) -> name.endsWith(".vm"));
            if (vmFiles == null || vmFiles.length == 0) {
                throw new IOException("No .vm files found in directory: " + path);
            }
            // Translate each .vm file in the directory
            for (File vmFile : vmFiles) {
                vmToAsm(vmFile.getPath());
            }
        //if the path is already a valid file, handles it only
        }else if(path.endsWith(".vm")){
            vmToAsm(path);
        }

    }


    //Given a vm file, create an asm file with Parser and codeWriter classes 
    private void vmToAsm(String file) throws IOException{
        String outputFile = file.replace(".vm", ".asm");
        parser = new Parser(file);
        writer = new CodeWriter(new File(outputFile));
        while(parser.hasMoreLines()){
            parser.advance();
            if(parser.getCurrCommand() == null){
                continue;
            }
            String line = parser.getCurrCommand();
            if(parser.commandType(line) == 0){
                writer.writeArithmetic(parser.arg1());
            }
            else {
                writer.writePushPop(parser.commandType(line), parser.arg1(), parser.arg2());
            }
        }
        writer.close();

    }


}
