import java.io.*;
import java.util.*;

public class JackTokenizer {
    private final List<String> tokens;
    private int currentTokenIndex;
    private String currentToken;
    public boolean inMultiLineComment;
    // Token types
    public enum TokenType {
        KEYWORD, SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST
    }

    // Keywords
    private static final Set<String> KEYWORDS = Set.of(
            "class", "constructor", "function", "method", "field", "static", "var",
            "int", "char", "boolean", "void", "true", "false", "null", "this",
            "let", "do", "if", "else", "while", "return"
    );

    // Symbols
    private static final Set<Character> SYMBOLS = Set.of(
            '{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '|', '<', '>', '=', '~'
    );

    // Constructor
    public JackTokenizer(String inputFile) throws IOException {
        tokens = new ArrayList<>();
        currentTokenIndex = 0;
        currentToken = null;
        inMultiLineComment = false;
        // Read the input file and tokenize it
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line;
        while ((line = reader.readLine()) != null) {
            line = removeComments(line,inMultiLineComment).trim();
            if (line.isEmpty()) continue;

            // Tokenize the line
            tokenizeLine(line);
        }
        reader.close();
    }

    // Remove comments from a line
    private String removeComments(String line, boolean in) {
        // Handle multi-line comments
        if (in) {
            int endCommentIndex = line.indexOf("*/");
            if (endCommentIndex != -1) {
                // Multi-line comment ends on this line
                inMultiLineComment = false;
                line = line.substring(endCommentIndex + 2).trim();
            } else {
                // Multi-line comment continues, skip the entire line
                return "";
            }
        }

        // Check for the start of a multi-line comment
        int startCommentIndex = line.indexOf("/**");
        if (startCommentIndex != -1) {
            // Multi-line comment starts on this line
            inMultiLineComment = true;
            int endCommentIndex = line.indexOf("*/");
            if(endCommentIndex != -1){
                inMultiLineComment = false;
            }

            line = line.substring(0, startCommentIndex).trim();


        }

        // Remove single-line comments
        int singleLineCommentIndex = line.indexOf("//");
        if (singleLineCommentIndex != -1) {
            line = line.substring(0, singleLineCommentIndex).trim();
        }

        return line;
    }

    // Tokenize a line into tokens
    private void tokenizeLine(String line) {
        StringBuilder currentTokenBuilder = new StringBuilder();
        boolean inString = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                if (inString) {
                    // End of string
                    currentTokenBuilder.append(ch);
                    tokens.add(currentTokenBuilder.toString());
                    currentTokenBuilder.setLength(0);
                    inString = false;
                } else {
                    // Start of string
                    inString = true;
                    currentTokenBuilder.append(ch);
                }
            } else if (inString) {
                // Inside a string
                currentTokenBuilder.append(ch);
            } else if (SYMBOLS.contains(ch)) {
                // Handle symbols
                if (!currentTokenBuilder.isEmpty()) {
                    tokens.add(currentTokenBuilder.toString());
                    currentTokenBuilder.setLength(0);
                }
                tokens.add(String.valueOf(ch));
            } else if (Character.isWhitespace(ch)) {
                // Handle whitespace
                if (!currentTokenBuilder.isEmpty()) {
                    tokens.add(currentTokenBuilder.toString());
                    currentTokenBuilder.setLength(0);
                }
            } else {
                // Build the current token
                currentTokenBuilder.append(ch);
            }
        }

        // Add the last token if any
        if (!currentTokenBuilder.isEmpty()) {
            tokens.add(currentTokenBuilder.toString());
        }

    }

    // Check if there are more tokens
    public boolean hasMoreTokens() {
        return currentTokenIndex < tokens.size();
    }

    // Advance to the next token
    public void advance() {
        if (hasMoreTokens()) {
            currentToken = tokens.get(currentTokenIndex);
            currentTokenIndex++;
        } else {
            currentToken = null;
        }
    }

    // Get the type of the current token
    public TokenType tokenType() {
        if (KEYWORDS.contains(currentToken)) {
            return TokenType.KEYWORD;
        } else if (SYMBOLS.contains(currentToken.charAt(0))) {
            return TokenType.SYMBOL;
        } else if (currentToken.matches("\\d+")) {
            return TokenType.INT_CONST;
        } else if (currentToken.startsWith("\"") && currentToken.endsWith("\"")) {
            return TokenType.STRING_CONST;
        } else {
            return TokenType.IDENTIFIER;
        }
    }

    // Get the keyword if the current token is a keyword
    public String keyword() {
        if (tokenType() == TokenType.KEYWORD) {
            return currentToken.trim();
        }
        throw new IllegalStateException("Current token is not a keyword.");
    }

    // Get the symbol if the current token is a symbol
    public char symbol() {
        if (tokenType() == TokenType.SYMBOL) {
            return currentToken.charAt(0);
        }
        throw new IllegalStateException("Current token is not a symbol.");
    }

    // Get the identifier if the current token is an identifier
    public String identifier() {
        if (tokenType() == TokenType.IDENTIFIER) {
            return currentToken;
        }
        throw new IllegalStateException("Current token is not an identifier.");
    }

    // Get the integer value if the current token is an integer
    public int intVal() {
        if (tokenType() == TokenType.INT_CONST) {
            return Integer.parseInt(currentToken);
        }
        throw new IllegalStateException("Current token is not an integer constant.");
    }

    // Get the string value if the current token is a string
    public String stringVal() {
        if (tokenType() == TokenType.STRING_CONST) {
            return currentToken.substring(1, currentToken.length() - 1);
        }
        throw new IllegalStateException("Current token is not a string constant.");
    }

    public String getCurrentToken() {
        return currentToken;
    }

    public String getPreviousToken() {
        if(currentTokenIndex > 0){
            return tokens.get(currentTokenIndex-2);
        }
        return null;
    }


    public String getNextToken() {
        if(currentTokenIndex < tokens.size() - 1){
            return tokens.get(currentTokenIndex+1);
        }
        return null;
    }

    public int getSize() {
        return tokens.size();
    }

    public int getCurrentTokenIndex(){
        return currentTokenIndex;
    }

}