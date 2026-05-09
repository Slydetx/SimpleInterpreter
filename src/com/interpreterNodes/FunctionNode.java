package com.interpreterNodes;

import java.util.List;

public class FunctionNode implements InterpreterNode {
    public VariableNode name;
    public List<VariableNode> parameters;
    public List<InterpreterNode> body;

}
