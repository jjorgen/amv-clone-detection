package com.github.javaparser.extend;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.MethodRepresentation;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.MethodDescribeVisitor;
import com.github.javaparser.utils.CompilationUnitWrapperOrig;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class SimpleMethodRepresentationTest {

    CompilationUnitWrapperOrig compilationUnitWithSimpleMethodToRepresent;

    private String CLASS_WITH_SIMPLE_METHOD_TO_REPRESENT = "C:/work/0_NSU/CH/ifa/draw/contrib/dnd/DNDFigures.java";
//    private String CLASS_WITH_SIMPLE_METHOD_TO_REPRESENT = "C:/WS_NSU/amv/src/main/java/CH/ifa/draw/contrib/dnd/DNDFigures.java";
//    private String CLASS_WITH_TWO_PUBLIC_METHODS = "C:\\WS_NSU\\amv\\src\\main\\java\\CH\\ifa\\draw\\contrib\\dnd\\DNDFigures.java";
    private String CLASS_WITH_TWO_PUBLIC_METHODS = "C:/work/0_NSU/CH/ifa/draw/contrib/dnd/DNDFigures.java";
    private MethodDescribeVisitor methodDescribeVisitor = new MethodDescribeVisitor();
    private CompilationUnitWrapper compilationUnitWrapper;

    @Before
    public void setUp() throws Exception {
        compilationUnitWrapper = new CompilationUnitWrapper(CLASS_WITH_SIMPLE_METHOD_TO_REPRESENT);
        compilationUnitWithSimpleMethodToRepresent = new CompilationUnitWrapperOrig(CLASS_WITH_SIMPLE_METHOD_TO_REPRESENT);
    }

    @Ignore
    @Test
    public void getMethodRepresentation() throws Exception {
        CompilationUnit firstCompilationUnit = compilationUnitWrapper.getCompilationUnit(CLASS_WITH_SIMPLE_METHOD_TO_REPRESENT);
        MethodRepresentation methodDeclaration = compilationUnitWrapper.getMethodRepresentationFor("getFigures", firstCompilationUnit);
//        methodDescribeVisitor.visit(methodDeclaration, null);
//        MethodRepresentation methodRepresentation = methodDescribeVisitor.getMethodRepresentation();
//        assertTrue(methodRepresentation.getParameters().size() == 0);
//        assertTrue(methodRepresentation.getThrows().size() == 0);
//        assertTrue(methodRepresentation.getBodyList().size() > 0);
    }

    @Test
    public void dumpVisitorWithoutCommentsTest() throws Exception {
        CompilationUnit firstCompilationUnit = CompilationUnitWrapper.getCompilationUnit(CLASS_WITH_TWO_PUBLIC_METHODS);
        System.out.println(firstCompilationUnit.toStringWithoutComments());
    }

//    @Ignore
    @Test
    public void getMethodDescriptionTest() throws Exception {
        System.out.println("Start of test");
        List<Node> nodeCollection = compilationUnitWithSimpleMethodToRepresent.getClassDescription();
        for (Node node: nodeCollection) {
            System.out.println(node.toString());
        }
        System.out.println("End of test");
    }
}
