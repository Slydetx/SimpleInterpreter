package com.parser;

import com.interpreterNodes.*;
import com.tokenizer.Token;
import com.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds an Abstract Syntax Tree from a flat list of tokens using recursive descent parsing. <br>
 * <br>
 * Entry point is parse(), which parses a single statement and stores the result in 'root'. <br>
 * Called once per line by Program — the interpreter then evaluates each root node in order.
 * <br>
 * <br>
 * Expression parsing is delegated to ExpressionParser.
 */
 public class Parser {

    TokenConsumer consumer;
    ExpressionParser expressionParser;
    public InterpreterNode root;

    public Parser(List<Token> tokenList) {
        this.consumer = new TokenConsumer(tokenList);
        this.expressionParser = new ExpressionParser(this.consumer);

    }

    /** Parses a single statement and stores the resulting AST node in 'root'. */
    public void parse() {
        this.root = parseStatement();
    }

    /**
     * Determines the statement type from the current token and delegates to the
     * appropriate parse method. Throws ParseException on unrecognized input.
     */
    private InterpreterNode parseStatement() {

        Token currentToken = consumer.peekCurrentToken();
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

    /** Consumes the given keyword token and parses the following comparison expression. */
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

    /** Consumes the given keyword token and parses the following statement as a body. */
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

    /** Parses additional comma-separated statements. */
    private List<InterpreterNode> parseCommaStatements() {

        List<InterpreterNode> commaStatements = new ArrayList<>();
        while (consumer.peekCurrentTokenType() == TokenType.COMMA) {
            consumer.matchAndConsume(TokenType.COMMA);
            commaStatements.add(parseStatement());
        }
        return commaStatements;
    }

    /**
     * Parses the while body: the first statement after 'do' followed by
     * any comma-separated statements.
     */
    private List<InterpreterNode> parseWhileBody() {

        List <InterpreterNode> whileBodyStatements = new ArrayList<>();

        InterpreterNode doStatement = parseDo();
        whileBodyStatements.add(doStatement);
        List <InterpreterNode> parsedCommaStatements = parseCommaStatements();
        whileBodyStatements.addAll(parsedCommaStatements);

        return whileBodyStatements;
    }

    /** Consumes 'fun' and parses the function name as a VariableNode. */
    private VariableNode parseFunctionDefinition() {

        consumer.matchAndConsume(TokenType.FUN);
        return expressionParser.parseVariable(consumer.peekCurrentToken());
    }

    /** Parses a comma-separated parameter list enclosed in parentheses. */
    private List<VariableNode> parseParameters() {

        consumer.matchAndConsume(TokenType.LPAR);

        List<VariableNode> parameters = new ArrayList<>();
        while (consumer.peekCurrentTokenType() == TokenType.VAR) {

            parameters.add(expressionParser.parseVariable(consumer.peekCurrentToken()));

            if (consumer.peekCurrentTokenType() == TokenType.COMMA) {
                consumer.matchAndConsume(TokenType.COMMA);
            }
        }

        consumer.matchAndConsume(TokenType.RPAR);
        return parameters;
    }

    /** Parses the function body enclosed in braces as a list of comma-separated statements. */
    private List<InterpreterNode> parseFunctionBody() {

        List<InterpreterNode> bodyStatements = new ArrayList<>();

        consumer.matchAndConsume(TokenType.LBRACE);
        bodyStatements.add(parseStatement());
        bodyStatements.addAll(parseCommaStatements());
        consumer.matchAndConsume(TokenType.RBRACE);
        return bodyStatements;
    }

    /** Parses a variable name followed by an assignment sign. */
    private VariableNode parseVariableAndAssignSign(Token currentToken) {
        VariableNode variableNode = expressionParser.parseVariable(currentToken);
        consumer.matchAndConsume(TokenType.ASSIGN);
        return variableNode;
    }
}
