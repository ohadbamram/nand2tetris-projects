# Project 11: Compiler II

## Overview
This project extends the **Jack Compiler** to handle **code generation**. The compiler translates the abstract syntax tree (AST) of Jack code into **VM code**, which can then be executed by the virtual machine. This is the final step in the compilation process, enabling Jack programs to run on the Hack computer.

## Implementation
- The compiler is implemented in **Java**.
- It consists of five main components:
  1. **Main**: The entry point of the program, responsible for invoking the `JackCompiler` with the correct input file.
  2. **JackCompiler**: The core class that coordinates the code generation process.
  3. **Tokenizer**: Tokenizes the input Jack code into meaningful tokens (e.g., keywords, symbols, identifiers).
  4. **CompilationEngine**: Parses the tokens and generates VM code from the AST.
  5. **VMWriter**: Writes VM commands to the output file.

## Files
- `Main.java`: The entry point of the program, which invokes the `JackCompiler`.
- `JackCompiler.java`: The core class that coordinates the code generation process.
- `Tokenizer.java`: Tokenizes the input Jack code into meaningful tokens.
- `CompilationEngine.java`: Parses the tokens and generates VM code.
- `VMWriter.java`: Writes VM commands to the output file.

## How to Run
1. Compile and run the compiler using the following commands:
   ```bash
   # Compile the Java files
   javac Main.java

   # Run the compiler on a Jack file (e.g., Prog.jack)
   java Main Prog.jack
