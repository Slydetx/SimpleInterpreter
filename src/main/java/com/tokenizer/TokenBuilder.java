package com.tokenizer;

public class TokenBuilder {
    InputConsumer inputConsumer;

    TokenBuilder(InputConsumer inputConsumer) {
        this.inputConsumer = inputConsumer;
    }


    public StringBuilder buildKeyWordOrVariable() {
        StringBuilder word = new StringBuilder();

        while (Character.isLetterOrDigit(inputConsumer.peekCurrentChar()) || inputConsumer.peekCurrentChar() == '_') {
            word.append(inputConsumer.consumeCurrentChar());
        }

        return word;
    }

    public StringBuilder buildNumber() {
        StringBuilder number = new StringBuilder();

        while (Character.isLetterOrDigit(inputConsumer.peekCurrentChar())) {
            number.append(inputConsumer.consumeCurrentChar());
        }

        return number;
    }

}
