package com;

import com.interpreter.TreeInterpreter;
import com.tokenizer.Tokenizer;
import com.parser.Parser;

import java.util.Map;

public class Program {

    /**
     * Orchestrates the interpreter pipeline for a given program input.<br>
     * <br>
     * Splits the input into lines, runs each line through the Tokenizer and Parser,
     * and evaluates the resulting AST. Global variable values are printed after
     * all statements have been executed.
     */
    TreeInterpreter treeInterpreter = new TreeInterpreter();

    /**
     * Executes the given program source string.
     * Returns this instance to allow method chaining (used in tests via getVariables()).
     */
    public Program execute(String input) {
        String[] statements = input.split("\n");

        for (String statement : statements) {
            Tokenizer tokenizer = new Tokenizer(statement);

            tokenizer.tokenize();

            Parser parser = new Parser(tokenizer.tokenList);
            parser.parse();

            treeInterpreter.evaluate(parser.root);
        }
        treeInterpreter.memory.printMemory();

        return this;
    }

    /** Returns the global variable map. (Used in tests to assert final variable values) */
    public Map<String, Object> getVariables() {
        return this.treeInterpreter.memory.globalVariablesToValues;
    }
}
