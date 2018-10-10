package com.github.javaparser.extend;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by jorgej2 on 5/20/2018.
 */
public class InterfaceRepresentationWrapper {

    private InterfaceRepresentation interfaceRepresentation;
    private String interfaceRepresentationWrapperPath;

    public InterfaceRepresentation getInterfaceRepresentation(String interfaceRepresentationWrapperPath) {
        validate(interfaceRepresentationWrapperPath);
        this.interfaceRepresentationWrapperPath = interfaceRepresentationWrapperPath;
        return createInterfaceRepresentation(interfaceRepresentationWrapperPath);
    }

    private InterfaceRepresentation createInterfaceRepresentation(String interfaceRepresentationWrapperPath) {
        InterfaceRepresentation interfaceRepresentation = new InterfaceRepresentation(interfaceRepresentationWrapperPath);
        return interfaceRepresentation;
    }

    private void validate(String filePath) {
        if (filePath == null || filePath.trim().length() == 0) {
            throw new IllegalArgumentException("Missing value for path: '" + filePath + "'");
        }
    }

    public CompilationUnit createCompilationUnit(String filePath) {
        CompilationUnit compilationUnit;
        try {
            FileInputStream in = new FileInputStream(filePath);
            compilationUnit = JavaParser.parse(in);
            in.close();
        } catch (IOException e) {
            throw new RuntimeException("An error occurred when creating compilation unit for File Path: " + filePath, e);
        }
        return compilationUnit;
    }

}
