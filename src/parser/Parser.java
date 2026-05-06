package parser;

import com.interpreterNodes.*;
import com.tokenizer.Token;
import com.tokenizer.TokenType;

import java.util.List;

public class Parser {

    List<Token> tokenList;
    int index = 0;
    public InterpreterNode root;

    public Parser(List<Token> tokenList) {
        this.tokenList = tokenList;
    }

    public void parse() {
        this.root = parseStatement();
    }

    public InterpreterNode parseStatement() {

        Token currentToken = this.getCurrentToken();

        switch (currentToken.tokenType) {
            case VAR -> {
                if (getNextTokenType() == TokenType.EQ) {
                    AssignNode assignNode = new AssignNode();
                    assignNode.variable = new VariableNode(currentToken);
                    index += 2;
                    assignNode.value = parseExpression();
                    return assignNode;
                } else return parseExpression(); //for the first example (assignments) this will never happen
            }
        }
        return null;
    }

    public InterpreterNode parseExpression() {
        Token currentToken = getCurrentToken();
        TokenType nextTokenType = getNextTokenType();

        switch (getCurrentTokenType()) {

            case VAR, VAL -> {

                //check if it'an operation or just a value/variable
                if (nextTokenType == TokenType.PLUS ) {

                    BinaryOpNode binaryOpNode = new BinaryOpNode();
                    binaryOpNode.operator = Operator.PLUS;

                    binaryOpNode.left = parseTerm();
                    index += 1;
                    binaryOpNode.right = parseTerm();
                    return binaryOpNode;

                } else if (nextTokenType == TokenType.MULT) {
                    InterpreterNode leftNode = parseTerm();
                    index += 1;
                    InterpreterNode rightNode = parseTerm();
                    return new BinaryOpNode(leftNode,rightNode,Operator.MULT);
                }
                else if (getCurrentTokenType() == TokenType.VAR) {
                    return new VariableNode(currentToken);
                } else {
                    return new ValueNode(Integer.parseInt(currentToken.tokenValue));
                }
            }
        }

        return null;
    }

    public InterpreterNode parseTerm () {
        Token currentToken = getCurrentToken();
        TokenType nextTokenType = getNextTokenType();

        if (getCurrentTokenType() == null) {
            return null;
        }

        switch (getCurrentTokenType()) {
            case LPAR -> {
                return parseExpression();
            }
            case VAR, VAL -> {
                if (nextTokenType == TokenType.MULT) {
                    BinaryOpNode binaryOpNode = new BinaryOpNode();
                    binaryOpNode.left = parseFactor();
                    binaryOpNode.operator = Operator.MULT;
                    index +=1;
                    binaryOpNode.right = parseFactor();
                    return binaryOpNode;

                } else {
                    return parseFactor();
                }

            }
        }
        return null;
    }

    public InterpreterNode parseFactor () {

        Token currentToken = tokenList.get(index);

        switch (currentToken.tokenType) {
            case VAR -> {

                return new VariableNode(currentToken);
            }
            case VAL -> {
                index +=1;
                return new ValueNode(Integer.parseInt(currentToken.tokenValue));
            }
            case LPAR -> {
                index += 1;
                InterpreterNode evaluatedExpression = parseExpression();
                index += 1; //for RPAR
                return evaluatedExpression;

                }
            default -> {
                System.out.println(getCurrentTokenType());
                throw  new IllegalStateException("Unexpected value or variable token");
            }
            }
    }

    private Token getCurrentToken() {
        return (this.index < tokenList.size())
                ? tokenList.get(index)
                : null;
    }

    private TokenType getCurrentTokenType() {
        return (this.index < tokenList.size())
                ? tokenList.get(index).tokenType
                : null;
    }

    private Token getNextToken() {
        return (this.index + 1 < tokenList.size())
                        ? tokenList.get(index + 1)
                        : null;
    }

    private TokenType getNextTokenType() {
        return (this.index + 1 < tokenList.size())
                ? tokenList.get(index + 1).tokenType
                : null;
    }
}
