package com.github.javaparser.extend;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.imports.ImportDeclaration;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.utils.ClassUtils;

import java.util.Map;
import java.util.Optional;

import static com.github.javaparser.ast.expr.NameExpr.name;
import static com.github.javaparser.utils.Utils.assertNotNull;
import static com.github.javaparser.utils.Utils.none;

/**
 * Created by jorgej2 on 5/19/2018.
 */
public class InterfaceRepresentation extends Node {

    private Optional<PackageDeclaration> pakage;

    private NodeList<ImportDeclaration> imports;

    private NodeList<TypeDeclaration<?>> types;

    private String interfaceFileName;

    private Node parentNode;
    private String interfaceName;

    public InterfaceRepresentation() {
        this(Range.UNKNOWN, none(), new NodeList<>(), new NodeList<>());
    }

    public InterfaceRepresentation(Optional<PackageDeclaration> pakage, NodeList<ImportDeclaration> imports, NodeList<TypeDeclaration<?>> types) {
        this(Range.UNKNOWN, pakage, imports, types);
    }

    public InterfaceRepresentation(Range range, Optional<PackageDeclaration> pakage, NodeList<ImportDeclaration> imports,
                           NodeList<TypeDeclaration<?>> types) {
        super(range);
        setPackage(pakage);
        setImports(imports);
        setTypes(types);
    }

    public InterfaceRepresentation(String interfaceFileName) {
        this(Range.UNKNOWN, none(), new NodeList<>(), new NodeList<>());
        this.interfaceFileName = interfaceFileName;
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return null;
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
    }

    /**
     * Add an import to the list of {@link ImportDeclaration} of this compilation unit<br>
     * shorthand for {@link #addImport(String)} with clazz.getName()
     *
     * @param clazz the class to import
     * @return this, the {@link CompilationUnit}
     */
    public InterfaceRepresentation addImport(Class<?> clazz) {
        if (ClassUtils.isPrimitiveOrWrapper(clazz) || clazz.getName().startsWith("java.lang"))
            return this;
        else if (clazz.isArray() && !ClassUtils.isPrimitiveOrWrapper(clazz.getComponentType())
                && !clazz.getComponentType().getName().startsWith("java.lang"))
            return addImport(clazz.getComponentType().getName());
        return addImport(clazz.getName());
    }

    /**
     * Add an import to the list of {@link ImportDeclaration} of this compilation unit<br>
     * shorthand for {@link #addImport(String, boolean, boolean)} with name,false,false
     *
     * @param name the import name
     * @return this, the {@link CompilationUnit}
     */
    public InterfaceRepresentation addImport(String name) {
        return addImport(name, false, false);
    }

    /**
     * Add an import to the list of {@link ImportDeclaration} of this compilation unit<br>
     * <b>This method check if no import with the same name is already in the list</b>
     *
     * @param name the import name
     * @param isStatic      is it an "import static"
     * @param isAsterisk does the import end with ".*"
     * @return this, the {@link CompilationUnit}
     */
    public InterfaceRepresentation addImport(String name, boolean isStatic, boolean isAsterisk) {
        final ImportDeclaration importDeclaration = ImportDeclaration.create(Range.UNKNOWN, name(name), isStatic, isAsterisk);
        if (getImports().stream().anyMatch(i -> i.toString().equals(importDeclaration.toString())))
            return this;
        else {
            getImports().add(importDeclaration);
            importDeclaration.setParentNode(this);
            return this;
        }
    }

    /**
     * Sets or clear the package declarations of this compilation unit.
     *
     * @param pakage
     *            the pakage declaration to set or <code>null</code> to default
     *            package
     */
    public InterfaceRepresentation setPackage(Optional<PackageDeclaration> pakage) {
        this.pakage = pakage;
        setAsParentNodeOf(this.pakage);
        return this;
    }


    /**
     * Sets the list of imports of this compilation unit. The list is initially
     * <code>null</code>.
     *
     * @param imports
     *            the list of imports
     */
    public InterfaceRepresentation setImports(NodeList<ImportDeclaration> imports) {
        this.imports = assertNotNull(imports);
        setAsParentNodeOf(this.imports);
        return this;
    }

    /**
     * Sets the list of types declared in this compilation unit.
     *
     * @param types
     *            the lis of types
     */
    public InterfaceRepresentation setTypes(NodeList<TypeDeclaration<?>> types) {
        this.types = assertNotNull(types);
        setAsParentNodeOf(this.types);
        return this;
    }


    /**
     * Retrieves the list of imports declared in this compilation unit or
     * <code>null</code> if there is no import.
     *
     * @return the list of imports or <code>null</code> if there is no import
     */
    public NodeList<ImportDeclaration> getImports() {
        return imports;
    }

    public String getInterfaceName() {
        return null;
    }
}
