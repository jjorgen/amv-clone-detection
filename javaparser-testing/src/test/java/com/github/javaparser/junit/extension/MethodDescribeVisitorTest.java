package com.github.javaparser.junit.extension;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.List;

import static com.github.javaparser.junit.wiki_samples.removenode.GitHubTest.getCompilationUnit;

/**
 * Created by John on 1/16/2017.
 */
public class MethodDescribeVisitorTest {

    @Test
//    @Ignore
    public void describeMethodsTest() throws Exception {
        CompilationUnit compilationUnitWithThreePublicMethods = getCompilationUnitWithThreePublicMethods();
        List<Node> nodeList = compilationUnitWithThreePublicMethods.describeMethods();
    }

    public CompilationUnit getCompilationUnitWithThreePublicMethods() throws Exception{
        FileInputStream in = new FileInputStream(
                "C:\\WS_NSU\\amv\\src\\main\\java\\CH\\ifa\\draw\\contrib\\dnd\\DNDFiguresTransferable.java");
        return getCompilationUnit(in);
    }

}
