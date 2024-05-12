package com.codeanalyser.analysis.java.checkfwk;

import com.codeanalyser.analysis.java.checkfwk.api.TestFileParser;
import com.codeanalyser.analysis.java.checkfwk.processor.CheckService;
import com.codeanalyser.shared.codemodal.CodeModalDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.fail;

@SpringBootTest
public class CustomTestRunner {
    @Autowired
    private CheckService checkService;

    @Test
    public void runCustomTests() {
        try {
            System.out.println(Paths.get("").getFileName().toFile().getAbsoluteFile());
            List<TestFileParser.TestData> tests = TestFileParser.parseTestFiles(Paths.get("src/test/checks/"));
            for (TestFileParser.TestData testData : tests) {
                runTest(testData.getCode(), testData.getExpectedOutput());
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error running tests: " + e.getMessage());
        }
    }

    private void runTest(String code, String expectedOutput) {
        // Mock or simulate the execution of the code, capturing the output
        String actualOutput = executeCode(code);
        assertEquals(expectedOutput, actualOutput, "Test failed for code: " + code);
    }

    private String executeCode(String code) {
        final CodeModalDto codeModalDto = new CodeModalDto();
        codeModalDto.setCode(code);

        checkService.analyseCode(codeModalDto);

        // Here you would need to actually compile and run the code, capturing stdout
        // This is a complex task and might require integrating a Java compiler and runtime
        // For simplicity, this function is just a placeholder
        return codeModalDto.getResults()
                .stream()
                .map(e -> e.getCode() + "\n" + e.getType().name() + ": " + e.getHighlight())
                .collect(Collectors.joining("\n"));
    }
}
