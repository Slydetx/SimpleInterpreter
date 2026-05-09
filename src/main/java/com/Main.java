package com;

import com.io.TerminalScanner;

/**
 * Entry point of the interpreter.
 * Reads a program from standard input and executes it.
 * Input is terminated by a blank line.
 */
public class Main {
    public static void main(String[] args) {

        TerminalScanner terminalScanner = new TerminalScanner();
        terminalScanner.scanConsole();

        if (!terminalScanner.getInput().isEmpty()) {
            Program program = new Program();
            program.execute(terminalScanner.getInput());
        }

    }
}
