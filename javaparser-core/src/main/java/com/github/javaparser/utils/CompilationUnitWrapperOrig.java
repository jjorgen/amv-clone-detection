package com.github.javaparser.utils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class CompilationUnitWrapperOrig {

    private FileInputStream in;
    private Object fileAsString;
    private CompilationUnit compilationUnit;

    public CompilationUnitWrapperOrig(String fileName) throws Exception {
        FileInputStream in = new FileInputStream("C:\\work\\0_NSU\\CH\\ifa\\draw\\contrib\\dnd/DNDFigures.java");
        try {
            // parse the file
            compilationUnit = JavaParser.parse(in);
        } finally {
            in.close();
        }
    }

    public void printFile(String fileName) {
        CompilationUnit cu;
        try {
            in = new FileInputStream(fileName);
            cu = JavaParser.parse(in);
            System.out.println(cu.toString());
        } catch (Exception e) {

        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getFileAsString() {
        return compilationUnit.toString();
    }

    public List<Node> getClassDescription() {
        return compilationUnit.describeMethods();
    }

    public static void main(String[] args) throws Exception {
        // creates an input stream for the file to be parsed
        FileInputStream in = new FileInputStream("C:/WS_NSU/amv/src/main/java/CH/ifa/draw/contrib/dnd/DNDFigures.java");


        CompilationUnit cu;
        try {
            // parse the file
            cu = JavaParser.parse(in);
            System.out.println("After Load");
        } finally {
            in.close();
        }

        // prints the resulting compilation unit to default system output
        System.out.println("Before");
        System.out.println(cu.toString());
        System.out.println("After");
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }
}
