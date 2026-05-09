package com.parser;

import com.interpreterNodes.Operator;
import com.tokenizer.Token;
import com.tokenizer.TokenType;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TokenConsumer {

    private static final Map<TokenType, Operator> TYPE_OPERATOR_MAP = Map.of(
            TokenType.PLUS, Operator.PLUS,
            TokenType.MINUS, Operator.MINUS,
            TokenType.MULT, Operator.MULT,
            TokenType.GT, Operator.GT,
            TokenType.LT, Operator.LT,
            TokenType.EQ, Operator.EQ,
            TokenType.LT_EQ, Operator.LT_EQ,
            TokenType.GT_EQ, Operator.GT_EQ,
            TokenType.NOT_EQ, Operator.NOT_EQ
    );

    List<Token> tokenList;
    int index = 0;

    public TokenConsumer (List<Token> tokenList) {
        this.tokenList = tokenList;
    }

    Token getCurrentToken() {
        return (this.index < tokenList.size())
                ? tokenList.get(index)
                : null;
    }

    TokenType getCurrentTokenType() {
        return (this.index < tokenList.size())
                ? tokenList.get(index).tokenType
                : null;
    }

    TokenType getNextTokenType() {
        return (this.index + 1 < tokenList.size())
                ? tokenList.get(index + 1).tokenType
                : null;
    }

    boolean checkIsFunctionCall() {
        return getCurrentTokenType() == TokenType.VAR && getNextTokenType() == TokenType.LPAR;

    }


    private boolean matchesCurrent(TokenType tokenType) {
        return getCurrentTokenType() == tokenType;
    }

    private void consume () {
        index += 1;
    }

    void matchAndConsume (TokenType tokenType) {

        if (matchesCurrent(tokenType)) consume();
        else {
            throw new ParseException("Expected " + getCurrentTokenType() + " but got " + tokenType + " at position " + index);
        }

    }

    Operator consumeBinOperator(Set<TokenType> expectedOperators) {

        Operator operator = TYPE_OPERATOR_MAP.get(getCurrentTokenType());

        if (operator == null || !expectedOperators.contains(getCurrentTokenType())) {
            throw new ParseException("Invalid operator: " + getCurrentTokenType());
        }

        consume();

        return operator;
    }

        String consumeAndGetFunctionName (Token currentToken) {
        this.index += 1; // name
        return currentToken.tokenValue;
    }
}
