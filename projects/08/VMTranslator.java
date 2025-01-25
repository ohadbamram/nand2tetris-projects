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
            String outputFile = input.getPath() + "/" + input.getName() + ".asm";
            writer = new CodeWriter(new File(outputFile));

            //
            if(vmFiles.length > 1){
                writer.writeInit();
            }


            // Translate each .vm file in the directory
            for (File vmFile : vmFiles) {
                vmToAsm(vmFile.getPath());
                writer.setFileName(vmFile.getName());
            }
            writer.close();
        //if the path is already a valid file, handles it only
        }else if(path.endsWith(".vm")){
            String outputFile = path.replace(".vm", ".asm");
            writer = new CodeWriter(new File(outputFile));
            writer.setFileName(input.getName());
            vmToAsm(path);
            writer.close();
        }

    }


    //Given a vm file, create an asm file with Parser and codeWriter classes 
    private void vmToAsm(String file) throws IOException{
        parser = new Parser(file);
        while(parser.hasMoreLines()){
            parser.advance();
            if(parser.getCurrCommand() == null){
                continue;
            }
            String line = parser.getCurrCommand();

            switch (parser.commandType(line)) {
                case 0 -> writer.writeArithmetic(parser.arg1());
                case 1, 2 -> writer.writePushPop(parser.commandType(line), parser.arg1(), parser.arg2());
                case 3 -> writer.writeLabel(parser.arg1());
                case 4 -> writer.writeGoto(parser.arg1());
                case 5 -> writer.writeIf(parser.arg1());
                case 6 -> writer.writeFunction(parser.arg1(), parser.arg2());
                case 7 -> writer.writeReturn();
                case 8 -> writer.writeCall(parser.arg1(), parser.arg2());
                default -> {}
            }
        }

    }


}
