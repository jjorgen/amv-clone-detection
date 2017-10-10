package com.github.javaparser.ast;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ReferenceType;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by John on 1/31/2017.
 */
public class MethodRepresentation {
    private NodeList<Parameter> parameters;
    private NodeList<ReferenceType<?>> aThrows;
    private ArrayList<Node> bodyList = new ArrayList<>();
    private String stringifiedWithoutComments;
    private String methodName;
    private String className;
    private String filePath;

    public MethodRepresentation(String filePath, String className) {
        this.filePath = filePath;
        this.className = className;
    }

    public NodeList<ReferenceType<?>> getThrows() {
        return aThrows;
    }

    public NodeList<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(NodeList<Parameter> parameters) {
        this.parameters = parameters;
    }

    public void setThrows(NodeList<ReferenceType<?>> aThrows) {
        this.aThrows = aThrows;
    }

    public void addToBody(Node node) {
        bodyList.add(node);
    }

    public ArrayList<Node> getBodyList() {
        return bodyList;
    }

    public void setStringifiedWithoutComments(String stringifiedWithoutComments) {
        this.stringifiedWithoutComments = stringifiedWithoutComments;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getStringifiedWithoutComments() {
        return stringifiedWithoutComments;
    }

    public List<String> getMethodTokens() {
        List<String> methodTokens = new ArrayList<>();
        int startPos = stringifiedWithoutComments.indexOf("{") + 1;
        int endPos = stringifiedWithoutComments.lastIndexOf("}");
        if (startPos != -1 && endPos != -1) {
            String methodBody = stringifiedWithoutComments.substring(startPos, endPos);
            StringTokenizer methodBodyTokenizer = new StringTokenizer(methodBody, "\r\n");
            while (methodBodyTokenizer.hasMoreElements()) {
                methodTokens.add(leftTrim(methodBodyTokenizer.nextToken()));
            }
        }
        return methodTokens;
    }

    private static String leftTrim(String s) {
        int i = 0;
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
            i++;
        }
        return s.substring(i);
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFullMethodName() {
        return className + "." + methodName;
    }

    public String getClassName() {
        return className;
    }
}
