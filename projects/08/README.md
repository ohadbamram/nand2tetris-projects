# Project 8: Virtual Machine II

## Overview
This project extends the **virtual machine (VM) translator** to handle **program flow** commands (e.g., `label`, `goto`, `if-goto`) and **function calling** commands (e.g., `function`, `call`, `return`). The VM translator translates high-level VM commands into Hack assembly code.

## Implementation
- The VM translator is implemented in **Java**.
- It consists of four main components:
  1. **Main**: The entry point of the program, responsible for invoking the `VMTranslator` with the correct input file.
  2. **VMTranslator**: The core class that coordinates the translation process.
  3. **Parser**: Parses VM commands into their components (e.g., command type, memory segment, value).
  4. **CodeWriter**: Translates parsed VM commands into Hack assembly code.
- The translator now handles:
  - **Program flow** commands: `label`, `goto`, `if-goto`.
  - **Function calling** commands: `function`, `call`, `return`.
- The output is a `.asm` file containing Hack assembly code.

## Files
- `Main.java`: The entry point of the program, which invokes the `VMTranslator`.
- `VMTranslator.java`: The core class that coordinates the translation process.
- `Parser.java`: Parses VM commands into their components.
- `CodeWriter.java`: Translates parsed VM commands into Hack assembly code.

## How to Run
1. Compile and run the VM translator using the following commands:
   ```bash
   # Compile the Java files
   javac Main.java

   # Run the VM translator on a VM file (e.g., Prog.vm)
   java Main Prog.vm
