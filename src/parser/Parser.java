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
                    assignNode.variable = consumeVariableAndEqualSign(currentToken);
                    assignNode.value = parseExpression();
                    return assignNode;
                } else return parseExpression(); //for the first example (assignments) this will never happen
            }
        }
        return null;
    }

    public InterpreterNode parseExpression() {

        InterpreterNode leftNode = parseTerm();

        while (getCurrentTokenType() == TokenType.PLUS) {
            Operator operator = consumeBinOperator(getCurrentToken());
            InterpreterNode rightNode = parseTerm();
            leftNode = new BinaryOpNode(leftNode,rightNode,operator);
        }
        return leftNode;
    }

    public InterpreterNode parseTerm () {

        InterpreterNode leftNode = parseFactor();

        while (getCurrentTokenType() == TokenType.MULT) {
            Operator operator = consumeBinOperator(getCurrentToken());
            InterpreterNode rightNode = parseFactor();
            leftNode = new BinaryOpNode(leftNode, rightNode, operator);
        }
        return leftNode;

    }

    public InterpreterNode parseFactor () {

        Token currentToken = getCurrentToken();
        switch (currentToken.tokenType) {
            case TokenType.VAR -> {return consumeVariable(currentToken);}
            case TokenType.VAL -> {return consumeValue(currentToken);}
            case TokenType.LPAR ->{return consumeParenthesis(currentToken);}
            default -> {throw  new IllegalStateException("Unexpected value or variable token");
            }
        }
    }

    private Token getCurrentToken() {
        return (this.index < tokenList.size())
                ? tokenList.get(index)
                : new Token(TokenType.NIL,"nil");
    }

    private TokenType getCurrentTokenType() {
        return (this.index < tokenList.size())
                ? tokenList.get(index).tokenType
                : TokenType.NIL;
    }

    private TokenType getNextTokenType() {
        return (this.index + 1 < tokenList.size())
                ? tokenList.get(index + 1).tokenType
                : null;
    }

    private VariableNode consumeVariableAndEqualSign(Token currentToken) {
        this.index += 2;
        return new VariableNode(currentToken);
    }

    private Operator consumeBinOperator(Token token) {
        this.index += 1;
        switch (token.tokenType) {
            case TokenType.PLUS -> {return Operator.PLUS;}
            case  TokenType.MULT -> {return Operator.MULT;}
            default -> throw new IllegalArgumentException("Invalid binary operator: " + token.tokenType);
        }
    }

    private VariableNode consumeVariable(Token token) {
        this.index += 1;
        return new VariableNode(token);
    }

    private ValueNode consumeValue(Token token) {
        this.index += 1;
        return new ValueNode(token.getNumericValue());
    }

    private InterpreterNode consumeParenthesis(Token token) {
        this.index += 1; //LPAR
        InterpreterNode evaluatedExpression = parseExpression();
        index += 1; //RPAR
        return evaluatedExpression;

    }

}
