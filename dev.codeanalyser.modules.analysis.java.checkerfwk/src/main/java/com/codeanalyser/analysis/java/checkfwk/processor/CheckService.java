package com.codeanalyser.analysis.java.checkfwk.processor;

import com.codeanalyser.shared.codemodal.CodeModalDto;
import com.sun.source.util.JavacTask;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.NullnessChecker;
import org.checkerframework.framework.source.SourceChecker;
import org.springframework.stereotype.Service;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CheckService {

    @SneakyThrows
    public CodeModalDto analyseCode(CodeModalDto codeModalDto) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        // Create a file with the Java code
        File sourceFile = new File("Example.java");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
            writer.write(codeModalDto.getCode());
        }
        CustomDiagnosticListener diagnosticListener = new CustomDiagnosticListener(codeModalDto);
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticListener, null, null);

        // Compile the source file
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(sourceFile));

        List<String> options = Arrays.asList(
                "-proc:only",
                "-processor",
                "org.checkerframework.checker.nullness.NullnessChecker"
        );

        // Set the custom diagnostic listener to handle errors
        JavacTask javacTask = (JavacTask) compiler.getTask(null, fileManager, diagnosticListener, options, null, compilationUnits);

        //NullnessChecker checker = new NullnessChecker();
        //javacTask.setProcessors(Arrays.asList(checker));

        // Perform the check
        javacTask.call();

        fileManager.close();

        return codeModalDto;
    }
}