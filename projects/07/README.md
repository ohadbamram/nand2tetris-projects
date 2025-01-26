# Project 7: Virtual Machine I

## Overview
This project involves building the first part of a **virtual machine (VM) translator**. The VM translator translates high-level VM commands (e.g., `push`, `pop`, `add`) into Hack assembly code.

## Implementation
- The VM translator is implemented in **Java**.
- It consists of four main components:
  1. **Main**: The entry point of the program, responsible for invoking the `VMTranslator` with the correct input file.
  2. **VMTranslator**: The core class that coordinates the translation process.
  3. **Parser**: Parses VM commands into their components (e.g., command type, memory segment, value).
  4. **CodeWriter**: Translates parsed VM commands into Hack assembly code.
- The translator handles **stack arithmetic** commands (e.g., `add`, `sub`, `neg`) and **memory access** commands (e.g., `push`, `pop`).
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
