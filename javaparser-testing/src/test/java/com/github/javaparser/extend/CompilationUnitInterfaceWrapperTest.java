package com.github.javaparser.extend;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.assertTrue;

/**
 * Created by jorgej2 on 5/19/2018.
 */
public class CompilationUnitInterfaceWrapperTest {

    private static final String COMPILATION_UNIT_INTERFACE_PATH =
            "C:/work/0_NSU/CH/ifa/draw/contrib/html/DisposableResourceManager.java";
    private CompilationUnitWrapper compilationUnitWrapper;

    @Before
    public void setUp() throws Exception {
        compilationUnitWrapper = new CompilationUnitWrapper(COMPILATION_UNIT_INTERFACE_PATH);
    }

    @Test
    public void compilation_unit_should_be_interface() throws Exception {
        boolean anInterface = compilationUnitWrapper.isInterface();
        assertTrue(COMPILATION_UNIT_INTERFACE_PATH + " is an interface", anInterface);
    }

    @Test
    public void interface_should_contain_methods_with_name() throws Exception {
        ArrayList<String> methodNames = compilationUnitWrapper.getMethodNames();
        System.out.println(methodNames);
        assertTrue("Interface should contain methods", methodNames.size() > 0);
    }

    @Test
    public void interface_contains_method_registerResource() throws Exception {
        boolean registerResource = compilationUnitWrapper.containsMethod("registerResource");
    }
}
