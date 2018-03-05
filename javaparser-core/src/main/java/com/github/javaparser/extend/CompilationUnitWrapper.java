package com.github.javaparser.extend;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.MethodRepresentation;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.MethodDescribeVisitor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompilationUnitWrapper {
    public static int METHOD_AT_BEGINNING = 1;
    public static int METHOD_AT_END = 2;
    public static int ALL_METHODS = 3;

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


    public List<Statement> getNamesOfMethodsCalledFromMethod(String methodName, int calledMethodType) {
        List<Statement> calledMethods = new ArrayList<>();
        CompilationUnit compilationUnit = null;
        try {
            compilationUnit = getCompilationUnit(this.filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<MethodDeclaration> methodDeclarations = compilationUnit.getNodesByType(MethodDeclaration.class);
        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            if (methodDeclaration.getDeclarationAsString().contains(methodName)) {
                List<Node> childrenNodes1 = methodDeclaration.getChildrenNodes();
//                System.out.println("" + methodDeclaration.getDeclarationAsString());

                // Get the body of the method declaration. This should then be the method body.
                Optional<BlockStmt> body = methodDeclaration.getBody();

                // If there is a body for this method declaration then explore this further.
                if (body.isPresent()) {

                    // Get the block statement of the body which should contain the individual
                    // statements within the body.
                    BlockStmt blockStmt = body.get();

                    // Explore further the individual statements within the method body.
                    Integer statementCount = 0;
                    NodeList<Statement> statements = ((BlockStmt) blockStmt).getStmts();
                    if (statements.size() > 1) {
                        extractCalledMethods(blockStmt, calledMethods, calledMethodType, statementCount);
                    }
                }
            }
        }
        return calledMethods;
    }

    /**
     * This method explores the individual statements within the method body. This method is a
     * recursive method which was written to print the body contents of a method.
     * @param node  contains the individual statements in the method body if the method
     *                   body is present for the method.
     * @param calledMethods
     * @param calledMethodType
     * @param statementCount
     */
    private void extractCalledMethods(Node node, List<Statement> calledMethods, int calledMethodType, Integer statementCount) {
        if (node instanceof BlockStmt) {
            NodeList<Statement> statements = ((BlockStmt) node).getStmts();
            for (Statement statement : statements) {
                ++statementCount;
                if (statement instanceof ExpressionStmt) {
//                    System.out.println("*** Expression statement ***   : "  + statement);
//                    System.out.println(statement);
                    extractCalledMethodFromStatement(statement, calledMethods);
                } else if (statement instanceof TypeDeclarationStmt) {
//                    System.out.println(statement);
                } else if (statement instanceof BreakStmt) {
//                    System.out.println(statement);
                } else if (statement instanceof ThrowStmt) {
//                    System.out.println(statement);
                } else if (statement instanceof ContinueStmt) {
//                    System.out.println(statement);
                } else if (statement instanceof ReturnStmt) {
//                    System.out.println(statement);
                } else if (statement instanceof TryStmt) {
//                    System.out.println(statement);       PRINTS OUT EVERYTHING
//                    System.out.println("TryStmt");
                    extractCalledMethods(((TryStmt) statement).getTryBlock(), calledMethods, calledMethodType, statementCount);
                } else if (statement instanceof BlockStmt) {
//                    System.out.println("Recursive call");
                    extractCalledMethods(statement, calledMethods, calledMethodType, statementCount);
                } else if (statement instanceof WhileStmt) {
//                    System.out.println("WhileStmt");
                    extractCalledMethods(((BlockStmt) ((WhileStmt) statement).getBody()), calledMethods, calledMethodType, statementCount);
                } else if (statement instanceof IfStmt) {
                    if (((IfStmt) statement).getThenStmt() instanceof ReturnStmt) {
                        // Do Nothing
                    }
                    else if (((IfStmt) statement).getThenStmt() instanceof ExpressionStmt) {
                        extractCalledMethodFromStatement(statement, calledMethods);
                    } else {
                        extractCalledMethods((BlockStmt) ((IfStmt) statement).getThenStmt(), calledMethods, calledMethodType, statementCount);
                    }
                }
//                } else if (statement instanceof IfStmt) {
//                    System.out.println("IfStmt");
//                    extractCalledMethods((BlockStmt) ((IfStmt) statement).getThenStmt(), calledMethods, calledMethodType, statementCount);
//                }
                if (statementCount == 1 && calledMethodType == METHOD_AT_BEGINNING) {
                    return;
                }
            }
        }
    }

    private void extractCalledMethodFromStatement(Statement statement, List<Statement> calledMethods) {
        calledMethods.add(statement);
    }

    public String getNameOfLastMethodCalledFromMethod(String writeStorable) {
        List<Statement> calledMethods = getNamesOfMethodsCalledFromMethod("writeStorable",
                CompilationUnitWrapper.METHOD_AT_END);

        if (calledMethods.size() == 0) {
            return new String();
        }
        return getMethodNameAsStringFor(calledMethods.get(calledMethods.size() - 1));
    }

    private static String getMethodNameAsStringFor(Statement statement) {
        String statementAsString = statement.toString().replaceAll(" ","");
        System.out.println("statementAsString: "  + statementAsString);

        if (statementAsString.startsWith("//")) {
            return new String();
        } else {
            int periodPos = statementAsString.lastIndexOf(".");
//            int periodPos = statementAsString.indexOf(".");
            if (!(periodPos > 0)) {
                return new String();
            } else {
                if (periodPos == statementAsString.length()) {
                    new String();
                } else {
                    String methodString = statementAsString.substring(periodPos + 1, statementAsString.length() - 1);
                    if (!methodString.contains("(")) {
                        new String();
                    } else {
                        int parenthesesPos = methodString.indexOf("(");
                        return methodString.substring(0, parenthesesPos);
                    }
                }
            }
        }
        return new String();
    }
}
