package com.codeanalyser.shop.code;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
public class CodeModalDto {
    private UUID id;
    private String ownerUsername;
    private String code;
    private List<CodeResult> results;
    private int score;

    @Data
    @EqualsAndHashCode(exclude = "codeModal") // Avoids circular references in generated equals and hashCode methods
    public static class CodeResult {
        private Long id; // Using Long for IDs of results

        private String code;
        private String highlight;
        private SuggestionType type;
        private CodeModalDto codeModal; // Back reference to the owning CodeModal
    }

    public enum SuggestionType {
        ERROR,
        WARNING,
        INFO,
        NONE
    }
}
