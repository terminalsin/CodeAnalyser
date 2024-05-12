package com.codeanalyser.shop.gpt.serializer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeResultDTO {
    private String code;
    private String highlight;
    private String type; // This will be converted to the enum SuggestionType
}