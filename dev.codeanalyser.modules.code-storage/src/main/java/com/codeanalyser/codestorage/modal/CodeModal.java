package com.codeanalyser.codestorage.modal;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "code_modals")  // Specifies the table name if different from the class name
public class CodeModal {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String ownerUsername;

    private String fileName;

    @Lob
    private String code;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "code_modal_id") // This column will be added to CodeResult to reference CodeModal
    private List<CodeResult> results;

    private int score;

    @Data
    @Entity
    @Table(name = "code_results")  // Each result is a separate entity
    @EqualsAndHashCode(exclude = "codeModal") // Avoids circular references in generated equals and hashCode methods
    public static class CodeResult {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id; // Using Long for IDs of results

        @Lob
        private String code;

        @Lob
        private String highlight;

        @Enumerated(EnumType.STRING) // Store enum values as their string representation
        private SuggestionType type;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "code_modal_id", insertable = false, updatable = false)
        private transient CodeModal codeModal; // Back reference to the owning CodeModal
    }

    public enum SuggestionType {
        ERROR,
        WARNING,
        INFO,
        NONE
    }
}
