package com;

import com.debug.Debugger;
import com.interpreter.TreeInterpreter;
import com.tokenizer.Tokenizer;
import com.parser.Parser;

public class Program {

    public void execute(String input) {
        String[] statements = input.split("\n");
        TreeInterpreter treeInterpreter = new TreeInterpreter();

        for (String statement : statements) {
            Tokenizer tokenizer = new Tokenizer(statement);

            tokenizer.tokenize();
            Debugger.debugTokenizer(tokenizer);

            Parser parser = new Parser(tokenizer.tokenList);
            parser.parse();

            Debugger.printTree(parser.root);

            treeInterpreter.evaluate(parser.root);
        }
        treeInterpreter.printMemory();
    }
}
