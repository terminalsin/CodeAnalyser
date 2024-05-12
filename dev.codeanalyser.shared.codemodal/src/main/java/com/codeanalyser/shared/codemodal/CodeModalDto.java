package com.codeanalyser.shared.codemodal;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
public class CodeModalDto {
    private UUID id;
    private String ownerUsername;

    private String fileName;

    private String code;
    private List<CodeResult> results;
    private int score;

    @Data
    @EqualsAndHashCode() // Avoids circular references in generated equals and hashCode methods
    public static class CodeResult {
        private Long id; // Using Long for IDs of results

        private String code;
        private String highlight;
        private SuggestionType type;

        public CodeResult(String code, String highlight, SuggestionType type) {
            this.code = code;
            this.highlight = highlight;
            this.type = type;
        }

        public CodeResult() {
        }
    }

    public enum SuggestionType {
        ERROR,
        WARNING,
        INFO,
        NONE
    }
}
