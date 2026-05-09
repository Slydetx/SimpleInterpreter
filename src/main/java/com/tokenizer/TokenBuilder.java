package com.tokenizer;

/**
 * Builds multi-character token values by consuming characters from InputConsumer.
 * Used by the Tokenizer for identifiers, keywords, and numeric literals.
 */
class TokenBuilder {
    InputConsumer inputConsumer;

    TokenBuilder(InputConsumer inputConsumer) {
        this.inputConsumer = inputConsumer;
    }

    /**
     * Reads a keyword or variable name (letters, digits, and underscores allowed) <br>
     * Stops at the first character that doesn't belong to an identifier.
     */
     StringBuilder buildKeyWordOrVariable() {
        StringBuilder word = new StringBuilder();

        while (Character.isLetterOrDigit(inputConsumer.peekCurrentChar()) || inputConsumer.peekCurrentChar() == '_') {
            word.append(inputConsumer.consumeCurrentChar());
        }

        return word;
    }

    /**
     * Reads a numeric literal (digits only).
     * Stops at the first non-digit character.
     */
     StringBuilder buildNumber() {
        StringBuilder number = new StringBuilder();

        while (Character.isLetterOrDigit(inputConsumer.peekCurrentChar())) {
            number.append(inputConsumer.consumeCurrentChar());
        }
        return number;
    }

}
