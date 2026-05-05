# Java Expression Parser & Evaluator

## Overview
This program takes a math expression, converts it into tokens, builds an expression tree, prints the tree, and evaluates the result.

## Features
- Supports +, -, *, /
- Handles parentheses ()
- Correct operator precedence
- Supports unary minus (negative numbers)
- Prints expression tree
- Evaluates result

## How to Run

Compile:
javac FinalProdject.java

Run:
java FinalProdject

Example Input:
-3 + (5 * (2 + 8) - 4) * (7 - (3 + 2)) + 6 / (1 + 2)

## How It Works
1. Tokenizes input  
2. Parses into an expression tree  
3. Prints the tree  
4. Evaluates the result  

## Notes
- u- = unary minus (negative numbers)
- No decimal support (integers only)
- I also updated the code to handle ++ and -- operators
## Author
Stephen Papp
