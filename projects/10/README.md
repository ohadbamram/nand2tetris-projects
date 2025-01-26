# Project 10: Compiler I

## Overview
This project involves building the **syntax analysis** part of the **Jack Compiler**. The compiler parses Jack code (a high-level object-oriented language) and generates an **abstract syntax tree (AST)** represented in XML format. This is the first step in translating Jack code into VM code.

## Implementation
- The compiler is implemented in **Java**.
- It consists of three main components:
  1. **Main**: The entry point of the program, responsible for invoking the `JackAnalyzer` with the correct input file.
  2. **JackAnalyzer**: The core class that coordinates the syntax analysis process.
  3. **Tokenizer**: Tokenizes the input Jack code into meaningful tokens (e.g., keywords, symbols, identifiers).
  4. **CompilationEngine**: Parses the tokens and generates the abstract syntax tree (AST) in XML format.

## Files
- `Main.java`: The entry point of the program, which invokes the `JackAnalyzer`.
- `JackAnalyzer.java`: The core class that coordinates the syntax analysis process.
- `Tokenizer.java`: Tokenizes the input Jack code into meaningful tokens.
- `CompilationEngine.java`: Parses the tokens and generates the AST in XML format.

## How to Run
1. Compile and run the compiler using the following commands:
   ```bash
   # Compile the Java files
   javac Main.java

   # Run the compiler on a Jack file (e.g., Prog.jack)
   java Main Prog.jack
