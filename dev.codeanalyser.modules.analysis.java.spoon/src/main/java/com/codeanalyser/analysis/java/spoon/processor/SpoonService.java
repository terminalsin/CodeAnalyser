package com.codeanalyser.analysis.java.spoon.processor;

import com.codeanalyser.analysis.java.spoon.checks.NullSafetyProcessor;
import com.codeanalyser.shared.codemodal.CodeModalDto;
import org.springframework.stereotype.Service;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.compiler.SpoonResource;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.support.compiler.VirtualFile;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class SpoonService {

    public CodeModalDto analyseCode(CodeModalDto codeModalDto) {
        codeModalDto.setResults(new ArrayList<>());

        Launcher launcher = new Launcher();
        launcher.addInputResource((SpoonResource)(new VirtualFile(codeModalDto.getCode())));
        launcher.getEnvironment().setNoClasspath(true);
        launcher.getEnvironment().setAutoImports(true);
        Collection<CtType<?>> allTypes = launcher.buildModel().getAllTypes();
        if (allTypes.size() != 1) {
            throw new SpoonException("parseClass only considers one class. Please consider using a Launcher object for more advanced usage.");
        }

        final CtClass<?> clazz = (CtClass)allTypes.stream().findFirst().get();

        launcher.addProcessor(new NullSafetyProcessor(codeModalDto));
        launcher.process();

        return codeModalDto;
    }
}
