import java.io.*;

public class CompilationEngine {
    private final JackTokenizer tokenizer;
    private final BufferedWriter writer;
    private int indentLevel = 0;

    // Constructor
    public CompilationEngine(String inputFile, String outputFile) throws IOException {
        tokenizer = new JackTokenizer(inputFile);
        writer = new BufferedWriter(new FileWriter(outputFile));
    }

    // Helper methods for indentation and writing XML tags
    private void writeIndented(String line) throws IOException {
        writer.write("  ".repeat(indentLevel) + line + "\n");
    }

    private void writeOpenTag(String tag) throws IOException {
        writeIndented("<" + tag + ">");
        indentLevel++;
    }

    private void writeCloseTag(String tag) throws IOException {
        indentLevel--;
        writeIndented("</" + tag + ">");
    }

    private void writeKeyword(String keyword) throws IOException {
        writeIndented("<keyword> " + keyword + " </keyword>");
    }

    private void writeSymbol(char symbol) throws IOException {
        writeIndented("<symbol> " + symbol + " </symbol>");
    }

    private void writeIdentifier(String identifier) throws IOException {
        writeIndented("<identifier> " + identifier + " </identifier>");
    }

    private void writeIntVal(int value) throws IOException {
        writeIndented("<integerConstant> " + value + " </integerConstant>");
    }

    private void writeStringVal(String value) throws IOException {
        writeIndented("<stringConstant> " + value + " </stringConstant>");
    }

    // Compilation methods
    public void compileClass() throws IOException {
        writeOpenTag("class");

        tokenizer.advance();
        writeKeyword("class");

        tokenizer.advance();
        writeIdentifier(tokenizer.identifier());

        tokenizer.advance();
        writeSymbol('{');

        while (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD) {
                String keyword = tokenizer.keyword();
                if (keyword.equals("static") || keyword.equals("field")) {
                    compileClassVarDec();
                } else if (keyword.equals("constructor") || keyword.equals("function") || keyword.equals("method")) {
                    compileSubroutine();
                }
            }
        }

        writeSymbol('}');
        writeCloseTag("class");
        writer.close();
    }

    private void compileClassVarDec() throws IOException {
        writeOpenTag("classVarDec");

        writeKeyword(tokenizer.keyword());

        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD) {
            writeKeyword(tokenizer.keyword());
        } else {
            writeIdentifier(tokenizer.identifier());
        }

        while (true) {
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol() == ';') {
                writeSymbol(';');
                break;
            } else if (tokenizer.tokenType() == JackTokenizer.TokenType.IDENTIFIER) {
                writeIdentifier(tokenizer.identifier());
            } else if (tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol() == ',') {
                writeSymbol(',');
            }
        }

        writeCloseTag("classVarDec");
    }

    private void compileSubroutine() throws IOException {
        writeOpenTag("subroutineDec");

        writeKeyword(tokenizer.keyword());

        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD) {
            writeKeyword(tokenizer.keyword());
        } else {
            writeIdentifier(tokenizer.identifier());
        }

        tokenizer.advance();
        writeIdentifier(tokenizer.identifier());

        tokenizer.advance();
        writeSymbol('(');
        compileParameterList();
        writeSymbol(')');

        compileSubroutineBody();
        writeCloseTag("subroutineDec");
    }

    private void compileParameterList() throws IOException {
        writeOpenTag("parameterList");

        while (true) {
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol() == ')') {
                break;
            } else if (tokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD) {
                writeKeyword(tokenizer.keyword());
            } else if (tokenizer.tokenType() == JackTokenizer.TokenType.IDENTIFIER) {
                writeIdentifier(tokenizer.identifier());
            } else if (tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol() == ',') {
                writeSymbol(',');
            }
        }

        writeCloseTag("parameterList");
    }

    private void compileSubroutineBody() throws IOException {
        writeOpenTag("subroutineBody");

        tokenizer.advance();
        writeSymbol('{');

        while (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD && tokenizer.keyword().equals("var")) {
                compileVarDec();
            } else {
                break;
            }
        }

        compileStatements();
        writeSymbol('}');
        writeCloseTag("subroutineBody");
    }

    private void compileVarDec() throws IOException {
        writeOpenTag("varDec");

        writeKeyword("var");

        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD) {
            writeKeyword(tokenizer.keyword());
        } else {
            writeIdentifier(tokenizer.identifier());
        }

        while (true) {
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol() == ';') {
                writeSymbol(';');
                break;
            } else if (tokenizer.tokenType() == JackTokenizer.TokenType.IDENTIFIER) {
                writeIdentifier(tokenizer.identifier());
            } else if (tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol() == ',') {
                writeSymbol(',');
            }
        }

        writeCloseTag("varDec");
    }

    private void compileStatements() throws IOException {
        writeOpenTag("statements");

        while (true) {
            if (tokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD) {
                switch (tokenizer.keyword()) {
                    case "let":
                        compileLet();
                        tokenizer.advance();
                        break;
                    case "if":
                        compileIf();
                        break;
                    case "while":
                        compileWhile();
                        tokenizer.advance();
                        break;
                    case "do":
                        compileDo();
                        tokenizer.advance();
                        break;
                    case "return":
                        compileReturn();
                        tokenizer.advance();
                        break;
                    default:
                        tokenizer.advance();
                        break;
                }
            } else {
                break;
            }
        }

        writeCloseTag("statements");
    }

    private void compileLet() throws IOException {
        writeOpenTag("letStatement");

        writeKeyword("let");
        tokenizer.advance();
        writeIdentifier(tokenizer.identifier());

        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol() == '[') {
            writeSymbol('[');
            tokenizer.advance();
            compileExpression();
            writeSymbol(']');
            tokenizer.advance();
        }

        writeSymbol('=');
        tokenizer.advance();
        compileExpression();
        writeSymbol(';');

        writeCloseTag("letStatement");
    }

    private void compileIf() throws IOException {
        writeOpenTag("ifStatement");

        writeKeyword("if");
        tokenizer.advance();
        writeSymbol('(');
        tokenizer.advance();
        compileExpression();
        writeSymbol(')');

        tokenizer.advance();
        writeSymbol('{');
        tokenizer.advance();
        compileStatements();
        writeSymbol('}');
        tokenizer.advance();

        if (tokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD && tokenizer.keyword().equals("else")) {
            writeKeyword("else");
            tokenizer.advance();
            writeSymbol('{');
            tokenizer.advance();
            compileStatements();
            writeSymbol('}');
            tokenizer.advance();
        }

        writeCloseTag("ifStatement");
    }

    private void compileWhile() throws IOException {
        writeOpenTag("whileStatement");

        writeKeyword("while");
        tokenizer.advance();
        writeSymbol('(');
        tokenizer.advance();
        compileExpression();
        writeSymbol(')');

        tokenizer.advance();
        writeSymbol('{');
        tokenizer.advance();
        if(!tokenizer.getCurrentToken().equals("}")){
            compileStatements();
        }
        writeSymbol('}');
        writeCloseTag("whileStatement");
    }

    private void compileDo() throws IOException {
        writeOpenTag("doStatement");

        writeKeyword("do");
        tokenizer.advance();
        writeIdentifier(tokenizer.identifier()); // Object or class name
        tokenizer.advance();
        compileSubroutineCall();
        writeSymbol(';');
        writeCloseTag("doStatement");
    }


    private void compileReturn() throws IOException {
        writeOpenTag("returnStatement");

        writeKeyword("return");
        tokenizer.advance();
        if (!tokenizer.getCurrentToken().equals(";")) {
            compileExpression();
        }
        writeSymbol(';');

        writeCloseTag("returnStatement");
    }


    private void compileExpression() throws IOException {
        writeOpenTag("expression");
        compileTerm();
        while(true){
            char op = tokenizer.getCurrentToken().charAt(0);
            if(isOperator(op)){
                if(op == '>'){
                    writeIndented("<symbol> " + "&gt;" + " </symbol>");
                }
                else if(op == '<'){
                    writeIndented("<symbol> " + "&lt;" + " </symbol>");
                }
                else if(op == '&'){
                    writeIndented("<symbol> " + "&amp;" + " </symbol>");
                }
                else{
                    writeSymbol(op);
                }
                tokenizer.advance();
                compileTerm();
            }
            else{
                break;
            }
        }
        writeCloseTag("expression");
    }

    private void compileTerm() throws IOException {
        writeOpenTag("term");
        if (tokenizer.tokenType() == JackTokenizer.TokenType.INT_CONST) {
            writeIntVal(tokenizer.intVal());
            tokenizer.advance();
        } else if (tokenizer.tokenType() == JackTokenizer.TokenType.STRING_CONST) {
            writeStringVal(tokenizer.stringVal());
            tokenizer.advance();
        } else if (tokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD) {
            writeKeyword(tokenizer.keyword());
            tokenizer.advance();
        } else if (tokenizer.tokenType() == JackTokenizer.TokenType.IDENTIFIER) {
            writeIdentifier(tokenizer.identifier());
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol() == '[') {
                writeSymbol('[');
                tokenizer.advance();
                compileExpression();
                writeSymbol(']');
                tokenizer.advance();
            } else if (tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && (tokenizer.symbol() == '(' || tokenizer.symbol() == '.')) {
                compileSubroutineCall();
            }
        } else if (tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol() == '(') {
            writeSymbol('(');
            tokenizer.advance();
            compileExpression();
            writeSymbol(')');
            tokenizer.advance();
        } else if (tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && isUnaryOp(tokenizer.symbol())) {
            writeSymbol(tokenizer.symbol());
            tokenizer.advance();
            compileTerm();
        }

        writeCloseTag("term");
    }

    private void compileSubroutineCall() throws IOException {
        if (tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol() == '.') {
            writeSymbol('.');
            tokenizer.advance();
            writeIdentifier(tokenizer.identifier());
            tokenizer.advance();
        }


        writeSymbol('(');
        tokenizer.advance();
        compileExpressionList();
        writeSymbol(')');
        tokenizer.advance();
    }

    private void compileExpressionList() throws IOException {
        writeOpenTag("expressionList");
        if (!tokenizer.getCurrentToken().equals(")")) {
            compileExpression();
            while (tokenizer.getCurrentToken().equals(",")) {
                writeSymbol(',');
                tokenizer.advance();
                compileExpression();
            }
        }

        writeCloseTag("expressionList");
    }

    private boolean isOperator(char symbol) {
        return "+-*/&|<>=".indexOf(symbol) != -1;
    }

    private boolean isUnaryOp(char symbol) {
        return "-~".indexOf(symbol) != -1;
    }
}