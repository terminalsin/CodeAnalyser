package com.codeanalyser.analysis.java.checkfwk;

import com.codeanalyser.analysis.java.checkfwk.processor.CheckService;
import com.codeanalyser.shared.codemodal.CodeModalDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SpoonServiceTest {

    @Autowired
    private CheckService checkService;

    private CodeModalDto codeModalDto;

    @BeforeEach
    public void setup() {
        codeModalDto = new CodeModalDto();
        codeModalDto.setCode("public class TestClass {}");
    }

    @Test
    public void analyseCodeShouldReturnSameCodeModalDtoWhenCodeIsValid() {
        CodeModalDto result = checkService.analyseCode(codeModalDto);
        assertEquals(codeModalDto, result);
    }

    @Test
    public void analyseCodeShouldParseSpoonServiceClassSuccessfully() {
        String spoonServiceClassCode =
                "package com.codeanalyser.analysis.java.checkfwk.processor;\n" +
                        "\n" +
                        "import com.codeanalyser.shared.codemodal.CodeModalDto;\n" +
                        "import org.springframework.stereotype.Service;\n" +
                        "import checkfwk.Launcher;\n" +
                        "import checkfwk.reflect.declaration.CtClass;\n" +
                        "\n" +
                        "@Service\n" +
                        "public class SpoonService {\n" +
                        "\n" +
                        "    public CodeModalDto analyseCode(CodeModalDto codeModalDto) {\n" +
                        "        CtClass analysisClass = Launcher.parseClass(codeModalDto.getCode());\n" +
                        "        return codeModalDto;\n" +
                        "    }\n" +
                        "}\n";

        codeModalDto.setCode(spoonServiceClassCode);
        CodeModalDto result = checkService.analyseCode(codeModalDto);
        assertNotNull(result);
        assertEquals(codeModalDto, result);
    }

    @Test
    public void analyseCodeShouldThrowExceptionWhenCodeIsNull() {
        codeModalDto.setCode(null);
        assertThrows(NullPointerException.class, () -> checkService.analyseCode(codeModalDto));
    }

    @Test
    public void analyseCodeShouldThrowExceptionWhenCodeIsEmpty() {
        codeModalDto.setCode("");
        assertThrows(IllegalArgumentException.class, () -> checkService.analyseCode(codeModalDto));
    }

    @Test
    public void analyseCodeShouldThrowExceptionWhenCodeIsNotValidJava() {
        codeModalDto.setCode("This is not valid Java code.");
        assertThrows(IllegalArgumentException.class, () -> checkService.analyseCode(codeModalDto));
    }
}
