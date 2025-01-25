import java.io.*;
import java.util.*;

public class HackAssembler {
    // fields
    private BufferedWriter fileWriter;
    private SymbolTable symbolTable;
    private Parser parser;
    private Code code;
    private static int addressCounter = 16;

    // constructor
    public HackAssembler (String inputFile) throws IOException {
        // initialize all the fields
        symbolTable = new SymbolTable();
        code = new Code();
        parser = new Parser(inputFile);
        // create new output file
        String outputFile = inputFile.replace(".asm", ".Hack");
        fileWriter = new BufferedWriter(new FileWriter(outputFile));
        // do the first pass then reset and do the second pass
        firstPass();
        parser.closeReader();
        parser = new Parser(inputFile);
        secondPass();
    }

    public void firstPass() throws IOException{
        // initialize counter to set the labels for each row
        int count = 0;
        while (parser.hasMoreLines()) {
            parser.advance();
            // skip if currIns is null
            if (parser.getCurrentInstruction() == null) {
                continue;
            }
            // add L inststructions to the symbol table
            if (parser.instructionType().equals("L_INSTRUCTION")) {
                if (!symbolTable.contains(parser.symbol())) {
                    symbolTable.addEntry(parser.symbol(), count);
                    count--;
                }
            }
            count++;
        }
    }

    public void secondPass() throws IOException{
        while (parser.hasMoreLines()) {
            parser.advance();
            // skip if currIns is null
            if (parser.getCurrentInstruction() == null) {
                continue;
            }
            String instruction = "";
            // handle A instructions
            if (parser.instructionType().equals("A_INSTRUCTION")) {
                int address = 0;
                try {
                    address = Integer.parseInt(parser.symbol());
                } catch (NumberFormatException e) {
                    if (!symbolTable.contains(parser.symbol())) {
                        symbolTable.addEntry(parser.symbol(), addressCounter);
                        address = (symbolTable.getAddress(parser.symbol()));
                        addressCounter++;
                    } else {
                        address = (symbolTable.getAddress(parser.symbol()));
                    }
                } finally {
                    instruction = String.format("%16s", Integer.toBinaryString(address)).replace(' ', '0');
                }
            } else if (parser.instructionType().equals("C_INSTRUCTION")) { // handle C instructions
                String dest = code.dest(parser.dest());
                String comp = code.comp(parser.comp());
                String jump = code.jump(parser.jump());
                instruction = "111" + comp + dest + jump;
            } else { // skip L instructions
                continue;
            }
            fileWriter.write(instruction + "\n");
        }
        fileWriter.close();
    }
}