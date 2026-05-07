package com.tokenizer;

public class RegexBuilder {
    String regex;

    public RegexBuilder splitAt(String start) {
        regex = start;
        return this;
    }

    public RegexBuilder or (String regex) {
        this.regex += "|" + regex;
        return this;
    }

    @Override
    public String toString() {
        return this.regex;
    }
}
