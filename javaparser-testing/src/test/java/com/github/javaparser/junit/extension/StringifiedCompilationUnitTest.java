package com.github.javaparser.junit.extension;

import com.github.javaparser.ast.CompilationUnit;
import org.junit.Test;

import java.io.FileInputStream;

import static com.github.javaparser.junit.wiki_samples.removenode.GitHubTest.getCompilationUnit;

public class StringifiedCompilationUnitTest {

    @Test
    public void stringifyDNDFiguresTest() throws Exception {
        CompilationUnit compilationUnitWithTwoPublicMethods = getCompilationUnitWithTwoPublicMethods();
        System.out.println(compilationUnitWithTwoPublicMethods.toString());
    }

    @Test
    public void stringifyDNDFiguresTransferableWithNoComments() throws Exception {
        CompilationUnit compilationUnitWithThreePublicMethods = getCompilationUnitWithThreePublicMethods();
        System.out.println(compilationUnitWithThreePublicMethods.toStringWithoutComments());
    }

    public CompilationUnit getCompilationUnitWithThreePublicMethods() throws Exception{
        FileInputStream in = new FileInputStream(
            "C:\\WS_NSU\\amv\\src\\main\\java\\CH\\ifa\\draw\\contrib\\html\\HTMLTextAreaFigure.java");
        return getCompilationUnit(in);
    }

    public CompilationUnit getCompilationUnitWithTwoPublicMethods() throws Exception{
        FileInputStream in = new FileInputStream(
                "C:\\WS_NSU\\amv\\src\\main\\java\\CH\\ifa\\draw\\contrib\\dnd\\DNDFigures.java");
        return getCompilationUnit(in);
    }
}
