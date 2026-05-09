package com.tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.lang.Character;

/**
 * Converts a source code string into a flat list of typed tokens. <br>
 * <br>
 * Scans character by character, delegating to: <br>
 * - TokenBuilder   — to read multi-character tokens (identifiers, numbers) <br>
 * - TokenClassifier — to classify raw strings into typed Token objects <br>
 * <br>
 */
public class Tokenizer {

    private final InputConsumer inputConsumer;
    private final TokenBuilder tokenBuilder;
    private final TokenClassifier tokenClassifier;

    public List<Token> tokenList = new ArrayList<>();

    public Tokenizer (String input) {
        this.inputConsumer = new InputConsumer(input);
        this.tokenBuilder = new TokenBuilder(this.inputConsumer);
        this.tokenClassifier = new TokenClassifier(this.tokenList);
    }

    /** Tokenizes the full input string and fills tokenList with the resulting tokens. */
    public void tokenize() {

        while (inputConsumer.currentPosition() <= inputConsumer.lastPosition()) {

            if (inputConsumer.currentIsKeywordOrVariable()) {

                StringBuilder word = tokenBuilder.buildKeyWordOrVariable();
                tokenClassifier.mapToken(word.toString());

            } else if (inputConsumer.currentIsDigit()) {

                StringBuilder number = tokenBuilder.buildNumber();
                tokenClassifier.mapToken(number.toString());

            } else if (inputConsumer.currentIsWhiteSpace()) {

                inputConsumer.consumeCurrentChar();

            } else {

                if (inputConsumer.nextTokenIsComparisonOperator()) {

                    tokenClassifier.mapToken(inputConsumer.getTwoChars());

                } else {

                    tokenClassifier.mapToken(Character.toString(inputConsumer.consumeCurrentChar()));
                }
            }

        }
    }
}