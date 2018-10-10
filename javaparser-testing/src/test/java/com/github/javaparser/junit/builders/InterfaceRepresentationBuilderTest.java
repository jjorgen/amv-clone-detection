package com.github.javaparser.junit.builders;

import com.github.javaparser.extend.InterfaceRepresentation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.github.javaparser.utils.Utils.EOL;
import static org.junit.Assert.assertEquals;

/**
 * Created by jorgej2 on 5/19/2018.
 */
public class InterfaceRepresentationBuilderTest {

    InterfaceRepresentation interfaceRepresentation;

    @Before
    public void setUp() throws Exception {
        interfaceRepresentation = new InterfaceRepresentation();
    }

    @After
    public void tearDown() throws Exception {
        interfaceRepresentation = null;
    }

    @Test
    public void testAddImport() {
        interfaceRepresentation.addImport(Map.class);
        interfaceRepresentation.addImport(Map.class);
        interfaceRepresentation.addImport(List.class);
        assertEquals(2, interfaceRepresentation.getImports().size());
        interfaceRepresentation.addImport("myImport");
        assertEquals(3, interfaceRepresentation.getImports().size());
        assertEquals("import " + Map.class.getName() + ";" + EOL, interfaceRepresentation.getImports().get(0).toString());
        assertEquals("import " + List.class.getName() + ";" + EOL, interfaceRepresentation.getImports().get(1).toString());
        assertEquals("import myImport;" + EOL, interfaceRepresentation.getImports().get(2).toString());
    }
}
