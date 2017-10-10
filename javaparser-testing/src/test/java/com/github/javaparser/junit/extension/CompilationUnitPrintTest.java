package com.github.javaparser.junit.extension;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.visitor.EqualsVisitor;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CompilationUnitPrintTest {

    private Object compilationUnitWithThreePublicMethods;
    private Node compilationUnitWithALrgeMethodBody;

    @Test
    public void parseMethodWithLargeBodyTest() throws Exception {
        List<MethodDeclaration> methodDeclarations = getCompilationUnitWithLargeMethodBody().getNodesByType(MethodDeclaration.class);
        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            if (methodDeclaration.getDeclarationAsString().contains("substituteEntityKeywords")) {
                List<Node> childrenNodes1 = methodDeclaration.getChildrenNodes();
                System.out.println(methodDeclaration.getDeclarationAsString());
                Optional<BlockStmt> body = methodDeclaration.getBody();
                if (body.isPresent()) {
                    BlockStmt blockStmt = body.get();
                    getBodyHierarchy(blockStmt);
                }
            }
        }
    }

    @Test
    public void parseMethodWithOneLargeMethodTest() throws Exception {
        List<MethodDeclaration> methodDeclarations = getCompilationUnitWithOneLargeMethod().getNodesByType(MethodDeclaration.class);
        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            if (methodDeclaration.getDeclarationAsString().contains("substituteEntityKeywords")) {
                List<Node> childrenNodes1 = methodDeclaration.getChildrenNodes();
                System.out.println(methodDeclaration.getDeclarationAsString());
                Optional<BlockStmt> body = methodDeclaration.getBody();
                if (body.isPresent()) {
                    BlockStmt blockStmt = body.get();
                    getBodyHierarchy(blockStmt);
                }
            }
        }
    }



    @Test
    public void getMethodRepresentation() throws Exception {
        CompilationUnit firstCompilationUnit = getCompilationUnitWithTwoPublicMethods();
        MethodDeclaration firstMethodDeclaration = getMethodByName(firstCompilationUnit, "getFigures");

    }

    @Test
    public void compareTwoEqualMethods() throws Exception {
        CompilationUnit firstCompilationUnit = getCompilationUnitWithTwoPublicMethods();
        MethodDeclaration firstMethodDeclaration = getMethodByName(firstCompilationUnit, "getFigures");

        CompilationUnit secondCompilationUnit = getCompilationUnitWithTwoPublicMethods();
        MethodDeclaration secondMethodDeclaration = getMethodByName(secondCompilationUnit, "getFigures");

        boolean equals = EqualsVisitor.equals(firstMethodDeclaration, secondMethodDeclaration);
        assertTrue(equals);
    }

    private MethodDeclaration getMethodByName(CompilationUnit compilationUnit, String methodName) {
        List<MethodDeclaration> methodDeclarations = compilationUnit.getNodesByType(MethodDeclaration.class);
        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            if (methodDeclaration.getName().equals(methodName)) {
                return methodDeclaration;
            }
        }
        return null;
    }

    @Test
    public void getAllMethodDeclarationsForClassWithManyMethodsTest() throws Exception {
        CompilationUnit compilationUnitForClassWithManyMethods = getCompilationUnitForClassWithManyMethods();
        List<MethodDeclaration> methodDeclarations = compilationUnitForClassWithManyMethods.getNodesByType(MethodDeclaration.class);
        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            System.out.println("Method Name: " + methodDeclaration.getName());

            String methodDeclarationAsString = getMethodDeclarationAsString(methodDeclaration);
            System.out.println(methodDeclarationAsString);
            Optional<BlockStmt> body = methodDeclaration.getBody();
//            System.out.println(body);
        }
    }

    @Test
    public void getThreeMethodsInAClassTest() throws Exception {
        List<MethodDeclaration> methodDeclarations = getCompilationUnitWithThreePublicMethods().getNodesByType(MethodDeclaration.class);
        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            String methodDeclarationAsString = getMethodDeclarationAsString(methodDeclaration);
            System.out.println(methodDeclarationAsString);

            System.out.println(getReturnType(methodDeclaration));

            List<String> modifiers = getModifiers(methodDeclaration.getModifiers());
            System.out.println(modifiers);

            List<String> methodThrows = getMethodThrows(methodDeclaration.getThrows());
            System.out.println(methodThrows);

            List<String> methodParameters = getMethodParameters(methodDeclaration.getParameters());
            System.out.println(methodParameters);
        }
    }

    @Ignore
    @Test
    public void compareMethodsTest() throws Exception {
        BlockStmt methodBlockStatement1 = getMethodBlockStatement();
        BlockStmt methodBlockStatementClone = getMethodBlockStatementClone();
        boolean equals = EqualsVisitor.equals(methodBlockStatement1, methodBlockStatementClone);
        assertTrue(equals);
    }

    private void getBodyHierarchy(Node node) {
        List<Node> childrenNodes = node.getChildrenNodes();

        if (!childrenNodes.isEmpty()) {
            System.out.println(node.toString());
            System.out.println("*** " + node.getClass().getName());
            return;
        } else {
            for (Node childNode : childrenNodes) {
                getBodyHierarchy(childNode);
            }
        }
    }

    private String getReturnType(MethodDeclaration methodDeclaration) {
        return methodDeclaration.getElementType().toString();
    }

    // Return method modifier(s) such as void, public, static, etc
    private List<String> getModifiers(EnumSet<Modifier> modifiers) {
        List<String> modifierList = new ArrayList<String>();
        for (Modifier modifier : modifiers) {
            modifierList.add(modifier.toString());
        }
        return modifierList;
    }

    // Return method parameters
    private List<String> getMethodParameters(NodeList<Parameter> parameters) {
        List<String> parameterList = new ArrayList<>();
        for (Parameter parameter: parameters) {
            parameterList.add(parameter.getElementType().toString());
        }
        return parameterList;
    }

    // This method returns a method declaration as a string
    private String getMethodDeclarationAsString(MethodDeclaration methodDeclaration) {
        return methodDeclaration.getDeclarationAsString();
    }

    // This method returns list of throws for a method
    private List<String> getMethodThrows(NodeList<ReferenceType<?>> aThrows) {
        List<String> throwsList = new ArrayList<>();
        for (ReferenceType thr : aThrows) {
            throwsList.add(thr.toStringWithoutComments());
        }
        return throwsList;
    }


    private CompilationUnit getCompilationUnit(FileInputStream in) throws IOException {
        CompilationUnit compilationUnit;
        try {
            // parse the file
            compilationUnit = JavaParser.parse(in);
        } finally {
            in.close();
        }
        return compilationUnit;
    }

    public CompilationUnit getCompilationUnitWithTwoPublicMethods() throws Exception{
        FileInputStream in = new FileInputStream("C:\\WS_NSU\\amv\\src\\main\\java\\CH\\ifa\\draw\\contrib\\dnd\\DNDFigures.java");
        return getCompilationUnit(in);
    }

    public CompilationUnit getCompilationUnitWithThreePublicMethods() throws Exception{
        FileInputStream in = new FileInputStream("C:\\WS_NSU\\amv\\src\\main\\java\\CH\\ifa\\draw\\contrib\\dnd\\DNDFiguresTransferable.java");
        return getCompilationUnit(in);
    }

    private CompilationUnit getCompilationUnitForClassWithManyMethods() throws Exception{
        System.out.println("HTMLTextAreaFigure.java");
        FileInputStream in = new FileInputStream("C:\\WS_NSU\\amv\\src\\main\\java\\CH\\ifa\\draw\\contrib\\html\\HTMLTextAreaFigure.java");
        return getCompilationUnit(in);
    }

    public Node getCompilationUnitWithLargeMethodBody() throws IOException {
        FileInputStream in = new FileInputStream("C:\\WS_NSU\\amv\\src\\main\\java\\CH\\ifa\\draw\\contrib\\html\\HTMLTextAreaFigure.java");
        return getCompilationUnit(in);
    }

    public Node getCompilationUnitWithOneLargeMethod() throws IOException {
        FileInputStream in = new FileInputStream("C:\\WS\\javaparser\\javaparser-testing\\src\\test\\resources\\extension\\HTMLTextAreaFigureTestClass.java");
        return getCompilationUnit(in);
    }

    public Node getCompilationUnitWithLargeMethodBodyClone() throws IOException {
        FileInputStream in = new FileInputStream("C:\\WS_NSU\\amv\\src\\main\\java\\CH\\ifa\\draw\\contrib\\html\\HTMLTextAreaFigureClone.java");
        return getCompilationUnit(in);
    }

    public BlockStmt getMethodBlockStatement() throws Exception {
        List<MethodDeclaration> methodDeclarations = getCompilationUnitWithLargeMethodBody().getNodesByType(MethodDeclaration.class);
        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            if (methodDeclaration.getDeclarationAsString().contains("substituteEntityKeywords")) {
                List<Node> childrenNodes1 = methodDeclaration.getChildrenNodes();
                System.out.println(methodDeclaration.getDeclarationAsString());
                Optional<BlockStmt> body = methodDeclaration.getBody();
                if (body.isPresent()) {
                    BlockStmt blockStmt = body.get();
                    return blockStmt;
                }
            }
        }
        return null;
    }

    public BlockStmt getMethodBlockStatementClone() throws Exception {
        List<MethodDeclaration> methodDeclarations = getCompilationUnitWithLargeMethodBodyClone().getNodesByType(MethodDeclaration.class);
        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            if (methodDeclaration.getDeclarationAsString().contains("substituteEntityKeywords")) {
                List<Node> childrenNodes1 = methodDeclaration.getChildrenNodes();
                System.out.println(methodDeclaration.getDeclarationAsString());
                Optional<BlockStmt> body = methodDeclaration.getBody();
                if (body.isPresent()) {
                    BlockStmt blockStmt = body.get();
                    return blockStmt;
                }
            }
        }
        return null;
    }

}


