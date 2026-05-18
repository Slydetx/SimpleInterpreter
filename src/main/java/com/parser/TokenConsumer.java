package com.parser;

import com.interpreterNodes.Operator;
import com.tokenizer.Token;
import com.tokenizer.TokenType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides controlled access to the token list for the parser. <br>
 * Maintains a current index that advances as tokens are consumed.
 */
class TokenConsumer {

    private static final Map<TokenType, Operator> TYPE_OPERATOR_MAP = Map.of(
            TokenType.PLUS, Operator.PLUS,
            TokenType.MINUS, Operator.MINUS,
            TokenType.MULT, Operator.MULT,
            TokenType.DIV, Operator.DIV,
            TokenType.GT, Operator.GT,
            TokenType.LT, Operator.LT,
            TokenType.EQ, Operator.EQ,
            TokenType.LT_EQ, Operator.LT_EQ,
            TokenType.GT_EQ, Operator.GT_EQ,
            TokenType.NOT_EQ, Operator.NOT_EQ
    );

    List<Token> tokenList;
    int index = 0;

    TokenConsumer (List<Token> tokenList) {
        this.tokenList = tokenList;
    }

    /** Returns the current token without advancing, or null if past the end of the token list. */
    Token peekCurrentToken() {
        return (this.index < tokenList.size())
                ? tokenList.get(index)
                : null;
    }

    /** Returns the current token type without advancing, or null if past the end of the token list. */
    TokenType peekCurrentTokenType() {
        return (this.index < tokenList.size())
                ? tokenList.get(index).tokenType
                : null;
    }

    /** Returns the next token type without advancing, or null if past the end. */
    TokenType peekNextTokenType() {
        return (this.index + 1 < tokenList.size())
                ? tokenList.get(index + 1).tokenType
                : null;
    }

    /** Returns true if the current token is a variable followed by '(' indicating a function call. */
    boolean checkIsFunctionCall() {
        return peekCurrentTokenType() == TokenType.VAR && peekNextTokenType() == TokenType.LPAR;

    }

    private boolean matchesCurrent(TokenType tokenType) {
        return peekCurrentTokenType() == tokenType;
    }

    private void consume () {
        index += 1;
    }

    /**
     * Asserts the current token matches the expected type, then advances.
     * @throws ParseException if the current token does not match the expected type.
     */
    void matchAndConsume(TokenType tokenType) {

        if (matchesCurrent(tokenType)) consume();
        else {
            throw new ParseException("Expected " + tokenType + " but got " + peekCurrentTokenType() + " at position " + index);
        }

    }

    /**
     * Asserts the current token is a valid binary operator, advances, and returns the corresponding Operator enum value.
     * @throws ParseException if the current token is not in the expected operator set.
     */
    Operator consumeBinOperator(Set<TokenType> expectedOperators) {

        Operator operator = TYPE_OPERATOR_MAP.get(peekCurrentTokenType());

        if (operator == null || !expectedOperators.contains(peekCurrentTokenType())) {
            throw new ParseException("Invalid operator: " + peekCurrentTokenType());
        }

        consume();

        return operator;
    }
    /** Advances past the function name token and returns its string value. */
        String consumeAndGetFunctionName (Token currentToken) {
        matchAndConsume(TokenType.VAR);
        return currentToken.tokenValue;
    }
}
