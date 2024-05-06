package com.codeanalyser.shop.gpt.serializer;

import com.codeanalyser.codestorage.modal.CodeModal;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CodeModalJsonParser {

    @Autowired
    private Gson gson;

    public CodeModal parseJsonToCodeModal(String json) {
        CodeReviewDTO reviewDTO = gson.fromJson(json, CodeReviewDTO.class);
        return convertDtoToEntity(reviewDTO);
    }

    private CodeModal convertDtoToEntity(CodeReviewDTO reviewDTO) {
        CodeModal codeModal = new CodeModal();
        codeModal.setScore(reviewDTO.getScore());
        List<CodeModal.CodeResult> results = reviewDTO.getReview().stream()
                .map(CodeModalJsonParser::convertResultDtoToEntity)
                .collect(Collectors.toList());
        codeModal.setResults(results);
        return codeModal;
    }

    private static CodeModal.CodeResult convertResultDtoToEntity(CodeResultDTO resultDTO) {
        CodeModal.CodeResult result = new CodeModal.CodeResult();
        result.setCode(resultDTO.getCode());
        result.setHighlight(resultDTO.getHighlight());
        result.setType(CodeModal.SuggestionType.valueOf(resultDTO.getType()));
        return result;
    }
}
