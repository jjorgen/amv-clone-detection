package com.github.javaparser.extend;

import com.github.javaparser.utils.CompilationUnitWrapperOrig;
import org.junit.Before;
import org.junit.Test;

public class CompilationUnitWrapperOrigTest {

    CompilationUnitWrapperOrig compilationUnitWrapperOrig;

    private String FILE_TO_PRINT = "C:/WS_NSU/amv/src/main/java/CH/ifa/draw/contrib/dnd/DNDFigures.java";

    @Before
    public void setUp() throws Exception {
        compilationUnitWrapperOrig = new CompilationUnitWrapperOrig(FILE_TO_PRINT);
    }

    @Test
    public void getFileAsStringTest() throws Exception {
        String fileAsString = compilationUnitWrapperOrig.getFileAsString();
        System.out.println(fileAsString);
    }
}
