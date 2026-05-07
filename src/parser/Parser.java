package parser;

import com.interpreterNodes.*;
import com.tokenizer.Token;
import com.tokenizer.TokenType;

import java.util.ArrayList;
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

        System.out.println("CURRENT TOKEN: " + currentToken.tokenType + ": " + currentToken.tokenValue);
        switch (currentToken.tokenType) {
            case VAR -> {
                if (getNextTokenType() == TokenType.ASSIGN) {
                    AssignNode assignNode = new AssignNode();
                    assignNode.variable = consumeVariableAndEqualSign(currentToken);
                    assignNode.variableValue = parseExpression();
                    return assignNode;
                } else throw new IllegalStateException("Invalid Assignment");
            }

            case IF -> {
                IfNode ifNode = new IfNode();
                ifNode.condition = consumeIf();
                ifNode.thenExpression = consumeThen();
                ifNode.elseExpression = consumeElse();
                return ifNode;
            }

            case WHILE -> {
                WhileNode whileNode = new WhileNode();
                whileNode.condition = consumeWhile();
                whileNode.body = consumeWhileBody();
                return whileNode;
            }
        }
        return null;
    }

    public InterpreterNode parseComparison () {
        InterpreterNode leftNode = parseExpression();
        Operator operator = consumeBinOperator(getCurrentToken());

        if (operator == Operator.GT || operator == Operator.LT || operator == Operator.EQ) {
            InterpreterNode rightNode = parseExpression();
            return new BinaryOpNode(leftNode,rightNode,operator);
        } else {
            throw new IllegalStateException("Invalid comparison operator");
        }
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
            case TokenType.LPAR ->{return consumeParenthesis();}
            default -> {throw  new IllegalStateException("Unexpected value or variable token");
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

    private TokenType getNextTokenType() {
        return (this.index + 1 < tokenList.size())
                ? tokenList.get(index + 1).tokenType
                : null;
    }

    private InterpreterNode consumeCondition() {
        this.index += 1;
        return parseComparison();
    }
    private InterpreterNode consumeIf () {
        return consumeCondition();
    }

    private InterpreterNode consumeWhile () {
        return consumeCondition();
    }

    private InterpreterNode consumeDo() {
        return consumeBody();
    }

    private List<InterpreterNode> consumeCommas() {

        List<InterpreterNode> commaStatements = new ArrayList<>();
        while (getCurrentTokenType() == TokenType.COMMA) {
            index +=1;
            commaStatements.add(parseStatement());
        }
        return commaStatements;
    }

    private InterpreterNode consumeBody() {
        this.index += 1;
        return parseStatement();

    }

    private InterpreterNode consumeThen() {
        return consumeBody();
    }

    private InterpreterNode consumeElse() {
        return consumeBody();
    }

    private List<InterpreterNode> consumeWhileBody () {
        List <InterpreterNode> whileBodyStatements = new ArrayList<>();

        InterpreterNode doStatement = consumeDo();
        whileBodyStatements.add(doStatement);
        List <InterpreterNode> consumeCommas = consumeCommas();
        whileBodyStatements.addAll(consumeCommas);

        return whileBodyStatements;
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
            case  TokenType.GT -> {return Operator.GT;}
            case  TokenType.LT -> {return Operator.LT;}
            case  TokenType.EQ -> {return Operator.EQ;}
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

    private InterpreterNode consumeParenthesis() {
        this.index += 1; //LPAR
        InterpreterNode evaluatedExpression = parseExpression();
        index += 1; //RPAR
        return evaluatedExpression;

    }

}
