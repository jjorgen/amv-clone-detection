package com.github.javaparser.extend;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.MethodRepresentation;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.DumpVisitor;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by jorgej2 on 5/20/2018.
 */
public class InterfaceRepresentationWrapperTest {

    private static final String INTERFACE_REPRESENTATION_WRAPPER_PATH =
            "C:\\work\\0_NSU\\CH\\ifa\\draw\\contrib\\html\\DisposableResourceManager.java";

    private static final String COMPILATION_UNIT_PATH = "C:/work/0_NSU/CH/ifa/draw/contrib/GraphicalCompositeFigure.java";


    private InterfaceRepresentationWrapper interfaceRepresentationWrapper;

    @Before
    public void setUp() throws Exception {
        interfaceRepresentationWrapper = new InterfaceRepresentationWrapper();
    }

    @Test
    public void getInterfaceRepresentation_returns_valid_object() throws Exception {
        InterfaceRepresentation interfaceRepresentation =
                interfaceRepresentationWrapper.getInterfaceRepresentation(INTERFACE_REPRESENTATION_WRAPPER_PATH);
        assertNotNull(interfaceRepresentation);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getInterfaceRepresentation_invalid_file_path_empty_string() throws Exception {
        InterfaceRepresentation interfaceRepresentation =
                interfaceRepresentationWrapper.getInterfaceRepresentation("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void getInterfaceRepresentation_invalid_file_path_null_path() throws Exception {
        InterfaceRepresentation interfaceRepresentation =
                interfaceRepresentationWrapper.getInterfaceRepresentation(null);
    }

    @Test
    public void createCompilationUnit_returns_valid_object() throws Exception {
        InterfaceRepresentation interfaceRepresentation =
                interfaceRepresentationWrapper.getInterfaceRepresentation(INTERFACE_REPRESENTATION_WRAPPER_PATH);
        CompilationUnit compilationUnit = interfaceRepresentationWrapper.createCompilationUnit(INTERFACE_REPRESENTATION_WRAPPER_PATH);

//        compilationUnit.

        for (final Iterator<TypeDeclaration<?>> i = compilationUnit.getTypes().iterator(); i.hasNext(); ) {
            TypeDeclaration<?> typeDeclaration = i.next();
            if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
                ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) typeDeclaration;
                if (classOrInterfaceDeclaration.isInterface()) {
                    System.out.println("This is an interface");
                } else {
                    System.out.println("This is a class");
                }
            }

            System.out.println(typeDeclaration);
        }
        DumpVisitor dumpVisitor = new DumpVisitor();
        dumpVisitor.visit(compilationUnit, null);
        System.out.println(dumpVisitor.getSource().trim());
    }

    @Test
    public void createCompilationUnitForClass() throws Exception {

        CompilationUnitWrapper compilationUnitWrapper = new CompilationUnitWrapper(COMPILATION_UNIT_PATH);
        CompilationUnit compilationUnit = compilationUnitWrapper.getCompilationUnit(COMPILATION_UNIT_PATH);
        for (final Iterator<TypeDeclaration<?>> i = compilationUnit.getTypes().iterator(); i.hasNext(); ) {
            TypeDeclaration<?> typeDeclaration = i.next();
            if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
                ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) typeDeclaration;
                if (classOrInterfaceDeclaration.isInterface()) {
                    System.out.println("This is an interface");
                } else {
                    System.out.println("This is a class");
                }
            }

            System.out.println(typeDeclaration);
        }

        List<MethodRepresentation> methodRepresentations = compilationUnitWrapper.getMethodRepresentations();
        for (MethodRepresentation methodRepresentation : methodRepresentations) {
            System.out.println("Method representation: " + methodRepresentation.getMethodName());
        }
    }

    @Test
    public void createCompilationUnitForInterface() throws Exception {

        CompilationUnitWrapper compilationUnitWrapper = new CompilationUnitWrapper(INTERFACE_REPRESENTATION_WRAPPER_PATH);
        CompilationUnit compilationUnit = compilationUnitWrapper.getCompilationUnit(INTERFACE_REPRESENTATION_WRAPPER_PATH);
        for (final Iterator<TypeDeclaration<?>> i = compilationUnit.getTypes().iterator(); i.hasNext(); ) {
            TypeDeclaration<?> typeDeclaration = i.next();
            if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
                ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) typeDeclaration;
                if (classOrInterfaceDeclaration.isInterface()) {
                    displayInterfaceInformation(compilationUnitWrapper);
                    System.out.println("This is an interface");
                } else {
                    System.out.println("This is a class");
                }
            }
        }
    }

    private void displayInterfaceInformation(CompilationUnitWrapper classOrInterfaceDeclaration) {
        List<MethodRepresentation> methodRepresentations = classOrInterfaceDeclaration.getMethodRepresentations();
        System.out.println("Full file name: " + classOrInterfaceDeclaration.getFullFileName());
        for (MethodRepresentation methodRepresentation : methodRepresentations) {
            System.out.println("Get method name: " + methodRepresentation.getMethodName());
        }
    }
}
