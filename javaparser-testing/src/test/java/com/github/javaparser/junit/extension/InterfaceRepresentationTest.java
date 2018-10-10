package com.github.javaparser.junit.extension;

import com.github.javaparser.extend.InterfaceRepresentation;
import org.junit.After;
import org.junit.Before;

/**
 * Created by jorgej2 on 5/19/2018.
 */
public class InterfaceRepresentationTest {

    private static final String INTERFACE_FILE = "C:/work/0_NSU/CH/ifa/draw/contrib/html/DisposableResourceManager";

    @Before
    public void setUp() throws Exception {
        InterfaceRepresentation interfaceRepresentation = new InterfaceRepresentation(INTERFACE_FILE);
    }

}
