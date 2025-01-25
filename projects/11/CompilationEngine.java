import java.io.IOException;

public class CompilationEngine {
    private final JackTokenizer tokenizer;
    private final VMWriter vmWriter;
    private final SymbolTable symbolTable;
    private String className;
    private String subroutineName;
    private String subroutineType;
    private int labelCounter = 0;

    // Constructor
    public CompilationEngine(String inputFile, String outputFile) throws IOException {
        tokenizer = new JackTokenizer(inputFile);
        vmWriter = new VMWriter(outputFile);
        symbolTable = new SymbolTable();
    }

    // Compile a class
    public void compileClass() throws IOException {
        tokenizer.advance(); // class
        tokenizer.advance(); // class name
        className = tokenizer.identifier();
        tokenizer.advance(); // {
        tokenizer.advance(); // keyword
        // Compile class-level variable declarations (static and field)
        while (tokenizer.hasMoreTokens() && (tokenizer.keyword().equals("static") || tokenizer.keyword().equals("field"))) {
            compileClassVarDec();
        }
        // Compile subroutines (methods, functions, constructors)
        while (tokenizer.hasMoreTokens() && (tokenizer.keyword().equals("function")|| tokenizer.keyword().equals("constructor") || tokenizer.keyword().equals("method"))) {
            compileSubroutine();
        }
        tokenizer.advance(); // Skip '}'
        vmWriter.close(); // Close the output file
    }

    // Compile a class-level variable declaration
    private void compileClassVarDec() throws IOException {
        String kind = tokenizer.keyword(); // 'static' or 'field'
        tokenizer.advance();
        String type = tokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD ? tokenizer.keyword() : tokenizer.identifier();
        tokenizer.advance();

        // Define variables in the symbol table
        while (true) {
            String varName = tokenizer.identifier();
            symbolTable.define(varName, type, SymbolTable.Kind.getKind(kind));
            tokenizer.advance();

            if (tokenizer.symbol() == ';') {
                break;
            }
            tokenizer.advance(); // Skip ','
        }
        tokenizer.advance(); // Skip ';'
    }

    // Compile a subroutine (method, function, constructor)
    private void compileSubroutine() throws IOException {
        symbolTable.reset(); // Reset the subroutine-level symbol table
        subroutineType = tokenizer.keyword(); // 'constructor', 'function', or 'method'
        if(subroutineType.equals("method")){
            symbolTable.incArg();
        }
        tokenizer.advance(); // function return type
        String returnType = tokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD ? tokenizer.keyword() : tokenizer.identifier();
        tokenizer.advance(); // method name
        subroutineName = tokenizer.identifier(); // Subroutine name
        tokenizer.advance(); // (
        tokenizer.advance(); // params or )

        // Compile parameter list
        compileParameterList();
        //if there are params advance beyond {
        if(tokenizer.symbol() == '{'){
            tokenizer.advance();
        }
        // Compile subroutine body
        compileSubroutineBody();
    }

    // Compile a parameter list
    private void compileParameterList() throws IOException {
        while (!tokenizer.getCurrentToken().equals(")")) {
            String type = tokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD ? tokenizer.keyword() : tokenizer.identifier();
            tokenizer.advance();
            String varName = tokenizer.identifier();
            symbolTable.define(varName, type, SymbolTable.Kind.ARG);
            tokenizer.advance();

            if (tokenizer.getCurrentToken().equals(",")) {
                tokenizer.advance(); // Skip ','
            }
        }
        tokenizer.advance();
    }

    // Compile a subroutine body
    private void compileSubroutineBody() throws IOException {

        // Compile variable declarations
        while (tokenizer.keyword().equals("var")) {
            compileVarDec();
        }


        // Write VM function declaration
        vmWriter.writeFunction(className + "." + subroutineName, symbolTable.varCount(SymbolTable.Kind.VAR));

        // Handle constructor and method-specific initialization
        if (subroutineType.equals("constructor")) {
            // Allocate memory for the object
            vmWriter.writePush("constant", symbolTable.varCount(SymbolTable.Kind.FIELD));
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop("pointer", 0); // Set THIS to the base address of the object
        } else if (subroutineType.equals("method")) {
            // Set THIS to the first argument (the object)
            vmWriter.writePush("argument", 0);
            vmWriter.writePop("pointer", 0);
        }

        // Compile statements
        compileStatements();
        tokenizer.advance(); // Skip '}'
    }

    // Compile a variable declaration
    private void compileVarDec() throws IOException {
        tokenizer.advance(); // type
        String type = tokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD ? tokenizer.keyword() : tokenizer.identifier();
        tokenizer.advance(); // var name

        // Define variables in the symbol table
        while (true) {
            String varName = tokenizer.identifier();
            symbolTable.define(varName, type, SymbolTable.Kind.VAR);
            tokenizer.advance(); // ; or ,

            if (tokenizer.symbol() == ';') {
                break;
            }
            tokenizer.advance(); // Skip ','
        }
        tokenizer.advance(); // Skip ';'
    }

    // Compile statements
    private void compileStatements() throws IOException {
        while (true) {
            if (tokenizer.getCurrentToken().equals("let")) {
                compileLet();
            } else if (tokenizer.getCurrentToken().equals("if")) {
                compileIf();
            } else if (tokenizer.getCurrentToken().equals("while")) {
                compileWhile();
            } else if (tokenizer.getCurrentToken().equals("do")) {
                compileDo();
            } else if (tokenizer.getCurrentToken().equals("return")) {
                compileReturn();
            } else {
                break;
            }
        }
    }

    // Compile a let statement
    private void compileLet() throws IOException {
        tokenizer.advance(); // name
        String varName = tokenizer.identifier(); // game
        SymbolTable.Kind kind = symbolTable.kindOf(varName); // var
        int index = symbolTable.indexOf(varName); // 0
        tokenizer.advance(); // =

        // Handle array access (e.g., let arr[i] = expr)
        if (tokenizer.symbol() == '[') {
            tokenizer.advance(); // Skip '['
            compileExpression(); // Compile the index expression
            vmWriter.writePush(SymbolTable.getSegment(kind), index); // Push the array base address
            vmWriter.writeArithmetic("add"); // Compute the element address
            tokenizer.advance(); // Skip ']'
            tokenizer.advance(); // Skip '='
            compileExpression(); // Compile the right-hand side expression
            vmWriter.writePop("temp", 0); // Store the result in temp 0
            vmWriter.writePop("pointer", 1); // Set THAT to the element address
            vmWriter.writePush("temp", 0); // Push the result
            vmWriter.writePop("that", 0); // Store the result in the array element
        } else {
            tokenizer.advance(); // Skip '='
            compileExpression(); // Compile the right-hand side expression
            vmWriter.writePop(SymbolTable.getSegment(kind), index); // Store the result in the variable
        }
        tokenizer.advance(); // Skip ';'
    }

    // Compile an if statement
    private void compileIf() throws IOException {
        tokenizer.advance(); // Skip 'if'
        tokenizer.advance(); // Skip '('
        compileExpression(); // Compile the condition
        tokenizer.advance(); // Skip ')'
        tokenizer.advance(); // Skip '{'

        String labelElse = className + "_" + ++labelCounter;
        String labelEnd = className + "_" + (labelCounter-1);
        labelCounter++;
        vmWriter.writeNot();
        vmWriter.writeIf(labelElse); // Jump to else if condition is false
        compileStatements(); // Compile the if block
        vmWriter.writeGoto(labelEnd); // Jump to end after if block
        vmWriter.writeLabel(labelElse); // Else label
        if(tokenizer.getCurrentToken().equals("}")){
            tokenizer.advance();
        }
        if (tokenizer.getCurrentToken().equals("else")) {
            tokenizer.advance(); // Skip 'else'
            tokenizer.advance(); // Skip '{'
            compileStatements(); // Compile the else block
            tokenizer.advance(); // Skip '}'
        }


        vmWriter.writeLabel(labelEnd); // End label

    }

    // Compile a while statement
    private void compileWhile() throws IOException {
        tokenizer.advance(); // Skip 'while'
        String labelStart = className + "_" + labelCounter++;
        String labelEnd = className + "_" +labelCounter++;

        vmWriter.writeLabel(labelStart); // Start label
        tokenizer.advance(); // Skip '('
        compileExpression(); // Compile the condition
        vmWriter.writeNot();
        vmWriter.writeIf(labelEnd); // Jump to end if condition is false
        tokenizer.advance(); // Skip ')'
        tokenizer.advance(); // Skip '{'
        compileStatements(); // Compile the while block
        vmWriter.writeGoto(labelStart); // Jump back to start
        vmWriter.writeLabel(labelEnd); // End label
        tokenizer.advance(); // Skip '}'
    }

    // Compile a do statement
    private void compileDo() throws IOException {
        tokenizer.advance(); // Skip 'do'
        tokenizer.advance(); // skip name
        compileSubroutineCall(); // Compile the subroutine call
        vmWriter.writePop("temp", 0); // Discard the return value
        tokenizer.advance(); // Skip ';'
    }

    // Compile a return statement
    private void compileReturn() throws IOException {
        tokenizer.advance(); // Skip 'return'
        if (!tokenizer.getCurrentToken().equals(";")) {
            compileExpression(); // Compile the return expression
        } else {
            vmWriter.writePush("constant", 0); // Default return value for void functions
        }
        vmWriter.writeReturn(); // Write the return command
        if (tokenizer.getCurrentTokenIndex() < tokenizer.getSize() - 2){
            vmWriter.writeNewline();
        }
        tokenizer.advance(); // Skip ';'
    }

    // Compile an expression
    private void compileExpression() throws IOException {
        compileTerm();
        while (isOperator(tokenizer.symbol())) {
            String op = tokenizer.symbol() == '>' ? "gt" :
                    tokenizer.symbol() == '<' ? "lt" :
                            tokenizer.symbol() == '&' ? "and" :
                                    tokenizer.symbol() == '|' ? "or" :
                                            String.valueOf(tokenizer.symbol());
            tokenizer.advance(); // Skip the operator
            compileTerm();
            vmWriter.writeArithmetic(op); // Write the arithmetic operation
        }
    }

    // Compile a term
    private void compileTerm() throws IOException {
        if (tokenizer.tokenType() == JackTokenizer.TokenType.INT_CONST) {
            vmWriter.writePush("constant", tokenizer.intVal());
            tokenizer.advance();
        } else if (tokenizer.tokenType() == JackTokenizer.TokenType.STRING_CONST) {
            String stringVal = tokenizer.stringVal();
            vmWriter.writePush("constant", stringVal.length());
            vmWriter.writeCall("String.new", 1); // Create a new string object
            for (char ch : stringVal.toCharArray()) {
                vmWriter.writePush("constant", (int) ch); // Push each character's ASCII value
                vmWriter.writeCall("String.appendChar", 2); // Append the character to the string
            }
            tokenizer.advance();
        } else if (tokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD) {
            switch (tokenizer.getCurrentToken()){
                case "this":
                    vmWriter.writePush("pointer", 0);
                    break;
                case "true":
                    vmWriter.writePush("constant", 1);
                    vmWriter.writeNeg();
                    break;
                case "false":
                    vmWriter.writePush("constant", 0);
                    break;
                default:
                   vmWriter.writePush("constant", 0);
            }

            tokenizer.advance();
        } else if (tokenizer.tokenType() == JackTokenizer.TokenType.IDENTIFIER) {
            String identifier = tokenizer.identifier();
            SymbolTable.Kind kind = symbolTable.kindOf(identifier);
            int index = symbolTable.indexOf(identifier);
            tokenizer.advance();

            if (tokenizer.symbol() == '[') {
                // Handle array access
                tokenizer.advance(); // Skip '['
                compileExpression(); // Compile the index expression
                vmWriter.writePush(SymbolTable.getSegment(kind), index); // Push the array base address
                vmWriter.writeArithmetic("add"); // Compute the element address
                vmWriter.writePop("pointer", 1); // Set THAT to the element address
                vmWriter.writePush("that", 0); // Push the array element
                tokenizer.advance(); // Skip ']'
            } else if (tokenizer.symbol() == '(' || tokenizer.symbol() == '.') {
                // Handle subroutine calls
                compileSubroutineCall();
            } else {
                // Handle variable access
                vmWriter.writePush(SymbolTable.getSegment(kind), index);
            }
        } else if (tokenizer.symbol() == '(') {
            // Handle parenthesized expressions
            tokenizer.advance(); // Skip '('
            compileExpression();
            tokenizer.advance(); // Skip ')'
        } else if (isUnaryOp(tokenizer.symbol())) {
            // Handle unary operators
            String op = tokenizer.symbol() == '-' ? "neg" : "not";
            tokenizer.advance(); // Skip the unary operator
            compileTerm();
            vmWriter.writeArithmetic(op);
        }
    }

    // Compile a subroutine call
    private void compileSubroutineCall() throws IOException {
        String funcName = tokenizer.getPreviousToken();
        int nArgs = 0;
        // Handle method calls
        if(tokenizer.getCurrentToken().equals(".")){
            tokenizer.advance(); // Skip '.'
            String methodName = tokenizer.identifier();
            tokenizer.advance(); // Skip the method name
            tokenizer.advance(); // Skip '('
            SymbolTable.Kind kind = symbolTable.kindOf(funcName);
            if (kind != SymbolTable.Kind.NONE) {
                // It's an object reference (e.g., game.run())
                // Push the object reference (this) onto the stack
                vmWriter.writePush(SymbolTable.getSegment(kind), symbolTable.indexOf(funcName));
                funcName = symbolTable.typeOf(funcName);
                nArgs = 1; // The implicit 'this' argument
            }
            nArgs += compileExpressionList();
            tokenizer.advance(); // Skip ')'
            vmWriter.writeCall(funcName + "." + methodName, nArgs);
        }
        else{
            nArgs = 1;
            vmWriter.writePush("pointer", 0);
            tokenizer.advance();
            nArgs += compileExpressionList();
            tokenizer.advance();
            vmWriter.writeCall(className + "." + funcName, nArgs);
        }


    }

    // Compile an expression list
    private int compileExpressionList() throws IOException {
        int nArgs = 0;
        if (!tokenizer.getCurrentToken().equals(")")) {
            compileExpression();
            nArgs++;
            while (tokenizer.getCurrentToken().equals(",")) {
                tokenizer.advance(); // Skip ','
                compileExpression();
                nArgs++;
            }
        }
        return nArgs;
    }

    // Check if a symbol is an operator
    private boolean isOperator(char symbol) {
        return "+-*/&|<>=".indexOf(symbol) != -1;
    }

    // Check if a symbol is a unary operator
    private boolean isUnaryOp(char symbol) {
        return "-~".indexOf(symbol) != -1;
    }

}