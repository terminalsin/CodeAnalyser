package com.codeanalyser.analysis.java.checkfwk.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestFileParser {
    public static List<TestData> parseTestFiles(Path directory) throws IOException {
        List<TestData> testCases = new ArrayList<>();
        Files.walk(directory)
            .filter(Files::isRegularFile)
            .forEach(path -> testCases.add(parseFile(path)));
        return testCases;
    }

    private static TestData parseFile(Path filePath) {
        try {
            String content = Files.readString(filePath);
            String[] parts = content.split("===== Output =====");
            String code = parts[0].trim();
            String expectedOutput = parts.length > 1 ? parts[1].trim() : "";
            return new TestData(code, expectedOutput);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + filePath, e);
        }
    }

    public static class TestData {
        private final String code;
        private final String expectedOutput;

        public TestData(String code, String expectedOutput) {
            this.code = code;
            this.expectedOutput = expectedOutput;
        }

        public String getCode() {
            return code;
        }

        public String getExpectedOutput() {
            return expectedOutput;
        }
    }
}
