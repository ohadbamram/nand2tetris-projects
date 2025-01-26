# Project 6: Assembler

## Overview
This project involves building an **assembler** for the Hack assembly language. The assembler translates symbolic assembly code into binary machine code that can be executed by the Hack computer.

## Implementation
- The assembler is implemented in **Java**.
- It consists of three main components:
  1. **Main**: The entry point of the program, responsible for reading the input file and coordinating the assembly process.
  2. **Parser**: Parses the assembly code, breaking it into its components (e.g., instruction type, symbols, labels).
  3. **CodeWriter**: Translates parsed instructions into binary machine code.
- The assembler handles **symbolic instructions** (e.g., `@LOOP`) and **labels** (e.g., `(LOOP)`).
- The output is a `.hack` file containing 16-bit binary instructions.

## Files
- `Main.java`: The entry point of the assembler.
- `Parser.java`: Parses assembly instructions into their components.
- `CodeWriter.java`: Translates parsed instructions into binary machine code.
- `SymbolTable.java`: Manages symbols and labels.

## How to Run
1. Compile and run the assembler using the following commands:
   ```bash
   # Compile the Java files
   javac Main.java

   # Run the assembler on an assembly file (e.g., Prog.asm)
   java Main Prog.asm
