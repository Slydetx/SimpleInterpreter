package com.tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.lang.Character;

public class Tokenizer {

    //TODO: Implement Division (/), !=, >=

    private final InputConsumer inputConsumer;
    private final TokenBuilder tokenBuilder;
    private final TokenClassifier tokenClassifier;

    public List<Token> tokenList = new ArrayList<>();

    public Tokenizer (String input) {
        this.inputConsumer = new InputConsumer(input);
        this.tokenBuilder = new TokenBuilder(this.inputConsumer);
        this.tokenClassifier = new TokenClassifier(this.tokenList);
    }

    public void tokenize() {

        while (inputConsumer.currentPosition() <= inputConsumer.lastPosition()) {

            if (inputConsumer.currentIsKeywordOrVariable()) {

                StringBuilder word = tokenBuilder.buildKeyWordOrVariable();
                tokenClassifier.mapToken(word.toString());

            } else if (inputConsumer.currentIsNumber()) {

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