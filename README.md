# Compilers
This repository contains my work for the Compilers class at University of Salerno. The final project is a LALR(1) parser implementation with type checking, scoping implementation and intermediate language generation (C) for Toy language. Note that the project is a partial implementation and not a perfect compiler for Toy, therefore many grammar specification may miss and differ from the original Toy language specification.
The src folder contains all the classes needed to compile and execute the project. The java-cup-runtime is a jar needed to use CUP and JFLEX.
To implement syntax checking, semantics checking and intermediate code generation I have used the Visitor Design Pattern.
The C code is generated when executing the project on a Toy file without semantics or syntax errors.
The other folder, "Hand Coded Lexer And Parser", contains lexer and parser implementation written without the help of JFLEX and CUP. This implementation works on a very small language.
