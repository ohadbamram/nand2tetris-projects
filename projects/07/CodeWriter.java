import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
    private final BufferedWriter writer;
    private static int labelCounter = 1;


    public CodeWriter(File output) throws IOException {
        writer = new BufferedWriter(new FileWriter(output));
        init();
    }

     public void writeArithmetic(String command) throws IOException {


            if(command.equalsIgnoreCase("add") || command.equalsIgnoreCase("sub")
              || command.equalsIgnoreCase("and") || command.equalsIgnoreCase("or")) {

                writeLine("// Pop two values from the stack");
                writeLine("@SP");
                writeLine("AM=M-1");
                writeLine("D=M");
                writeLine("@SP");
                writeLine("AM=M-1");
                writeLine("// " + command + " the two values");
                switch(command){

                    case "add":
                        writeLine("M=D+M");
                        break;
                    case "sub":
                        writeLine("M=M-D");
                        break;
                    case "and":
                        writeLine("M=D&M");
                        break;
                    case "or":
                        writeLine("M=D|M");
                        break;
                    default:
                        break;
                }

                writeLine("// Increment SP");
                writeLine("@SP");
                writeLine("M=M+1");

            }
            else if(command.equalsIgnoreCase("eq") || command.equalsIgnoreCase("lt")
                    || command.equalsIgnoreCase("gt")){


                // Init two labels to avoid collisions of jumps
                String labelTrue = "LABEL_TRUE_" + labelCounter;
                String labelEnd = "LABEL_END_" + labelCounter;
                String jump = "J" + command.toUpperCase();
                labelCounter++;

                writeLine("// Decrement sp twice and subtract the two values");
                writeLine("@SP");
                writeLine("AM=M-1");
                writeLine("D=M");
                writeLine("@SP");
                writeLine("AM=M-1");
                writeLine("D=M-D");
                writeLine("// Jump to true if D = 0");
                writeLine("@" + labelTrue);
                writeLine("D;" + jump);
                writeLine("// Push 0 to the stack if D != 0");
                writeLine("@SP");
                writeLine("A=M");
                writeLine("M=0");
                writeLine("@SP");
                writeLine("M=M+1");
                writeLine("// Unconditional jump to end");
                writeLine("@" + labelEnd );
                writeLine("0;JMP");
                writeLine("// Push -1 to the stack if D = 0");
                writeLine("(" + labelTrue + ")");
                writeLine("@SP");
                writeLine("A=M");
                writeLine("M=-1");
                writeLine("@SP");
                writeLine("M=M+1");
                writeLine("(" + labelEnd + ")");


            }

            // Handles not or neg case
            else{
                writeLine("@SP");
                writeLine("A=M-1");

                if(command.equalsIgnoreCase("not")){
                    writeLine("M=!M");
                }
                else {
                    writeLine("M=-M");
                }
            }

    }

    // Set the addresses of saved symbols
    private void init() throws IOException{
        writeLine("@256");
        writeLine("D=A");
        writeLine("@SP");
        writeLine("M=D");
        writeLine("@300");
        writeLine("D=A");
        writeLine("@LCL");
        writeLine("M=D");
        writeLine("@400");
        writeLine("D=A");
        writeLine("@ARG");
        writeLine("M=D");
        writeLine("@3000");
        writeLine("D=A");
        writeLine("@THIS");
        writeLine("M=D");
        writeLine("@3010");
        writeLine("D=A");
        writeLine("@THAT");
        writeLine("M=D");

    }

    // An helper function to write a line and go to a new line
    private void writeLine(String line) throws IOException {
        writer.write(line);
        writer.newLine();
    }


    public void writePushPop(int command, String segment, int index) throws IOException {

        //Handle pointer in an helper function
        if(segment.equalsIgnoreCase("pointer")){
            pointerPushPop(command , index);
            return;
        }
        //Handles static accordingly
        else if(segment.equalsIgnoreCase("static")){
            index += 16;
        }
        //Gets the label of the segment
       segment = getLabel(segment);

       writeLine("// D = " + index);
       writeLine("@" + index);
       writeLine("D=A");

       //Segment need to be taken in the address calculation
       if(segment != null && !segment.equals("static")){
           writeLine("// A = " + segment + " + " + index);
           writeLine("@" + segment);

           //Segment is R5 so we go to R5 + index
           if(segment.equalsIgnoreCase("R5")){
               writeLine("A=D+A");
           }
           //Else we go to the index + the address given in the segment
           else{
               writeLine("A=D+M");
           }
           writeLine("D=M");
       }
        if(segment != null && segment.equals("static")){
            writeLine("D=M");
        }



        // Push
       if(command == Parser.C_PUSH){
           writeLine("// RAM[SP] = RAM[" + segment + " + " + index + "]");
           writeLine("@SP");
           writeLine("A=M");
           writeLine("M=D");
           writeLine("// SP++");
           writeLine("@SP");
           writeLine("M=M+1");
       }
       //Pop from stack to the given segment and index using temp R13
       else{
           writeLine("D=A");
           writeLine("@R13");
           writeLine("M=D");
           writeLine("@SP");
           writeLine("M=M-1");
           writeLine("A=M");
           writeLine("D=M");
           writeLine("@R13");
           writeLine("A=M");
           writeLine("M=D");
       }
    }


    //An helper function to handle push and pop for pointer case
    private void pointerPushPop(int command , int index) throws IOException{

        if(command == Parser.C_POP){

            //Pop from stack to THIS
            if(index == 0){
                writeLine("@SP");
                writeLine("A=M-1");
                writeLine("D=M");
                writeLine("@THIS");
                writeLine("M=D");
                writeLine("@SP");
                writeLine("M=M-1");
            }
            //Pop from stack to THAT
            else{
                writeLine("@SP");
                writeLine("A=M-1");
                writeLine("D=M");
                writeLine("@THAT");
                writeLine("M=D");
                writeLine("@SP");
                writeLine("M=M-1");
            }
        }
        // Push from THIS to the stack
        else{
            if(index == 0){
                writeLine("@THIS");
                writeLine("D=M");
                writeLine("@SP");
                writeLine("A=M");
                writeLine("M=D");
                writeLine("@SP");
                writeLine("M=M+1");

            }
            // Push from THAT to the stack
            else{
                writeLine("@THAT");
                writeLine("D=M");
                writeLine("@SP");
                writeLine("A=M");
                writeLine("M=D");
                writeLine("@SP");
                writeLine("M=M+1");
            }
        }

    }

    //Get label of the segment
    private String getLabel(String segment) {
        switch (segment.toLowerCase()) {
            case "local":
                return "LCL";
            case "argument":
                return "ARG";
            case "this":
                return "THIS";
            case "that":
                return "THAT";
            case "temp":
                return "R5";
            case "static":
                return "static";
            default:
                return null;
        }

    }

    public void close() throws IOException {
        writer.close();
    }
}
