package com.github.javaparser.extend;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.MethodRepresentation;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.util.List;

public class CompilationUnitWrapperTest {

    private static final String COMPILATION_UNIT_PATH = "C:/work/0_NSU/CH/ifa/draw/contrib/GraphicalCompositeFigure.java";
    private CompilationUnitWrapper compilationUnitWrapper;

    @Before
    public void setUp() throws Exception {
        compilationUnitWrapper = new CompilationUnitWrapper(COMPILATION_UNIT_PATH);
    }

    @Test
    public void getCompilationUnit() throws Exception {
        CompilationUnit compilationUnit = compilationUnitWrapper.getCompilationUnit(COMPILATION_UNIT_PATH);
        compilationUnit.getImports();
        System.out.println("test");
    }

    @Test
    public void getMethodRepresentationsForCompilationUnit() throws Exception {
        List<MethodRepresentation> methodRepresentations = compilationUnitWrapper.getMethodRepresentations();
        assertTrue(methodRepresentations != null && methodRepresentations.size() > 0);
        for (MethodRepresentation methodRepresentation : methodRepresentations) {
            assertTrue(!methodRepresentation.getStringifiedWithoutComments().isEmpty());
        }
    }

    @Test
    public void getFilePath() throws Exception {
        String filePath = compilationUnitWrapper.getFilePath();
        assertTrue(filePath != null && filePath.trim().length() > 0);
        System.out.println(filePath);
    }

    @Test
    public void getValidJavaClassName() throws Exception {
        String className = compilationUnitWrapper.getClassOrInterfaceName();
        assertTrue(className != null && className.trim().length() > 0);
    }
}
