package com.tokenizer;

public class InputConsumer {
    private int index = 0;
    private final char[] chars;

    InputConsumer(String input) {
        chars = input.toCharArray();
    }

    int currentPosition() {
        return index;
    }

    int lastPosition() {
        return chars.length - 1;
    }

    char consumeCurrentChar() {
        char c = chars[index];
        advance();
        return c;
    }

    char peekCurrentChar() {
        return index < chars.length ? chars[index] : ' ';
    }

    char peekNextChar() {
        return chars[index + 1];
    }

    String getTwoChars() {
        String twoChars = Character.toString(chars[index]) + chars[index + 1];
        advance();
        advance();
        return twoChars;
    }

    private void advance() {
        index++;
    }

    public boolean currentIsKeywordOrVariable(){
        return Character.isLetter(peekCurrentChar()) || peekCurrentChar() == '_'; //allows names to start with '_'
    }

    public boolean currentIsNumber() {
        return Character.isDigit(peekCurrentChar());
    }

    public boolean currentIsWhiteSpace() {
        return peekCurrentChar() == ' ';
    }

    public boolean nextTokenIsComparisonOperator() {
        return  (peekCurrentChar() == '=' || peekCurrentChar() == '<') && peekNextChar() == '=';
    }

}
