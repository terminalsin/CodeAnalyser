package com.codeanalyser.shop.serializer;

import com.codeanalyser.shop.gpt.serializer.CodeModalJsonParser;
import com.codeanalyser.shop.code.dto.CodeModalDto;
import com.codeanalyser.shop.gpt.serializer.CodeResultDTO;
import com.codeanalyser.shop.gpt.serializer.CodeReviewDTO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CodeModalJsonParserTest {

    @InjectMocks
    @Autowired
    private CodeModalJsonParser codeModalJsonParser;

    @Autowired
    private Gson gson;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should parse JSON to CodeModalDto successfully")
    public void parseJsonToCodeModalSuccess() {
        // Create json
        String json = "{\"score\": 8, \"review\": [{\"code\": \"code\", \"highlight\": \"highlight\", \"type\": \"INFO\"}], \"name\": \"name\"}";

        // Create equivalent DTO
        CodeReviewDTO reviewDTO = new CodeReviewDTO();
        reviewDTO.setScore(8);
        reviewDTO.setName("name");
        CodeResultDTO resultDTO = new CodeResultDTO();
        resultDTO.setCode("code");
        resultDTO.setHighlight("highlight");
        resultDTO.setType("INFO");
        reviewDTO.setReview(Collections.singletonList(resultDTO));

        // Execute
        CodeModalDto codeModalDto = codeModalJsonParser.parseJsonToCodeModal(json);

        // Verify
        assertEquals(8, codeModalDto.getScore());
        assertEquals("name", codeModalDto.getFileName());
        assertEquals(1, codeModalDto.getResults().size());
        assertEquals("code", codeModalDto.getResults().get(0).getCode());
        assertEquals("highlight", codeModalDto.getResults().get(0).getHighlight());
        assertEquals(CodeModalDto.SuggestionType.INFO, codeModalDto.getResults().get(0).getType());
    }

    @Test
    @DisplayName("Should return empty CodeModalDto when JSON is empty")
    public void parseEmptyJsonToCodeModal() {
        String json = """
                {
                  "id": "893e4638-4ef7-4b86-99bb-9d86b82e52b9",
                  "name": "",
                  "review": [],
                  "score": 0
                }    
        """;

        CodeModalDto codeModalDto = codeModalJsonParser.parseJsonToCodeModal(json);

        assertEquals(0, codeModalDto.getScore());
        assertEquals("", codeModalDto.getFileName());
        assertEquals(0, codeModalDto.getResults().size());
    }

    @Test
    @DisplayName("Should throw exception when JSON is null")
    public void parseNullJsonToCodeModal() {
        assertThrows(JsonSyntaxException.class, () -> codeModalJsonParser.parseJsonToCodeModal(null));
    }

    @Test
    @DisplayName("Should throw exception when JSON is not valid")
    public void parseInvalidJsonToCodeModal() {
        String json = "{invalid json}";
        assertThrows(JsonSyntaxException.class, () -> codeModalJsonParser.parseJsonToCodeModal(json));
    }
}