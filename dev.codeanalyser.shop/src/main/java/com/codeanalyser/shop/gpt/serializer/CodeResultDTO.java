package com.codeanalyser.shop.gpt.serializer;

public class CodeResultDTO {
    private String code;
    private String highlight;
    private String type; // This will be converted to the enum SuggestionType

    // Getters and setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}