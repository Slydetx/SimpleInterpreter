package com.io;

import java.nio.file.Files;

import java.nio.file.Path;

/**
 * Reads the contents of a text file into a string.
 * Used to load a program from a file path instead of standard input.
 */
public class TextFileReader {

    private final String source;

    private String content;

    public TextFileReader(String source) {

        this.source = source;

    }

    public void readFile() {

        try {

            content = Files.readString(Path.of(source));

        } catch (Exception e) {

            throw new RuntimeException("Failed to read file: " + source, e);

        }

    }

    public String getContent() {

        return content;

    }

}