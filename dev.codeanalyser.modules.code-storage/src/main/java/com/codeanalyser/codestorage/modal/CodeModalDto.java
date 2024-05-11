package com.codeanalyser.codestorage.modal;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class CodeModalDto {
    private UUID id;
    private String fileName;
    private String ownerUsername;
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

        public CodeModal.CodeResult toCodeResult(final CodeModal parent) {
            CodeModal.CodeResult codeResult = new CodeModal.CodeResult();
            codeResult.setId(this.id);
            codeResult.setCode(this.code);
            codeResult.setHighlight(this.highlight);
            codeResult.setType(this.type.toSuggestionType());
            codeResult.setCodeModal(parent);
            return codeResult;
        }
    }

    public enum SuggestionType {
        ERROR,
        WARNING,
        INFO,
        NONE;

        public CodeModal.SuggestionType toSuggestionType() {
            return CodeModal.SuggestionType.valueOf(this.name());
        }
    }

    public CodeModal toCodeModal() {
        CodeModal codeModal = new CodeModal();
        codeModal.setId(this.id);
        codeModal.setOwnerUsername(this.ownerUsername);
        codeModal.setFileName(this.fileName);
        codeModal.setCode(this.code);
        codeModal.setResults(this.results.stream().map(result -> result.toCodeResult(codeModal)).collect(Collectors.toList()));
        codeModal.setScore(this.score);
        return codeModal;
    }
}
