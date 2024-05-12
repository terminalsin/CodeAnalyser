package com.codeanalyser.analysis.java.checkfwk.processor;

import com.codeanalyser.shared.codemodal.CodeModalDto;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomDiagnosticListener implements DiagnosticListener<JavaFileObject> {
    private final CodeModalDto dto;

    public CustomDiagnosticListener(CodeModalDto dto) {
        this.dto = dto;
        dto.setResults(new ArrayList<>());
    }

    @Override
    public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
        final CodeModalDto.SuggestionType type = switch (diagnostic.getKind()) {
            case ERROR -> CodeModalDto.SuggestionType.ERROR;
            case WARNING -> CodeModalDto.SuggestionType.WARNING;
            case MANDATORY_WARNING -> CodeModalDto.SuggestionType.WARNING;
            case NOTE -> CodeModalDto.SuggestionType.INFO;
            default -> CodeModalDto.SuggestionType.NONE;
        };

        dto.getResults().add(new CodeModalDto.CodeResult(
                diagnostic.getMessage(Locale.getDefault()),
                diagnostic.getMessage(Locale.getDefault()),
                type
        ));
    }
}
