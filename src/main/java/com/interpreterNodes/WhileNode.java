package com.interpreterNodes;

import java.util.List;

public class WhileNode implements InterpreterNode {
    public InterpreterNode condition;
    public List<InterpreterNode> body;
}
