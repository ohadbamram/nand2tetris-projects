import java.io.*;
import javax.imageio.IIOException;
import java.util.*;


public class Parser {
    private BufferedReader reader;
    private String currCommand;
    private boolean hasMoreLines;
    private ArrayList<String> arithmeticCommands;

    public static final int C_ARITHMETIC = 0;
    public static final int C_PUSH = 1;
    public static final int C_POP = 2;
    public static final int C_LABEL = 3;
    public static final int C_GOTO = 4;
    public static final int C_IF = 5;
    public static final int C_FUNCTION = 6;
    public static final int C_RETURN = 7;
    public static final int C_CALL = 8;


    public Parser(String file) throws IOException {
          reader = new BufferedReader(new FileReader(file));
          currCommand = null;
          hasMoreLines = true;
          initializeArithmeticCommands();

    }

    public void advance() throws IOException {

        // Read lines until a non-empty line is found or the end of the file is reached
        while (hasMoreLines && (currCommand = reader.readLine()) != null) {

            //trim spaces before and after line
            currCommand = currCommand.split("//")[0].trim();
            //Skip comment lines or empty lines
            if(currCommand.isEmpty() || currCommand.startsWith("//")){
                continue;
            }



                return; // Exit once a valid line is found

        }
        // End of file or no more valid lines
        hasMoreLines = false;
        currCommand = null;
        reader.close();
    }


    //An arithmetic commands list
    private void initializeArithmeticCommands() {
        arithmeticCommands = new ArrayList<String>();

        arithmeticCommands.add("add");
        arithmeticCommands.add("sub");
        arithmeticCommands.add("neg");
        arithmeticCommands.add("eq");
        arithmeticCommands.add("gt");
        arithmeticCommands.add("lt");
        arithmeticCommands.add("and");
        arithmeticCommands.add("or");
        arithmeticCommands.add("not");
    }

    public boolean hasMoreLines() {
        return hasMoreLines;
    }


    //Given a line returns the command type
    public int commandType(String line){

        String[] words = line.split(" ");
        String firstWord = words[0];
            if(arithmeticCommands.contains(firstWord)){
                return C_ARITHMETIC;
            }
            else if (line.contains("push")) {
                return C_PUSH;
            }
            else if (line.contains("label")) {
                return C_LABEL;
            }
            else if (line.contains("if")) {
                return C_IF;
            }
            else if (line.contains("goto")) {
                return C_GOTO;
            }
            else if (line.contains("function")) {
                return C_FUNCTION;
            }
            else if (line.contains("return")) {
                return C_RETURN;
            }
            else if (line.contains("call")) {
                return C_CALL;
            }
            else{
                return C_POP;
            }

    }

    public String getCurrCommand(){
        return currCommand;
    }

    public String arg1(){
        if(currCommand == null){
            return null;
        }
        String[] words = currCommand.split(" ");
        if(commandType(currCommand) == C_ARITHMETIC){
            return words[0];
        }
        else{
            return words[1];
        }
    }

    public int arg2() throws NumberFormatException {
        try {
            String[] words = currCommand.split(" ");
            return Integer.parseInt(words[2]);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("Invalid argument: " + nfe.getMessage());
        }

    }


}