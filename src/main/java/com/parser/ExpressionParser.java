package com.parser;

import com.interpreterNodes.*;
import com.tokenizer.Token;
import com.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ExpressionParser {

    private static final Set<TokenType> COMPARISON_TOKENS = Set.of(
            TokenType.GT,
            TokenType.LT,
            TokenType.EQ,
            TokenType.LT_EQ,
            TokenType.GT_EQ,
            TokenType.NOT_EQ
    );

    TokenConsumer consumer;

    public ExpressionParser(TokenConsumer consumer) {
        this.consumer = consumer;
    }

    public InterpreterNode parseComparison () {

        InterpreterNode leftNode = parseExpression();

        // getCurrentTokenType() returns null when the index is past the end of the token list
        if (consumer.getCurrentTokenType() == null || !COMPARISON_TOKENS.contains(consumer.getCurrentTokenType())) {
            return leftNode;
        }

        Operator operator = consumer.consumeBinOperator(COMPARISON_TOKENS);
        InterpreterNode rightNode = parseExpression();
        return new BinaryOpNode(leftNode,rightNode,operator);

    }

    public InterpreterNode parseExpression() {

        InterpreterNode leftNode = parseTerm();

        while (consumer.getCurrentTokenType() == TokenType.PLUS || consumer.getCurrentTokenType() == TokenType.MINUS) {
            Operator operator = consumer.consumeBinOperator(Set.of(TokenType.PLUS,TokenType.MINUS));
            InterpreterNode rightNode = parseTerm();
            leftNode = new BinaryOpNode(leftNode,rightNode,operator);
        }
        return leftNode;
    }

    public InterpreterNode parseTerm () {

        InterpreterNode leftNode = parseFactor();

        while (consumer.getCurrentTokenType() == TokenType.MULT || consumer.getCurrentTokenType() == TokenType.DIV) {
            Operator operator = consumer.consumeBinOperator(Set.of(TokenType.MULT, TokenType.DIV));
            InterpreterNode rightNode = parseFactor();
            leftNode = new BinaryOpNode(leftNode, rightNode, operator);
        }
        return leftNode;

    }

    public InterpreterNode parseFactor () {


        if (consumer.checkIsFunctionCall()) {

            FunctionCallNode functionCallNode = new FunctionCallNode();
            functionCallNode.name = consumer.consumeAndGetFunctionName(consumer.getCurrentToken());

            List<InterpreterNode> arguments;
            arguments = parseArguments();
            functionCallNode.arguments = arguments;

            return functionCallNode;
        }

        Token currentToken = consumer.getCurrentToken();

        switch (currentToken.tokenType) {
            case TokenType.VAR -> { return parseVariable(currentToken); }
            case TokenType.VAL -> { return parseValue(currentToken); }
            case TokenType.LPAR ->{ return parseParenthesis(); }

            case TokenType.TRUE -> { consumer.matchAndConsume(TokenType.TRUE); return new ValueNode(1); }
            case TokenType.FALSE -> { consumer.matchAndConsume(TokenType.FALSE); return new ValueNode(0); }

            default -> throw  new ParseException ("Unexpected value or variable token: " + consumer.getCurrentTokenType());
        }
    }

    private InterpreterNode parseParenthesis() {
        consumer.matchAndConsume(TokenType.LPAR);
        InterpreterNode evaluatedExpression = parseExpression();
        consumer.matchAndConsume(TokenType.RPAR);
        return evaluatedExpression;

    }

    private List<InterpreterNode> parseArguments() {

        consumer.matchAndConsume(TokenType.LPAR);
        List<InterpreterNode> arguments = new ArrayList<>();

        while (checkIsValidArgument()){

            arguments.add(parseExpression());

            if (consumer.getCurrentTokenType() == TokenType.COMMA) {
                consumer.matchAndConsume(TokenType.COMMA);
            }
        }

        consumer.matchAndConsume(TokenType.RPAR);
        return arguments;
    }

    VariableNode parseVariable(Token token) {
        consumer.matchAndConsume(TokenType.VAR);
        return new VariableNode(token);
    }

    private ValueNode parseValue(Token token) {
        consumer.matchAndConsume(TokenType.VAL);
        return new ValueNode(token.getNumericValue());
    }
    private boolean checkIsValidArgument() {
        return consumer.getCurrentTokenType() == TokenType.VAL
                || consumer.getCurrentTokenType() == TokenType.VAR
                ||  consumer.getCurrentTokenType() == TokenType.TRUE
                || consumer.getCurrentTokenType() == TokenType.FALSE;
    }

}
