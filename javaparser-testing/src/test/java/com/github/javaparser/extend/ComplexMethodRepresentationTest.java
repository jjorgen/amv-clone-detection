package com.github.javaparser.extend;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.MethodRepresentation;
import com.github.javaparser.ast.visitor.MethodDescribeVisitor;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ComplexMethodRepresentationTest {

    public static final String METHOD_NAME = "substituteEntityKeywords";
    private static String CLASS_WITH_ONE_LARGE_METHOD = "C:\\WS\\javaparser\\javaparser-testing\\src\\test\\resources\\extension\\HTMLTextAreaFigureTestClass.java";

    private MethodDescribeVisitor methodDescribeVisitor = new MethodDescribeVisitor();
    private CompilationUnit compilationUnit;
    private CompilationUnitWrapper compilationUnitWrapper;

    @Before
    public void setUp() throws Exception {
        compilationUnitWrapper = new CompilationUnitWrapper(CLASS_WITH_ONE_LARGE_METHOD);
        compilationUnit = CompilationUnitWrapper.getCompilationUnit(CLASS_WITH_ONE_LARGE_METHOD);
    }

    @Test
    public void getMethodRepresentation() throws Exception {
        MethodRepresentation methodRepresentation = compilationUnitWrapper.getMethodRepresentationFor(METHOD_NAME, compilationUnit);
        assertTrue(methodRepresentation.getParameters().size() > 0);
        assertTrue(methodRepresentation.getThrows().size() == 0);
        assertEquals("Method names should be equal", METHOD_NAME, methodRepresentation.getMethodName());
   }

    @Test
    public void createCompilationUnitWrapper() throws Exception {
        CompilationUnitWrapper compilationUnitWrapper = new CompilationUnitWrapper(CLASS_WITH_ONE_LARGE_METHOD);

    }
}
