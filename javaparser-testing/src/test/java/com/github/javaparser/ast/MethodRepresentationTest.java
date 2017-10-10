package com.github.javaparser.ast;

import com.github.javaparser.extend.CompilationUnitWrapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertTrue;

/**
 * Created by John Jorgensen on 3/12/2017.
 */
public class MethodRepresentationTest {

    private static final String COMPILATION_UNIT = "C:/WS_NSU/amv/src/main/java/CH/ifa/draw/util/UndoRedoActivity.java";
    private CompilationUnitWrapper compilationUnitWrapper;
    private List<String> methodTokens;

    @Before
    public void setUp() throws Exception {
        compilationUnitWrapper = new CompilationUnitWrapper(COMPILATION_UNIT);
    }

    @Test
    public void createMethodRepresentationForMethod() throws Exception {
        MethodRepresentation readMethodRepresentation = compilationUnitWrapper.getMethodRepresentation("createUndoRedoActivity");
        assertTrue("Method representation is not null",readMethodRepresentation != null);
    }

    @Test
    public void getTokenizeMethodRepresentation() throws Exception {
        MethodRepresentation methodRepresentation = compilationUnitWrapper.getMethodRepresentation("createUndoRedoActivity");
        assertTrue("Method representation is not null", methodRepresentation != null);
        assertTrue("Stringified method representation is not null", methodRepresentation.getStringifiedWithoutComments() != null);
//        System.out.println(methodRepresentation.getStringifiedWithoutComments());
        List<String> methodTokens = methodRepresentation.getMethodTokens();
        assertTrue("Method tokens is not null", methodTokens != null);
        assertTrue("Method tokens exists", methodTokens.size() > 0);
        System.out.println(methodTokens);
    }

    @Test
    public void getAllTokenizedMethodRepresentations() throws Exception {
        List<MethodRepresentation> methodRepresentations = compilationUnitWrapper.getMethodRepresentations();
        for (MethodRepresentation methodRepresentation: methodRepresentations) {
            System.out.println("*** File Path:   " + methodRepresentation.getFilePath());
            System.out.println("*** Method Name: " + methodRepresentation.getMethodName());
            System.out.println("*** Full Method Name: " + methodRepresentation.getFullMethodName());
            System.out.println("*** Class Name: " + methodRepresentation.getClassName());
            System.out.println(methodRepresentation.getMethodTokens());
        }
    }
}
