import java.io.*;

import javax.imageio.IIOException;
public class Parser {
    // fields
    private BufferedReader reader;
    private String currIns;
    private boolean hasMoreLines;

    // constructor
    public Parser (String file) throws IOException {
        reader = new BufferedReader(new FileReader(file));
        currIns = null;
        hasMoreLines = true;
    }
    
    public boolean hasMoreLines() {
        return hasMoreLines;
    }

    public void advance() throws IOException{
        // only advance if there's more lines
        if (!hasMoreLines) {
            return;
        }
        // make currIns the next line with no comments or whitespaces
        String line;
        while ((line = reader.readLine()) != null) {
            // remove all comments and whitespaces from the line
            line = line.trim();
            line = line.split("//")[0];
            line = line.replace("\\s", "");
            // if line is a valid instruction then make currIns line and break
            if (!line.isEmpty()) {
                currIns = line;
                return;
            }  
        }
        // if there's no valid instructions left in the file change hasMoreLines
        hasMoreLines = false;
        currIns = null;
        reader.close();              
    }

    public String getCurrentInstruction() {
        return currIns;
    }

    public void closeReader() throws IOException{
        reader.close();
    }

    public String instructionType() {
        if (currIns.startsWith("@")) {
            return "A_INSTRUCTION";
        } else if (currIns.startsWith("(") && currIns.endsWith(")")) {
            return "L_INSTRUCTION";
        } else {
            return "C_INSTRUCTION";
        }
    }

    public String symbol() {
        // if it's a C instruction don't bother
        if (instructionType() == "C_INSTRUCTION") {
            return null;
        }
        // if it's an A instruction return it without @
        if (instructionType() == "A_INSTRUCTION") {
            return currIns.substring(1);
        }
        // if it's an L instruction return it without the ()
        return currIns.substring(1, currIns.length() - 1);
    }

    public String dest() {
        // return the instruction until the =
        if ((instructionType() == "C_INSTRUCTION") && (currIns.contains("="))) {
            return currIns.substring(0, currIns.indexOf("="));
        }
        // return null if not C instruction
        return null;
    }

    public String comp() {
        // return the instruction from the = up until the ;
        if (instructionType() == "C_INSTRUCTION") {
            if (currIns.contains(";") && currIns.contains("=")) {
                return currIns.substring(currIns.indexOf("=") + 1,currIns.indexOf(";"));   
            } else if(currIns.contains("=")){
                return currIns.substring(currIns.indexOf("=") + 1); 
            }else if (currIns.contains(";")) {
                return currIns.substring(0, currIns.indexOf(";"));
            }
        }
        // return null if not C instruction
        return null;            
    }

    public String jump() {
        // return the instruction from the ;
        if ((instructionType() == "C_INSTRUCTION") && (currIns.contains(";"))) {
            return currIns.substring(currIns.indexOf(";") + 1);
        }
        // return null if not C instruction
        return null;
    }
}
