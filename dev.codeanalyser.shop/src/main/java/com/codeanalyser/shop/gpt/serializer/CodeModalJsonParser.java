package com.codeanalyser.shop.gpt.serializer;

import com.codeanalyser.shop.code.dto.CodeModalDto;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CodeModalJsonParser {

    private final Gson gson;

    @Autowired
    public CodeModalJsonParser(Gson gson) {
        this.gson = gson;
    }

    public CodeModalDto parseJsonToCodeModal(String json) {
        CodeReviewDTO reviewDTO = gson.fromJson(json, CodeReviewDTO.class);

        if (reviewDTO == null) {
            throw new JsonSyntaxException("Invalid JSON format");
        }

        return convertDtoToEntity(reviewDTO);
    }

    private CodeModalDto convertDtoToEntity(CodeReviewDTO reviewDTO) {
        CodeModalDto codeModal = new CodeModalDto();
        codeModal.setScore(reviewDTO.getScore());
        List<CodeModalDto.CodeResult> results = reviewDTO.getReview().stream()
                .map(CodeModalJsonParser::convertResultDtoToEntity)
                .collect(Collectors.toList());
        codeModal.setResults(results);
        codeModal.setFileName(reviewDTO.getName());
        return codeModal;
    }

    private static CodeModalDto.CodeResult convertResultDtoToEntity(CodeResultDTO resultDTO) {
        CodeModalDto.CodeResult result = new CodeModalDto.CodeResult();
        result.setCode(resultDTO.getCode());
        result.setHighlight(resultDTO.getHighlight());
        result.setType(CodeModalDto.SuggestionType.valueOf(resultDTO.getType()));
        return result;
    }
}
