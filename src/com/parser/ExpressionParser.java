package com.parser;

import com.interpreterNodes.*;
import com.tokenizer.Token;
import com.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ExpressionParser {

    private static final Set<Operator> COMPARISON_TOKENS = Set.of(
            Operator.GT,
            Operator.LT,
            Operator.EQ
    );

    TokenConsumer consumer;

    public ExpressionParser(TokenConsumer consumer) {
        this.consumer = consumer;
    }

    public InterpreterNode parseComparison () {

        InterpreterNode leftNode = parseExpression();
        Operator operator = consumer.consumeBinOperator(COMPARISON_TOKENS);
        InterpreterNode rightNode = parseExpression();
        return new BinaryOpNode(leftNode,rightNode,operator);

    }

    public InterpreterNode parseExpression() {

        InterpreterNode leftNode = parseTerm();

        while (consumer.getCurrentTokenType() == TokenType.PLUS) {
            Operator operator = consumer.consumeBinOperator(Set.of(Operator.PLUS)); //useless check, but needed for comparison operators... maybe split functions? But useful if introducing Operator.MINUS later
            InterpreterNode rightNode = parseTerm();
            leftNode = new BinaryOpNode(leftNode,rightNode,operator);
        }
        return leftNode;
    }

    public InterpreterNode parseTerm () {

        InterpreterNode leftNode = parseFactor();

        while (consumer.getCurrentTokenType() == TokenType.MULT) {
            Operator operator = consumer.consumeBinOperator(Set.of(Operator.MULT)); //useless check, but needed for comparison operators... maybe split functions? But useful if introducing Operator.DIVIDE later
            InterpreterNode rightNode = parseFactor();
            leftNode = new BinaryOpNode(leftNode, rightNode, operator);
        }
        return leftNode;

    }

    public InterpreterNode parseFactor () {


        if (consumer.checkIsFunctionCall()) {

            FunctionCallNode functionCallNode = new FunctionCallNode();
            functionCallNode.name = consumer.consumeAndGetFunctionName(consumer.getCurrentToken());

            List<ValueNode> arguments;
            arguments = parseArguments();
            functionCallNode.arguments = arguments;

            return functionCallNode;
        }

        Token currentToken = consumer.getCurrentToken();

        switch (currentToken.tokenType) {
            case TokenType.VAR -> { return parseVariable(currentToken); }
            case TokenType.VAL -> { return parseValue(currentToken); }
            case TokenType.LPAR ->{ return parseParenthesis(); }
            default -> throw  new ParseException ("Unexpected value or variable token: " + consumer.getCurrentTokenType());
        }
    }

    private InterpreterNode parseParenthesis() {
        consumer.matchAndConsume(TokenType.LPAR);
        InterpreterNode evaluatedExpression = parseExpression();
        consumer.matchAndConsume(TokenType.RPAR);
        return evaluatedExpression;

    }

    private List<ValueNode> parseArguments() {

        consumer.matchAndConsume(TokenType.LPAR);
        List<ValueNode> arguments = new ArrayList<>();

        while (consumer.getCurrentTokenType() == TokenType.VAL) {

            arguments.add(parseValue(consumer.getCurrentToken()));

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

}
