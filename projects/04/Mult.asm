// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
// The algorithm is based on repetitive addition.


    // zero R2
    @R2         
    M=0
    // if R0 = 0 jump to end
    @R0         
    D=M
    @END
    D;JEQ
    // if R1 = 0 jump to end 
    @R1         
    D=M
    @END
    D;JEQ
    // copy R0 to temp to be able to change it every iteration
    @R0         
    D=M
    @temp
    M=D
// each iteration, add R1 and R2, running R0=temp times
(LOOP)     
    @R1         
    D=M
    @R2
    M=D+M
    @temp
    M=M-1
    // if temp > 0 go back to LOOP
    @temp     
    D=M
    @LOOP
    D;JGT   
(END)    
    @END
    0;JMP