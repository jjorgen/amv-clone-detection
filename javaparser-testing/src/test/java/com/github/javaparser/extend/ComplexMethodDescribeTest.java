package com.github.javaparser.extend;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.MethodRepresentation;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

public class ComplexMethodDescribeTest {

    private static String CLASS_WITH_ONE_LARGE_METHOD = "C:\\WS\\javaparser\\javaparser-testing\\src\\test\\resources\\extension\\HTMLTextAreaFigureTestClass.java";
    private static CompilationUnit compilationUnit;
    private CompilationUnitWrapper compilationUnitWrapper;

    @Before
    public void setUp() throws Exception {
        compilationUnitWrapper = new CompilationUnitWrapper(CLASS_WITH_ONE_LARGE_METHOD);
    }

    @Test
    public void parseMethodWithLargeBodyTest() throws Exception {
        MethodRepresentation methodDeclaration = getMethodDeclaration();
        String stringifiedMethodDeclaration = methodDeclaration.getStringifiedWithoutComments();
        System.out.println(stringifiedMethodDeclaration);
    }

    public MethodRepresentation getMethodDeclaration() throws IOException {
        compilationUnit = CompilationUnitWrapper.getCompilationUnit(CLASS_WITH_ONE_LARGE_METHOD);
        MethodRepresentation methodDeclaration = compilationUnitWrapper.getMethodRepresentationFor("substituteEntityKeywords", compilationUnit);
        return methodDeclaration;
    }
}
