# Project 9: XO Game

## Overview
This project is a **two-player XO (Tic-Tac-Toe) game** implemented in **Jack**, the high-level language used in the Nand2Tetris course. Players take turns marking 'X' or 'O' on a 3x3 grid by pressing keys 1-9. The game detects winning conditions and announces the winner or a tie when the board is full.

## Motivation
We chose to create an XO game because it's a classic game that reminds us of our childhood. It was a fun challenge to implement a childhood game on a computer we built ourselves.

## Architecture
The game consists of two main classes:
1. **XO.jack**: Handles the game logic, including the board state, player turns, and win/tie conditions.
2. **Main.jack**: Initializes the game and runs the main loop until a win or tie is detected.

### **XO.jack**
This class contains the core logic of the game. Below are the key fields and methods:

#### **Fields**
- **Array squares**: Represents the 3x3 game board. Values: `0` (empty), `1` ('O'), `2` ('X').
- **boolean playerO**: Tracks the current player: `true` for 'O', `false` for 'X'.

#### **Key Methods**
1. **constructor XO new()**:
   - Initializes the game: creates the `squares` array, draws the board, and displays instructions.

2. **method void selection()**:
   - Handles player input (keys 1-9) and validates moves.
   - Calls `nextTurn()` to update the board and switch players.

3. **method void nextTurn(int location, int x, int y, boolean playerO)**:
   - Places 'O' or 'X' on the board based on the current player.
   - Calls `drawO()` or `drawX()` to render the move.

4. **method void drawBoard()**:
   - Draws the 3x3 grid using `Screen.drawLine()`.

5. **method void startingText()**:
   - Displays game instructions and the initial board layout.

6. **method boolean isFull()**:
   - Checks if the board is full (no empty squares).

7. **Drawing Methods**:
   - **drawO(int x, int y)**: Draws an 'O' at the specified coordinates using `Screen.drawCircle()`.
   - **drawX(int x, int y)**: Draws an 'X' at the specified coordinates using `Screen.drawLine()`.

8. **method boolean equalTriplet(int x, int y, int z)**:
   - Checks if three values form a winning line. Announces the winner if a winning condition is met.

9. **method boolean win()**:
   - Checks all possible winning conditions (rows, columns, diagonals).
   - Returns `true` if a player wins.

### **Main.jack**
This class initializes the game and runs the main loop until a win or tie is detected.

## How It Works
1. The game starts by initializing the board and displaying instructions.
2. Players take turns pressing keys (1-9) to place 'X' or 'O' on the board.
3. After each move, the game checks for a win or tie using `win()` and `isFull()`.
4. If a winning condition is met, the game announces the winner. If the board is full, it declares a tie.

## How to Run
1. Compile the Jack files using the **Jack Compiler**:
   ```bash
   JackCompiler XO.jack Main.jack
