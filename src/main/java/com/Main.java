package com;

import com.io.TerminalScanner;
import com.io.TextFileReader;

public class Main {
    public static void main(String[] args) {

        /*
        TextFileReader textFileReader = new TextFileReader("src/main/java/com/io/textEditor.txt");
        textFileReader.readFile();
        Program program = new Program();
        program.execute(textFileReader.getContent());
        */

        TerminalScanner terminalScanner = new TerminalScanner();
        terminalScanner.scanConsole();

        if (!terminalScanner.getInput().isEmpty()) {
            Program program = new Program();
            program.execute(terminalScanner.getInput());
        }

    }
}
