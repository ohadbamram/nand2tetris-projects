// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Fill.asm

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, 
// the screen should be cleared.

//Start at current
(RESET)     
    @SCREEN
    D=A
    @current
    M=D
//check the input, blacken if a key is pressed, else whiten
(INPUT)     
    @KBD
    D=M
    @BLACK
    D;JGT
    @WHITE  
    D;JEQ
    @INPUT
    0;JMP
(BLACK)   
    @selector
    M=-1
    @COLOR
    0;JMP
(WHITE)  
    @selector
    M=0
    @COLOR
    0;JMP
//change the color iteratively
(COLOR)  
    @selector
    D=M
    @current
    A=M
    M=D
    @current
    D=M
    @current
    M=M+1  
    @COLOR
    D;JGT
    @RESET
    0;JMP
