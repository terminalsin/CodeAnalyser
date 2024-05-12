package com.codeanalyser.analysis.java.spoon.checks;

import com.codeanalyser.shared.codemodal.CodeModalDto;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.*;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NullSafetyProcessor extends CodeModalCheck<CtMethod<?>> {
    private Set<String> nullableParams = new HashSet<>();

    public NullSafetyProcessor(CodeModalDto dto) {
        super(dto);
    }

    @Override
    public void process(CtMethod<?> method) {
        nullableParams.clear();
        checkParametersForNonNull(method);
        checkVariableAccess(method);
    }

    private void checkParametersForNonNull(CtMethod<?> method) {
        for (CtParameter<?> parameter : method.getParameters()) {
            if (!hasNonNullAnnotation(parameter)) {
                nullableParams.add(parameter.getSimpleName());
            }
        }
    }

    private boolean hasNonNullAnnotation(CtParameter<?> parameter) {
        return parameter.getAnnotations().stream()
                .anyMatch(a -> a.getAnnotationType().getSimpleName().equals("NonNull"));
    }

    private void checkVariableAccess(CtMethod<?> method) {
        List<CtVariableAccess<?>> variableAccesses = method.getElements(new TypeFilter<>(CtVariableAccess.class));
        for (CtVariableAccess<?> access : variableAccesses) {
            if (nullableParams.contains(access.getVariable().getSimpleName()) && !isAccessSafe(access)) {
                this.warning(String.format(
                            "Parameter '%s' is nullable and should be checked for null before use.",
                            access.getVariable().getSimpleName()
                        ),
                        // get parent of the access to get more coverage
                        access.getParent()
                );
            }
        }
    }

    private boolean isAccessSafe(CtVariableAccess<?> access) {
        CtElement parent = access.getParent();
        while (parent != null && !(parent instanceof CtMethod)) {
            if (parent instanceof CtBinaryOperator) {
                CtBinaryOperator<?> operator = (CtBinaryOperator<?>) parent;
                if (operator.getKind() == BinaryOperatorKind.NE && operator.getLeftHandOperand() == access || operator.getRightHandOperand() == access) {
                    return true;
                }
            }
            parent = parent.getParent();
        }
        return false;
    }
}