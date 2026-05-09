package com.parser;

import com.interpreterNodes.*;
import com.tokenizer.Token;
import com.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Parses expressions from the token list into AST nodes.<br>
 * <br>
 * Uses recursive descent with the following precedence hierarchy (lowest to highest):<br>
 *   parseComparison → parseExpression → parseTerm → parseFactor<br>
 * <br>
 * - parseComparison handles ==, !=, <, <=, >, >=<br>
 * - parseExpression handles + and - <br>
 * - parseTerm       handles * and / <br>
 * - parseFactor     handles literals, variables, function calls, parentheses<br>
 */
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

    ExpressionParser(TokenConsumer consumer) {
        this.consumer = consumer;
    }

    /**
     * Parses a comparison expression (e.g., x > 10, n <= 0). <br>
     * If no comparison operator is found, returns the left side directly as a passthrough. <br>
     */
    InterpreterNode parseComparison() {

        InterpreterNode leftNode = parseExpression();

        // getCurrentTokenType() returns null when the index is past the end of the token list
        if (consumer.peekCurrentTokenType() == null || !COMPARISON_TOKENS.contains(consumer.peekCurrentTokenType())) {
            return leftNode;
        }

        Operator operator = consumer.consumeBinOperator(COMPARISON_TOKENS);
        InterpreterNode rightNode = parseExpression();
        return new BinaryOpNode(leftNode,rightNode,operator);

    }

    /** Parses addition and subtraction, looping to handle chains like a + b + c. */
    InterpreterNode parseExpression() {

        InterpreterNode leftNode = parseTerm();

        while (consumer.peekCurrentTokenType() == TokenType.PLUS || consumer.peekCurrentTokenType() == TokenType.MINUS) {
            Operator operator = consumer.consumeBinOperator(Set.of(TokenType.PLUS,TokenType.MINUS));
            InterpreterNode rightNode = parseTerm();
            leftNode = new BinaryOpNode(leftNode,rightNode,operator);
        }
        return leftNode;
    }

    /** Parses multiplication and division, looping to handle chains like a * b * c. */
    private InterpreterNode parseTerm () {

        InterpreterNode leftNode = parseFactor();

        while (consumer.peekCurrentTokenType() == TokenType.MULT || consumer.peekCurrentTokenType() == TokenType.DIV) {
            Operator operator = consumer.consumeBinOperator(Set.of(TokenType.MULT, TokenType.DIV));
            InterpreterNode rightNode = parseFactor();
            leftNode = new BinaryOpNode(leftNode, rightNode, operator);
        }
        return leftNode;

    }

    /**
     * Parses atomic expressions: function calls, variables, integer literals,
     * boolean literals (true/false), and parenthesized expressions.
     */
    private InterpreterNode parseFactor () {


        if (consumer.checkIsFunctionCall()) {

            FunctionCallNode functionCallNode = new FunctionCallNode();
            functionCallNode.name = consumer.consumeAndGetFunctionName(consumer.peekCurrentToken());

            List<InterpreterNode> arguments;
            arguments = parseArguments();
            functionCallNode.arguments = arguments;

            return functionCallNode;
        }

        Token currentToken = consumer.peekCurrentToken();

        switch (currentToken.tokenType) {
            case TokenType.VAR -> { return parseVariable(currentToken); }
            case TokenType.VAL -> { return parseValue(currentToken); }
            case TokenType.LPAR ->{ return parseParenthesis(); }

            case TokenType.TRUE -> { consumer.matchAndConsume(TokenType.TRUE); return new ValueNode(1); }
            case TokenType.FALSE -> { consumer.matchAndConsume(TokenType.FALSE); return new ValueNode(0); }

            default -> throw  new ParseException ("Unexpected value or variable token: " + consumer.peekCurrentTokenType());
        }
    }

    /** Consumes a parenthesized expression and returns the inner node. */
    private InterpreterNode parseParenthesis() {
        consumer.matchAndConsume(TokenType.LPAR);
        InterpreterNode evaluatedExpression = parseExpression();
        consumer.matchAndConsume(TokenType.RPAR);
        return evaluatedExpression;

    }

    /** Parses a comma-separated argument list enclosed in parentheses. */
    private List<InterpreterNode> parseArguments() {

        consumer.matchAndConsume(TokenType.LPAR);
        List<InterpreterNode> arguments = new ArrayList<>();

        while (checkIsValidArgument()){

            arguments.add(parseExpression());

            if (consumer.peekCurrentTokenType() == TokenType.COMMA) {
                consumer.matchAndConsume(TokenType.COMMA);
            }
        }

        consumer.matchAndConsume(TokenType.RPAR);
        return arguments;
    }

    /** Consumes a VAR token and returns a VariableNode with the given token. */
    VariableNode parseVariable(Token token) {
        consumer.matchAndConsume(TokenType.VAR);
        return new VariableNode(token);
    }

    /** Consumes a VAL token and returns a ValueNode with the parsed integer value. */
    private ValueNode parseValue(Token token) {
        consumer.matchAndConsume(TokenType.VAL);
        return new ValueNode(token.getNumericValue());
    }

    /** Returns true if the current token can start an argument expression. */
    private boolean checkIsValidArgument() {
        return consumer.peekCurrentTokenType() == TokenType.VAL
                || consumer.peekCurrentTokenType() == TokenType.VAR
                ||  consumer.peekCurrentTokenType() == TokenType.TRUE
                || consumer.peekCurrentTokenType() == TokenType.FALSE;
    }

}
