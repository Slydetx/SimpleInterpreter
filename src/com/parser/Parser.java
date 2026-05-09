package com.parser;

import com.interpreterNodes.*;
import com.tokenizer.Token;
import com.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    TokenConsumer consumer;
    ExpressionParser expressionParser;
    public InterpreterNode root;

    public Parser(List<Token> tokenList) {
        this.consumer = new TokenConsumer(tokenList);
        this.expressionParser = new ExpressionParser(this.consumer);

    }

    public void parse() {
        this.root = parseStatement();
    }

    public InterpreterNode parseStatement() {

        Token currentToken = consumer.getCurrentToken();

        switch (currentToken.tokenType) {
            case VAR -> {
                AssignNode assignNode = new AssignNode();
                assignNode.variable = parseVariableAndAssignSign(currentToken);
                assignNode.variableValue = expressionParser.parseComparison();
                return assignNode;
            }

            case IF -> {
                IfNode ifNode = new IfNode();
                ifNode.condition = parseIf();
                ifNode.thenExpression = parseThen();
                ifNode.elseExpression = parseElse();
                return ifNode;
            }

            case WHILE -> {
                WhileNode whileNode = new WhileNode();
                whileNode.condition = parseWhile();
                whileNode.body = parseWhileBody();
                return whileNode;
            }

            case FUN -> {
                FunctionNode functionNode = new FunctionNode();
                functionNode.name = parseFunctionDefinition();
                functionNode.parameters = parseParameters();
                functionNode.body = parseFunctionBody();
                return functionNode;
            }

            case RETURN -> {
                consumer.matchAndConsume(TokenType.RETURN);
                return expressionParser.parseExpression();

            }
            default -> throw new ParseException("Invalid statement");

        }
    }

    private InterpreterNode parseCondition(TokenType tokenType) {
        consumer.matchAndConsume(tokenType);
        return expressionParser.parseComparison();
    }
    private InterpreterNode parseIf() {
        return parseCondition(TokenType.IF);
    }

    private InterpreterNode parseWhile() {
        return parseCondition(TokenType.WHILE);
    }

    private InterpreterNode consumeBody(TokenType tokenType) {
        consumer.matchAndConsume(tokenType);
        return parseStatement();

    }

    private InterpreterNode parseDo() {
        return consumeBody(TokenType.DO);
    }

    private InterpreterNode parseThen() {
        return consumeBody(TokenType.THEN);
    }

    private InterpreterNode parseElse() {
        return consumeBody(TokenType.ELSE);
    }

    private List<InterpreterNode> parseCommaStatements() {

        List<InterpreterNode> commaStatements = new ArrayList<>();
        while (consumer.getCurrentTokenType() == TokenType.COMMA) {
            consumer.matchAndConsume(TokenType.COMMA);
            commaStatements.add(parseStatement());
        }
        return commaStatements;
    }

    private List<InterpreterNode> parseWhileBody() {

        List <InterpreterNode> whileBodyStatements = new ArrayList<>();

        InterpreterNode doStatement = parseDo();
        whileBodyStatements.add(doStatement);
        List <InterpreterNode> parsedCommaStatements = parseCommaStatements();
        whileBodyStatements.addAll(parsedCommaStatements);

        return whileBodyStatements;
    }


    private VariableNode parseFunctionDefinition() {

        consumer.matchAndConsume(TokenType.FUN);
        return expressionParser.parseVariable(consumer.getCurrentToken());
    }

    private List<VariableNode> parseParameters() {

        consumer.matchAndConsume(TokenType.LPAR);

        List<VariableNode> parameters = new ArrayList<>();
        while (consumer.getCurrentTokenType() == TokenType.VAR) {

            parameters.add(expressionParser.parseVariable(consumer.getCurrentToken()));

            if (consumer.getCurrentTokenType() == TokenType.COMMA) {
                consumer.matchAndConsume(TokenType.COMMA);
            }
        }

        consumer.matchAndConsume(TokenType.RPAR);
        return parameters;
    }


    private List<InterpreterNode> parseFunctionBody() {

        List<InterpreterNode> bodyStatements = new ArrayList<>();

        consumer.matchAndConsume(TokenType.LBRACE);
        bodyStatements.add(parseStatement());
        bodyStatements.addAll(parseCommaStatements());
        consumer.matchAndConsume(TokenType.RBRACE);
        return bodyStatements;
    }

    private VariableNode parseVariableAndAssignSign(Token currentToken) {
        VariableNode variableNode = expressionParser.parseVariable(currentToken);
        consumer.matchAndConsume(TokenType.ASSIGN);
        return variableNode;
    }
}
