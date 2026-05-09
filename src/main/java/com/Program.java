package com;

import com.interpreter.TreeInterpreter;
import com.tokenizer.Tokenizer;
import com.parser.Parser;

import java.util.Map;

public class Program {

    TreeInterpreter treeInterpreter = new TreeInterpreter();

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

    public Map<String, Object> getVariables() {
        return this.treeInterpreter.memory.globalVariablesToValues;
    }
}
