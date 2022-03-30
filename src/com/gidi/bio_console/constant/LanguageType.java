package com.gidi.bio_console.constant;

public enum LanguageType {
    CHINESE("zh"),
    ENGLISH("en");

    private String language;

    LanguageType(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language == null ? "" : language;
    }
}
