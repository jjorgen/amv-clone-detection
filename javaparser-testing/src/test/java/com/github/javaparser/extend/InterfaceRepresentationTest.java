package com.github.javaparser.extend;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by jorgej2 on 5/21/2018.
 */
public class InterfaceRepresentationTest {

    private static final String INTERFACE_REPRESENTATION_WRAPPER_PATH =
            "C:\\work\\0_NSU\\CH\\ifa\\draw\\contrib\\html\\DisposableResourceManager.java";

    private InterfaceRepresentationWrapper interfaceRepresentationWrapper;

    @Before
    public void setUp() throws Exception {
        interfaceRepresentationWrapper = new InterfaceRepresentationWrapper();
    }

    @Test
    public void name() throws Exception {
    }

    @Test
    public void getInterfaceNameTest() throws Exception {
        InterfaceRepresentation interfaceRepresentation =
                interfaceRepresentationWrapper.getInterfaceRepresentation(INTERFACE_REPRESENTATION_WRAPPER_PATH);
        String interfaceName = interfaceRepresentation.getInterfaceName();
        assertNotNull(interfaceName);
    }
}
