package com.codeanalyser.analysis.java.spoon;

import com.codeanalyser.analysis.java.spoon.processor.SpoonService;
import com.codeanalyser.shared.codemodal.CodeModalDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NullSafetyProcessorTest {

    @Autowired
    private SpoonService spoonService;

    private CodeModalDto codeModalDto;

    @BeforeEach
    public void setup() {
        codeModalDto = new CodeModalDto();
    }

    @Test
    public void testNullableParameterWithoutNullCheck() {
        String code = "public class TestClass {\n" +
                "    public void testMethod(String nullableParam) {\n" +
                "        System.out.println(nullableParam.length());\n" +
                "    }\n" +
                "}";
        codeModalDto.setCode(code);

        CodeModalDto result = spoonService.analyseCode(codeModalDto);

        assertFalse(result.getResults().isEmpty(), "Expected at least one warning for nullable parameter without null check");
        assertEquals(1, result.getResults().size());

        final CodeModalDto.CodeResult codeResult = result.getResults().get(0);

        assertEquals("Parameter 'nullableParam' is nullable and should be checked for null before use.", codeResult.getHighlight());
        assertEquals("nullableParam.length()", codeResult.getCode());
        assertEquals(CodeModalDto.SuggestionType.WARNING, codeResult.getType());
    }
}