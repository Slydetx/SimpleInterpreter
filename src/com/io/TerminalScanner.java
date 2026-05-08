package com.io;

import java.util.Scanner;

public class TerminalScanner {
    private String input;

    public void scanConsole() {

        StringBuilder fullTerminalInput = new StringBuilder();
        Scanner sc = new Scanner(System.in);

        String currentTerminalInput = sc.nextLine();
        while (!currentTerminalInput.isEmpty()) {

            fullTerminalInput.append(currentTerminalInput).append("\n");
            currentTerminalInput = sc.nextLine();
        }

        sc.close();
        this.input = fullTerminalInput.toString();
    }

    public String getInput() {
        return this.input;
    }
}
