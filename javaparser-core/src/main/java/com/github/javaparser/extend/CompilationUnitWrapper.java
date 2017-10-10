package com.github.javaparser.extend;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.MethodRepresentation;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.MethodDescribeVisitor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CompilationUnitWrapper {
    private CompilationUnit compilationUnit;
    private String filePath;
    private List<MethodRepresentation> methodRepresentations = new ArrayList<>();
    private String className;

    public CompilationUnitWrapper(String filePath) {
        validate(filePath);
        this.filePath = filePath;
        createCompilationUnit(filePath);
        List<MethodDeclaration> methodDeclarations = compilationUnit.getNodesByType(MethodDeclaration.class);
        MethodDescribeVisitor methodDescribeVisitor = new MethodDescribeVisitor(filePath, this);
        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            methodDescribeVisitor.visit(methodDeclaration, null);
            methodRepresentations.add(methodDescribeVisitor.getMethodRepresentation());
        }
    }

    private void validate(String filePath) {
        if (filePath == null || filePath.trim().length() == 0) {
            throw new IllegalArgumentException("Missing value for path: '" + filePath + "'");
        }
    }

    private void createCompilationUnit(String filePath) {
        try {
            FileInputStream in = new FileInputStream(filePath);
            compilationUnit = JavaParser.parse(in);
            in.close();
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when creating compilation unit for File Path: " + filePath, e);
        }
    }

    public static CompilationUnit getCompilationUnit(String filePath) throws IOException {
        FileInputStream in = new FileInputStream(filePath);

        CompilationUnit compilationUnit;
        try {
            // parse the file
            compilationUnit = JavaParser.parse(in);
        } finally {
            in.close();
        }
        return compilationUnit;
    }

    public MethodRepresentation getMethodRepresentationFor(String methodName, CompilationUnit compilationUnit) {
        MethodRepresentation methodRepresentation = null;

        List<MethodDeclaration> methodDeclarations = compilationUnit.getNodesByType(MethodDeclaration.class);
        methodRepresentation = getMethodRepresentation(methodName, methodDeclarations, compilationUnit);
        return methodRepresentation;
    }

    private MethodRepresentation getMethodRepresentation(String methodName, List<MethodDeclaration> methodDeclarations, CompilationUnit compilationUnit) {
        MethodRepresentation methodRepresentation = null;
        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            if (methodDeclaration.getName().equals(methodName)) {
                MethodDescribeVisitor methodDescribeVisitor = new MethodDescribeVisitor(filePath, this);
                methodDescribeVisitor.visit(methodDeclaration, compilationUnit);
                methodRepresentation = methodDescribeVisitor.getMethodRepresentation();
            }
        }
        return methodRepresentation;
    }

    public List<MethodRepresentation> getMethodRepresentations() {
        return methodRepresentations;
    }

    public MethodRepresentation getMethodRepresentation(String methodName) {
        MethodRepresentation methodRepresentation = null;
        List<MethodDeclaration> methodDeclarations = compilationUnit.getNodesByType(MethodDeclaration.class);
        methodRepresentation = getMethodRepresentation(methodName, methodDeclarations, compilationUnit);
        return methodRepresentation;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getClassName() {
        int lastDelimiterIndex = filePath.lastIndexOf("\\");
        if (lastDelimiterIndex  == -1) {
            lastDelimiterIndex = filePath.lastIndexOf("/");
        }
        int indexOfPeriod = filePath.lastIndexOf(".");
        if (indexOfPeriod == -1 || lastDelimiterIndex == -1 ||  !(indexOfPeriod > lastDelimiterIndex)) {
            throw new IllegalStateException("Invalid Java file path: '" + filePath + "'");
        }
        return  filePath.substring(lastDelimiterIndex + 1, indexOfPeriod);
    }


}
