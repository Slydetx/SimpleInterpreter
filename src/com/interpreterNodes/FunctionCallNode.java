package com.interpreterNodes;

import java.util.List;

public class FunctionCallNode implements InterpreterNode {
    public String name;
    public List<InterpreterNode> arguments;

}
