package com;

import com.interpreter.TreeInterpreter;
import com.tokenizer.Tokenizer;
import parser.Parser;

public class Program {

    public void execute(String input) {
        String[] statements = input.split("\n");
        TreeInterpreter treeInterpreter = new TreeInterpreter();

        for (String statement : statements) {
            Tokenizer tokenizer = new Tokenizer();

            tokenizer.tokenize(statement);

            Parser parser = new Parser(tokenizer.tokenList);
            parser.parse();

            treeInterpreter.evaluate(parser.root);
        }
        treeInterpreter.printMemory();
    }
}
