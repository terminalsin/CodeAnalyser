package com.codeanalyser.analysis.java.spoon.checks;

import com.codeanalyser.shared.codemodal.CodeModalDto;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtElement;

public abstract class CodeModalCheck<E extends CtElement> extends AbstractProcessor<E> {
    private final CodeModalDto dto;

    public CodeModalCheck(CodeModalDto dto) {
        this.dto = dto;
    }

    protected void info(final String message, final CtElement element) {
        log(message, element, CodeModalDto.SuggestionType.INFO);
    }

    protected void warning(final String message, final CtElement element) {
        log(message, element, CodeModalDto.SuggestionType.WARNING);
    }

    protected void error(final String message, final CtElement element) {
        log(message, element, CodeModalDto.SuggestionType.ERROR);
    }

    private void log(final String message, final CtElement element, final CodeModalDto.SuggestionType suggestionType) {
        dto.getResults().add(new CodeModalDto.CodeResult(
                element.prettyprint(),
                message,
                suggestionType
        ));
    }
}
