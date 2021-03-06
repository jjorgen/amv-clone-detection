/*
 * Copyright (C) 2007-2010 Júlio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2016 The JavaParser Team.
 *
 * This file is part of JavaParser.
 * 
 * JavaParser can be used either under the terms of
 * a) the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * b) the terms of the Apache License 
 *
 * You should have received a copy of both licenses in LICENCE.LGPL and
 * LICENCE.APACHE. Please refer to those files for details.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 */

package com.github.javaparser.ast.visitor;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.imports.*;
import com.github.javaparser.ast.nodeTypes.NodeWithTypeArguments;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.javaparser.utils.PositionUtils.sortByBeginPosition;
import static com.github.javaparser.utils.Utils.EOL;

/**
 * Dumps the AST to formatted Java source code.
 *
 * @author Julio Vilmar Gesser
 */
public class DumpVisitor implements VoidVisitor<Object> {

	private boolean printComments;

	public DumpVisitor() {
		this(true);
	}

	public DumpVisitor(boolean printComments) {
		this.printComments = printComments;
	}

	public static class SourcePrinter {

		private final String indentation;

		public SourcePrinter(final String indentation) {
			this.indentation = indentation;
		}

		private int level = 0;

		private boolean indented = false;

		private final StringBuilder buf = new StringBuilder();

		public void indent() {
			level++;
		}

		public void unindent() {
			level--;
		}

		private void makeIndent() {
			for (int i = 0; i < level; i++) {
				buf.append(indentation);
			}
		}

		public void print(final String arg) {
			if (!indented) {
				makeIndent();
				indented = true;
			}
			buf.append(arg);
		}

		public void printLn(final String arg) {
			print(arg);
			printLn();
		}

		public void printLn() {
			buf.append(EOL);
			indented = false;
		}

		public String getSource() {
			return buf.toString();
		}

		@Override
		public String toString() {
			return getSource();
		}
	}

	private final SourcePrinter printer = createSourcePrinter();

	protected SourcePrinter createSourcePrinter() {
		return new SourcePrinter("    ");
	}

	public String getSource() {
		return printer.getSource();
	}

	private void printModifiers(final EnumSet<Modifier> modifiers) {
		if (modifiers.size() > 0)
			printer.print(modifiers.stream().map(Modifier::getLib).collect(Collectors.joining(" ")) + " ");
	}

	private void printMembers(final NodeList<BodyDeclaration<?>> members, final Object arg) {
		for (final BodyDeclaration<?> member : members) {
			printer.printLn();
			member.accept(this, arg);
			printer.printLn();
		}
	}

	private void printMemberAnnotations(final NodeList<AnnotationExpr> annotations, final Object arg) {
		if(annotations.isEmpty()){
			return;
		}
		for (final AnnotationExpr a : annotations) {
			a.accept(this, arg);
			printer.printLn();
		}
	}

	private void printAnnotations(final NodeList<AnnotationExpr> annotations, boolean prefixWithASpace, final Object arg) {
		if(annotations.isEmpty()){
			return;
		}
		if (prefixWithASpace) {
			printer.print(" ");
		}
		for (AnnotationExpr annotation : annotations) {
			annotation.accept(this, arg);
			printer.print(" ");
		}
	}

	private void printTypeArgs(final NodeWithTypeArguments<?> nodeWithTypeArguments, final Object arg) {
		Optional<NodeList<Type<?>>> optionalTypeArguments = nodeWithTypeArguments.getTypeArguments();
		optionalTypeArguments.ifPresent( typeArguments -> {
			printer.print("<");
			for (final Iterator<Type<?>> i = typeArguments.iterator(); i.hasNext(); ) {
				final Type<?> t = i.next();
				t.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
			printer.print(">");
		});
	}

	private void printTypeParameters(final NodeList<TypeParameter> args, final Object arg) {
        args.ifNotEmpty(tp -> {
            printer.print("<");
            for (final Iterator<TypeParameter> i = tp.iterator(); i.hasNext(); ) {
                final TypeParameter t = i.next();
                t.accept(this, arg);
                if (i.hasNext()) {
                    printer.print(", ");
                }
            }
            printer.print(">");
        });
	}

	private void printArguments(final NodeList<Expression> args, final Object arg) {
		printer.print("(");
        for (final Iterator<Expression> i = args.iterator(); i.hasNext(); ) {
            final Expression e = i.next();
            e.accept(this, arg);
            if (i.hasNext()) {
                printer.print(", ");
            }
        }
		printer.print(")");
	}

	private void printJavaComment(final Optional<? extends Comment> javacomment, final Object arg) {
        javacomment.ifPresent(c -> c.accept(this, arg));
	}

	@Override
	public void visit(final CompilationUnit n, final Object arg) {
		printJavaComment(n.getComment(), arg);

        n.getPackage().ifPresent(p -> p.accept(this, arg));

		n.getImports().accept(this, arg);
		if(!n.getImports().isEmpty()){
			printer.printLn();
		}

		for (final Iterator<TypeDeclaration<?>> i = n.getTypes().iterator(); i.hasNext(); ) {
			i.next().accept(this, arg);
			printer.printLn();
			if (i.hasNext()) {
				printer.printLn();
			}
		}

		printOrphanCommentsEnding(n);
	}

	@Override
	public void visit(final PackageDeclaration n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printAnnotations(n.getAnnotations(), false, arg);
		printer.print("package ");
		n.getName().accept(this, arg);
		printer.printLn(";");
		printer.printLn();

		printOrphanCommentsEnding(n);
	}

	@Override
	public void visit(final NameExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print(n.getName());

		printOrphanCommentsEnding(n);
	}

	@Override
	public void visit(final QualifiedNameExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		n.getQualifier().accept(this, arg);
		printer.print(".");
		printer.print(n.getName());

		printOrphanCommentsEnding(n);
	}

	@Override
	public void visit(final ClassOrInterfaceDeclaration n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printMemberAnnotations(n.getAnnotations(), arg);
		printModifiers(n.getModifiers());

		if (n.isInterface()) {
			printer.print("interface ");
		} else {
			printer.print("class ");
		}

		printer.print(n.getName());

		printTypeParameters(n.getTypeParameters(), arg);

        if(!n.getExtends().isEmpty()) {
            printer.print(" extends ");
            for (final Iterator<ClassOrInterfaceType> i = n.getExtends().iterator(); i.hasNext(); ) {
                final ClassOrInterfaceType c = i.next();
                c.accept(this, arg);
                if (i.hasNext()) {
                    printer.print(", ");
                }
            }
        }
        
        if(!n.getImplements().isEmpty()) {
            printer.print(" implements ");
            for (final Iterator<ClassOrInterfaceType> i = n.getImplements().iterator(); i.hasNext(); ) {
                final ClassOrInterfaceType c = i.next();
                c.accept(this, arg);
                if (i.hasNext()) {
                    printer.print(", ");
                }
            }
        }
        
		printer.printLn(" {");
		printer.indent();
        printMembers(n.getMembers(), arg);

		printOrphanCommentsEnding(n);

		printer.unindent();
		printer.print("}");
	}

	@Override
	public void visit(final EmptyTypeDeclaration n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print(";");

		printOrphanCommentsEnding(n);
	}

	@Override
	public void visit(final JavadocComment n, final Object arg) {
		printer.print("/**");
		printer.print(n.getContent());
		printer.printLn("*/");
	}

	@Override
	public void visit(final ClassOrInterfaceType n, final Object arg) {
		printJavaComment(n.getComment(), arg);

        n.getScope().ifPresent(s -> {
			s.accept(this, arg);
			printer.print(".");
		});
		for (AnnotationExpr ae : n.getAnnotations()) {
			ae.accept(this, arg);
			printer.print(" ");
		}

		printer.print(n.getName());

		if (n.isUsingDiamondOperator()) {
			printer.print("<>");
		} else {
			printTypeArgs(n, arg);
		}
	}

	@Override
	public void visit(final TypeParameter n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		for (AnnotationExpr ann : n.getAnnotations()) {
			ann.accept(this, arg);
			printer.print(" ");
		}
		printer.print(n.getName());
		n.getTypeBound().ifNotEmpty(tb -> {
			printer.print(" extends ");
			for (final Iterator<ClassOrInterfaceType> i = tb.iterator(); i.hasNext(); ) {
				final ClassOrInterfaceType c = i.next();
				c.accept(this, arg);
				if (i.hasNext()) {
					printer.print(" & ");
				}
			}
		});
	}

	@Override
	public void visit(final PrimitiveType n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printAnnotations(n.getAnnotations(), true, arg);
		switch (n.getType()) {
			case Boolean:
				printer.print("boolean");
				break;
			case Byte:
				printer.print("byte");
				break;
			case Char:
				printer.print("char");
				break;
			case Double:
				printer.print("double");
				break;
			case Float:
				printer.print("float");
				break;
			case Int:
				printer.print("int");
				break;
			case Long:
				printer.print("long");
				break;
			case Short:
				printer.print("short");
				break;
		}
	}

	@Override
    public void visit(final ArrayType n, final Object arg) {
        final List<ArrayType> arrayTypeBuffer = new LinkedList<>();
        Type type = n;
        while (type instanceof ArrayType) {
            final ArrayType arrayType = (ArrayType) type;
            arrayTypeBuffer.add(arrayType);
            type = arrayType.getComponentType();
        }

        type.accept(this, arg);
        for (ArrayType arrayType : arrayTypeBuffer) {
            printAnnotations(arrayType.getAnnotations(), true, arg);
            printer.print("[]");
        }
    }

	@Override
	public void visit(final ArrayCreationLevel n, final Object arg) {
		printAnnotations(n.getAnnotations(), true, arg);
		printer.print("[");
        n.getDimension().ifPresent(d->d.accept(this, arg));
		printer.print("]");
	}

	@Override
	public void visit(final IntersectionType n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printAnnotations(n.getAnnotations(), false, arg);
		boolean isFirst = true;
		for (ReferenceType element : n.getElements()) {
			element.accept(this, arg);
			if (isFirst) {
				isFirst = false;
			} else {
				printer.print(" & ");
			}
		}
	}

    @Override public void visit(final UnionType n, final Object arg) {
        printJavaComment(n.getComment(), arg);
		printAnnotations(n.getAnnotations(), true, arg);
        boolean isFirst = true;
        for (ReferenceType element : n.getElements()) {
            if (isFirst) {
                isFirst = false;
            } else {
                printer.print(" | ");
            }
	        element.accept(this, arg);
        }
    }


	@Override
	public void visit(final WildcardType n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printAnnotations(n.getAnnotations(), false, arg);
		printer.print("?");
        n.getExtends().ifPresent(e -> {
			printer.print(" extends ");
			e.accept(this, arg);
		});
		n.getSuper().ifPresent( s -> {
			printer.print(" super ");
			s.accept(this, arg);
		});
	}

	@Override
	public void visit(final UnknownType n, final Object arg) {
		// Nothing to dump
	}

	@Override
	public void visit(final FieldDeclaration n, final Object arg) {
		printOrphanCommentsBeforeThisChildNode(n);

		printJavaComment(n.getComment(), arg);
		printMemberAnnotations(n.getAnnotations(), arg);
		printModifiers(n.getModifiers());
		n.getElementType().accept(this, arg);
		for(ArrayBracketPair pair: n.getArrayBracketPairsAfterElementType()){
			pair.accept(this, arg);
		}

		printer.print(" ");
		for (final Iterator<VariableDeclarator> i = n.getVariables().iterator(); i.hasNext(); ) {
			final VariableDeclarator var = i.next();
			var.accept(this, arg);
			if (i.hasNext()) {
				printer.print(", ");
			}
		}

		printer.print(";");
	}

	@Override
	public void visit(final VariableDeclarator n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		n.getId().accept(this, arg);
		n.getInit().ifPresent( i -> {
			printer.print(" = ");
			i.accept(this, arg);
		});
	}

	@Override
	public void visit(final VariableDeclaratorId n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print(n.getName());
		for(ArrayBracketPair pair: n.getArrayBracketPairsAfterId()){
			pair.accept(this, arg);
		}
	}

	@Override
	public void visit(final ArrayInitializerExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("{");
		n.getValues().ifNotEmpty(v -> {
			printer.print(" ");
			for (final Iterator<Expression> i = v.iterator(); i.hasNext(); ) {
				final Expression expr = i.next();
				expr.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
			printer.print(" ");
		});
		printer.print("}");
	}

	@Override
	public void visit(final VoidType n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printAnnotations(n.getAnnotations(), false, arg);
		printer.print("void");
	}

	@Override
	public void visit(final ArrayAccessExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		n.getName().accept(this, arg);
		printer.print("[");
		n.getIndex().accept(this, arg);
		printer.print("]");
	}

	@Override
	public void visit(final ArrayCreationExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("new ");
		n.getElementType().accept(this, arg);
		for (ArrayCreationLevel level : n.getLevels()) {
			level.accept(this, arg);
		}
		n.getInitializer().ifPresent(i -> {
			printer.print(" ");
			i.accept(this, arg);
		});
	}

	@Override
	public void visit(final AssignExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		n.getTarget().accept(this, arg);
		printer.print(" ");
		switch (n.getOperator()) {
			case assign:
				printer.print("=");
				break;
			case and:
				printer.print("&=");
				break;
			case or:
				printer.print("|=");
				break;
			case xor:
				printer.print("^=");
				break;
			case plus:
				printer.print("+=");
				break;
			case minus:
				printer.print("-=");
				break;
			case rem:
				printer.print("%=");
				break;
			case slash:
				printer.print("/=");
				break;
			case star:
				printer.print("*=");
				break;
			case lShift:
				printer.print("<<=");
				break;
			case rSignedShift:
				printer.print(">>=");
				break;
			case rUnsignedShift:
				printer.print(">>>=");
				break;
		}
		printer.print(" ");
		n.getValue().accept(this, arg);
	}

	@Override
	public void visit(final BinaryExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		n.getLeft().accept(this, arg);
		printer.print(" ");
		switch (n.getOperator()) {
			case or:
				printer.print("||");
				break;
			case and:
				printer.print("&&");
				break;
			case binOr:
				printer.print("|");
				break;
			case binAnd:
				printer.print("&");
				break;
			case xor:
				printer.print("^");
				break;
			case equals:
				printer.print("==");
				break;
			case notEquals:
				printer.print("!=");
				break;
			case less:
				printer.print("<");
				break;
			case greater:
				printer.print(">");
				break;
			case lessEquals:
				printer.print("<=");
				break;
			case greaterEquals:
				printer.print(">=");
				break;
			case lShift:
				printer.print("<<");
				break;
			case rSignedShift:
				printer.print(">>");
				break;
			case rUnsignedShift:
				printer.print(">>>");
				break;
			case plus:
				printer.print("+");
				break;
			case minus:
				printer.print("-");
				break;
			case times:
				printer.print("*");
				break;
			case divide:
				printer.print("/");
				break;
			case remainder:
				printer.print("%");
				break;
		}
		printer.print(" ");
		n.getRight().accept(this, arg);
	}

	@Override
	public void visit(final CastExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("(");
		n.getType().accept(this, arg);
		printer.print(") ");
		n.getExpr().accept(this, arg);
	}

	@Override
	public void visit(final ClassExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		n.getType().accept(this, arg);
		printer.print(".class");
	}

	@Override
	public void visit(final ConditionalExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		n.getCondition().accept(this, arg);
		printer.print(" ? ");
		n.getThenExpr().accept(this, arg);
		printer.print(" : ");
		n.getElseExpr().accept(this, arg);
	}

	@Override
	public void visit(final EnclosedExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("(");
        n.getInner().ifPresent(i -> i.accept(this, arg));
		printer.print(")");
	}

	@Override
	public void visit(final FieldAccessExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		n.getScope().accept(this, arg);
		printer.print(".");
		printer.print(n.getField());
	}

	@Override
	public void visit(final InstanceOfExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		n.getExpr().accept(this, arg);
		printer.print(" instanceof ");
		n.getType().accept(this, arg);
	}

	@Override
	public void visit(final CharLiteralExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("'");
		printer.print(n.getValue());
		printer.print("'");
	}

	@Override
	public void visit(final DoubleLiteralExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print(n.getValue());
	}

	@Override
	public void visit(final IntegerLiteralExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print(n.getValue());
	}

	@Override
	public void visit(final LongLiteralExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print(n.getValue());
	}

	@Override
	public void visit(final IntegerLiteralMinValueExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print(n.getValue());
	}

	@Override
	public void visit(final LongLiteralMinValueExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print(n.getValue());
	}

	@Override
	public void visit(final StringLiteralExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("\"");
		printer.print(n.getValue());
		printer.print("\"");
	}

	@Override
	public void visit(final BooleanLiteralExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print(String.valueOf(n.getValue()));
	}

	@Override
	public void visit(final NullLiteralExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("null");
	}

	@Override
	public void visit(final ThisExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		n.getClassExpr().ifPresent(ce -> {
			ce.accept(this, arg);
			printer.print(".");
		});
		printer.print("this");
	}

	@Override
	public void visit(final SuperExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		n.getClassExpr().ifPresent(ce ->{
			ce.accept(this, arg);
			printer.print(".");
		});
		printer.print("super");
	}

	@Override
	public void visit(final MethodCallExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		n.getScope().ifPresent(s -> {
			s.accept(this, arg);
			printer.print(".");
		});
		printTypeArgs(n, arg);
		printer.print(n.getName());
		printArguments(n.getArgs(), arg);
	}

	@Override
	public void visit(final ObjectCreationExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		n.getScope().ifPresent(s -> {
			s.accept(this, arg);
			printer.print(".");
		});

		printer.print("new ");

		printTypeArgs(n, arg);
        n.getTypeArguments().ifPresent(t -> printer.print(" "));

		n.getType().accept(this, arg);

		printArguments(n.getArgs(), arg);

		n.getAnonymousClassBody().ifPresent(acb -> {
			printer.printLn(" {");
			printer.indent();
			printMembers(acb, arg);
			printer.unindent();
			printer.print("}");
		});
	}

	@Override
	public void visit(final UnaryExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		switch (n.getOperator()) {
			case positive:
				printer.print("+");
				break;
			case negative:
				printer.print("-");
				break;
			case inverse:
				printer.print("~");
				break;
			case not:
				printer.print("!");
				break;
			case preIncrement:
				printer.print("++");
				break;
			case preDecrement:
				printer.print("--");
				break;
			default:
		}

		n.getExpr().accept(this, arg);

		switch (n.getOperator()) {
			case postIncrement:
				printer.print("++");
				break;
			case postDecrement:
				printer.print("--");
				break;
			default:
		}
	}

	@Override
	public void visit(final ConstructorDeclaration n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printMemberAnnotations(n.getAnnotations(), arg);
		printModifiers(n.getModifiers());

		printTypeParameters(n.getTypeParameters(), arg);
		if (n.isGeneric()) {
			printer.print(" ");
		}
		printer.print(n.getName());

		printer.print("(");
		if (!n.getParameters().isEmpty()) {
			for (final Iterator<Parameter> i = n.getParameters().iterator(); i.hasNext(); ) {
				final Parameter p = i.next();
				p.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}
		printer.print(")");

		n.getThrows().ifNotEmpty(t -> {
			printer.print(" throws ");
			for (final Iterator<ReferenceType<?>> i = t.iterator(); i.hasNext(); ) {
				final ReferenceType<?> name = i.next();
				name.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		});
		printer.print(" ");
		n.getBody().accept(this, arg);
	}

	@Override
	public void visit(final MethodDeclaration n, final Object arg) {
		if (printComments) {
			printOrphanCommentsBeforeThisChildNode(n);
			printJavaComment(n.getComment(), arg);
		}
		printMemberAnnotations(n.getAnnotations(), arg);
		printModifiers(n.getModifiers());
		if (n.isDefault()) {
			printer.print("default ");
		}
		printTypeParameters(n.getTypeParameters(), arg);
		n.getTypeParameters().ifNotEmpty( tp -> printer.print(" "));

		n.getElementType().accept(this, arg);
		for(ArrayBracketPair pair: n.getArrayBracketPairsAfterElementType()){
			pair.accept(this, arg);
		}
		printer.print(" ");
		printer.print(n.getName());

		printer.print("(");
        for (final Iterator<Parameter> i = n.getParameters().iterator(); i.hasNext(); ) {
            final Parameter p = i.next();
            p.accept(this, arg);
            if (i.hasNext()) {
                printer.print(", ");
            }
        }
		printer.print(")");

		for(ArrayBracketPair pair: n.getArrayBracketPairsAfterParameterList()){
			pair.accept(this, arg);
		}

		n.getThrows().ifNotEmpty(t -> {
			printer.print(" throws ");
			for (final Iterator<ReferenceType<?>> i = t.iterator(); i.hasNext(); ) {
				final ReferenceType name = i.next();
				name.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		});
        if (n.getBody().isPresent()) {
            printer.print(" ");
            n.getBody().get().accept(this, arg);
        } else {
            printer.print(";");
        }
    }

	@Override
	public void visit(final Parameter n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printAnnotations(n.getAnnotations(), false, arg);
		printModifiers(n.getModifiers());
		n.getElementType().accept(this, arg);
		for(ArrayBracketPair pair: n.getArrayBracketPairsAfterElementType()){
			pair.accept(this, arg);
		}
		if (n.isVarArgs()) {
			printer.print("...");
		}
		printer.print(" ");
		n.getId().accept(this, arg);
	}

	@Override
	public void visit(final ExplicitConstructorInvocationStmt n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		if (n.isThis()) {
			printTypeArgs(n, arg);
			printer.print("this");
		} else {
            n.getExpr().ifPresent(e -> {
                e.accept(this, arg);
				printer.print(".");
			});
			printTypeArgs(n, arg);
			printer.print("super");
		}
		printArguments(n.getArgs(), arg);
		printer.print(";");
	}

	@Override
	public void visit(final VariableDeclarationExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printAnnotations(n.getAnnotations(), false, arg);
		printModifiers(n.getModifiers());

		n.getElementType().accept(this, arg);
		for(ArrayBracketPair pair: n.getArrayBracketPairsAfterElementType()){
			pair.accept(this, arg);
		}
		printer.print(" ");

		for (final Iterator<VariableDeclarator> i = n.getVariables().iterator(); i.hasNext(); ) {
			final VariableDeclarator v = i.next();
			v.accept(this, arg);
			if (i.hasNext()) {
				printer.print(", ");
			}
		}
	}

	@Override
	public void visit(final TypeDeclarationStmt n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		n.getTypeDeclaration().accept(this, arg);
	}

	@Override
	public void visit(final AssertStmt n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("assert ");
		n.getCheck().accept(this, arg);
		n.getMessage().ifPresent(m -> {
			printer.print(" : ");
			m.accept(this, arg);
		});
		printer.print(";");
	}

	@Override
	public void visit(final BlockStmt n, final Object arg) {
		printOrphanCommentsBeforeThisChildNode(n);
		printJavaComment(n.getComment(), arg);
		printer.printLn("{");
        printer.indent();
        for (final Statement s : n.getStmts()) {
            s.accept(this, arg);
            printer.printLn();
        }
        printer.unindent();
		printOrphanCommentsEnding(n);
		printer.print("}");

	}

	@Override
	public void visit(final LabeledStmt n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print(n.getLabel());
		printer.print(": ");
		n.getStmt().accept(this, arg);
	}

	@Override
	public void visit(final EmptyStmt n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print(";");
	}

	@Override
	public void visit(final ExpressionStmt n, final Object arg) {
		printOrphanCommentsBeforeThisChildNode(n);
		printJavaComment(n.getComment(), arg);
		n.getExpression().accept(this, arg);
		printer.print(";");
	}

	@Override
	public void visit(final SwitchStmt n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("switch(");
		n.getSelector().accept(this, arg);
		printer.printLn(") {");
        printer.indent();
        for (final SwitchEntryStmt e : n.getEntries()) {
            e.accept(this, arg);
        }
        printer.unindent();
		printer.print("}");

	}

	@Override
	public void visit(final SwitchEntryStmt n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		if (n.getLabel().isPresent()) {
			printer.print("case ");
			n.getLabel().get().accept(this, arg);
			printer.print(":");
		} else {
			printer.print("default:");
		}
		printer.printLn();
		printer.indent();
        for (final Statement s : n.getStmts()) {
            s.accept(this, arg);
            printer.printLn();
        }
		printer.unindent();
	}

	@Override
	public void visit(final BreakStmt n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("break");
		n.getId().ifPresent(id -> {
			printer.print(" ");
			printer.print(id);
		});
		printer.print(";");
	}

	@Override
	public void visit(final ReturnStmt n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("return");
		n.getExpr().ifPresent( e-> {
			printer.print(" ");
			e.accept(this, arg);
		});
		printer.print(";");
	}

	@Override
	public void visit(final EnumDeclaration n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printMemberAnnotations(n.getAnnotations(), arg);
		printModifiers(n.getModifiers());

		printer.print("enum ");
		printer.print(n.getName());

		if (!n.getImplements().isEmpty()) {
			printer.print(" implements ");
			for (final Iterator<ClassOrInterfaceType> i = n.getImplements().iterator(); i.hasNext(); ) {
				final ClassOrInterfaceType c = i.next();
				c.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}

		printer.printLn(" {");
		printer.indent();
        printer.printLn();
        for (final Iterator<EnumConstantDeclaration> i = n.getEntries().iterator(); i.hasNext(); ) {
            final EnumConstantDeclaration e = i.next();
            e.accept(this, arg);
            if (i.hasNext()) {
                printer.print(", ");
            }
        }
		if (!n.getMembers().isEmpty()) {
			printer.printLn(";");
			printMembers(n.getMembers(), arg);
		} else {
			if (!n.getEntries().isEmpty()) {
				printer.printLn();
			}
		}
		printer.unindent();
		printer.print("}");
	}

	@Override
	public void visit(final EnumConstantDeclaration n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printMemberAnnotations(n.getAnnotations(), arg);
		printer.print(n.getName());

		if (!n.getArgs().isEmpty()) {
			printArguments(n.getArgs(), arg);
		}

		if (!n.getClassBody().isEmpty()) {
			printer.printLn(" {");
			printer.indent();
			printMembers(n.getClassBody(), arg);
			printer.unindent();
			printer.printLn("}");
		}
	}

	@Override
	public void visit(final EmptyMemberDeclaration n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print(";");
	}

	@Override
	public void visit(final InitializerDeclaration n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		if (n.isStatic()) {
			printer.print("static ");
		}
		n.getBlock().accept(this, arg);
	}

	@Override
	public void visit(final IfStmt n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("if (");
		n.getCondition().accept(this, arg);
		final boolean thenBlock = n.getThenStmt() instanceof BlockStmt;
		if (thenBlock) // block statement should start on the same line
			printer.print(") ");
		else {
			printer.printLn(")");
			printer.indent();
		}
		n.getThenStmt().accept(this, arg);
		if (!thenBlock)
			printer.unindent();
        n.getElseStmt().ifPresent(es -> {
			if (thenBlock)
				printer.print(" ");
			else
				printer.printLn();
			final boolean elseIf = es instanceof IfStmt;
			final boolean elseBlock = es instanceof BlockStmt;
			if (elseIf || elseBlock) // put chained if and start of block statement on a same level
				printer.print("else ");
			else {
				printer.printLn("else");
				printer.indent();
			}
			es.accept(this, arg);
			if (!(elseIf || elseBlock))
				printer.unindent();
		});
	}

	@Override
	public void visit(final WhileStmt n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("while (");
		n.getCondition().accept(this, arg);
		printer.print(") ");
		n.getBody().accept(this, arg);
	}

	@Override
	public void visit(final ContinueStmt n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("continue");
		n.getId().ifPresent(id -> {
			printer.print(" ");
			printer.print(id);
		});
		printer.print(";");
	}

	@Override
	public void visit(final DoStmt n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("do ");
		n.getBody().accept(this, arg);
		printer.print(" while (");
		n.getCondition().accept(this, arg);
		printer.print(");");
	}

	@Override
	public void visit(final ForeachStmt n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("for (");
		n.getVariable().accept(this, arg);
		printer.print(" : ");
		n.getIterable().accept(this, arg);
		printer.print(") ");
		n.getBody().accept(this, arg);
	}

	@Override
	public void visit(final ForStmt n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("for (");
        for (final Iterator<Expression> i = n.getInit().iterator(); i.hasNext(); ) {
            final Expression e = i.next();
            e.accept(this, arg);
            if (i.hasNext()) {
                printer.print(", ");
            }
        }
		printer.print("; ");
        n.getCompare().ifPresent(c -> c.accept(this, arg));
		printer.print("; ");
        for (final Iterator<Expression> i = n.getUpdate().iterator(); i.hasNext(); ) {
            final Expression e = i.next();
            e.accept(this, arg);
            if (i.hasNext()) {
                printer.print(", ");
            }
        }
		printer.print(") ");
		n.getBody().accept(this, arg);
	}

	@Override
	public void visit(final ThrowStmt n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("throw ");
		n.getExpr().accept(this, arg);
		printer.print(";");
	}

	@Override
	public void visit(final SynchronizedStmt n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("synchronized (");
		n.getExpr().accept(this, arg);
		printer.print(") ");
		n.getBody().accept(this, arg);
	}

	@Override
	public void visit(final TryStmt n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("try ");
		if (!n.getResources().isEmpty()) {
			printer.print("(");
			Iterator<VariableDeclarationExpr> resources = n.getResources().iterator();
			boolean first = true;
			while (resources.hasNext()) {
				visit(resources.next(), arg);
				if (resources.hasNext()) {
					printer.print(";");
					printer.printLn();
					if (first) {
						printer.indent();
					}
				}
				first = false;
			}
			if (n.getResources().size() > 1) {
				printer.unindent();
			}
			printer.print(") ");
		}
		n.getTryBlock().accept(this, arg);
        for (final CatchClause c : n.getCatchs()) {
            c.accept(this, arg);
        }
		n.getFinallyBlock().ifPresent(fb -> {
			printer.print(" finally ");
			fb.accept(this, arg);
		});
	}

	@Override
	public void visit(final CatchClause n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print(" catch (");
		n.getParam().accept(this, arg);
		printer.print(") ");
		n.getBody().accept(this, arg);

	}

	@Override
	public void visit(final AnnotationDeclaration n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printMemberAnnotations(n.getAnnotations(), arg);
		printModifiers(n.getModifiers());

		printer.print("@interface ");
		printer.print(n.getName());
		printer.printLn(" {");
		printer.indent();
        printMembers(n.getMembers(), arg);
		printer.unindent();
		printer.print("}");
	}

	@Override
	public void visit(final AnnotationMemberDeclaration n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printMemberAnnotations(n.getAnnotations(), arg);
		printModifiers(n.getModifiers());

		n.getType().accept(this, arg);
		printer.print(" ");
		printer.print(n.getName());
		printer.print("()");
        n.getDefaultValue().ifPresent(dv -> {
			printer.print(" default ");
			dv.accept(this, arg);
		});
		printer.print(";");
	}

	@Override
	public void visit(final MarkerAnnotationExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("@");
		n.getName().accept(this, arg);
	}

	@Override
	public void visit(final SingleMemberAnnotationExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("@");
		n.getName().accept(this, arg);
		printer.print("(");
		n.getMemberValue().accept(this, arg);
		printer.print(")");
	}

	@Override
	public void visit(final NormalAnnotationExpr n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("@");
		n.getName().accept(this, arg);
		printer.print("(");
        for (final Iterator<MemberValuePair> i = n.getPairs().iterator(); i.hasNext(); ) {
            final MemberValuePair m = i.next();
            m.accept(this, arg);
            if (i.hasNext()) {
                printer.print(", ");
            }
        }
		printer.print(")");
	}

	@Override
	public void visit(final MemberValuePair n, final Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print(n.getName());
		printer.print(" = ");
		n.getValue().accept(this, arg);
	}

	@Override
	public void visit(final LineComment n, final Object arg) {
		if (!this.printComments) {
			return;
		}
		printer.print("//");
		String tmp = n.getContent();
		tmp = tmp.replace('\r', ' ');
		tmp = tmp.replace('\n', ' ');
		printer.printLn(tmp);
	}

	@Override
	public void visit(final BlockComment n, final Object arg) {
		if (!this.printComments) {
			return;
		}
		printer.print("/*");
		printer.print(n.getContent());
		printer.printLn("*/");
	}

	@Override
	public void visit(LambdaExpr n, Object arg) {
		printJavaComment(n.getComment(), arg);

		final NodeList<Parameter> parameters = n.getParameters();
		final boolean printPar = n.isParametersEnclosed();

		if (printPar) {
			printer.print("(");
		}
		for (Iterator<Parameter> i = parameters.iterator(); i.hasNext(); ) {
			Parameter p = i.next();
			p.accept(this, arg);
			if (i.hasNext()) {
				printer.print(", ");
			}
		}
		if (printPar) {
			printer.print(")");
		}

		printer.print(" -> ");
		final Statement body = n.getBody();
		if (body instanceof ExpressionStmt) {
			// Print the expression directly
			((ExpressionStmt) body).getExpression().accept(this, arg);
		} else {
			body.accept(this, arg);
		}
	}


	@Override
	public void visit(MethodReferenceExpr n, Object arg) {
		printJavaComment(n.getComment(), arg);
        n.getScope().accept(this, arg);
		printer.print("::");
		printTypeArgs(n, arg);
        printer.print(n.getIdentifier());
	}

	@Override
	public void visit(TypeExpr n, Object arg) {
		printJavaComment(n.getComment(), arg);
        n.getType().accept(this, arg);
	}

	@Override
	public void visit(ArrayBracketPair arrayBracketPair, Object arg) {
		printAnnotations(arrayBracketPair.getAnnotations(), true, arg);
		printer.print("[]");
	}

	@Override
	public void visit(NodeList n, Object arg) {
		for(Object node: n){
            ((Node)node).accept(this, arg);
		}
	}

	@Override
	public void visit(EmptyImportDeclaration n, Object arg) {
		printer.printLn(";");
	}

	@Override
	public void visit(SingleStaticImportDeclaration n, Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("import static ");
		n.getType().accept(this, arg);
		printer.print(".");
		printer.print(n.getStaticMember());
		printer.printLn(";");
		printOrphanCommentsEnding(n);
	}

	@Override
	public void visit(SingleTypeImportDeclaration n, Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("import ");
		n.getType().accept(this, arg);
		printer.printLn(";");
		printOrphanCommentsEnding(n);
	}

	@Override
	public void visit(StaticImportOnDemandDeclaration n, Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("import static ");
		n.getType().accept(this, arg);
		printer.printLn(".*;");
		printOrphanCommentsEnding(n);
	}

	@Override
	public void visit(TypeImportOnDemandDeclaration n, Object arg) {
		printJavaComment(n.getComment(), arg);
		printer.print("import ");
		n.getName().accept(this, arg);
		printer.printLn(".*;");
		printOrphanCommentsEnding(n);
	}

	private void printOrphanCommentsBeforeThisChildNode(final Node node) {
		if (node instanceof Comment) return;

		Node parent = node.getParentNode();
		while (parent != null && parent instanceof NodeList) {
			parent = parent.getParentNode();
		}
		if (parent == null) return;
		List<Node> everything = new LinkedList<>();
		everything.addAll(parent.getBackwardsCompatibleChildrenNodes());
		sortByBeginPosition(everything);
		int positionOfTheChild = -1;
		for (int i = 0; i < everything.size(); i++) {
			if (everything.get(i) == node) positionOfTheChild = i;
		}
		if (positionOfTheChild == -1) {
			throw new AssertionError("I am not a child of my parent.");
		}
		int positionOfPreviousChild = -1;
		for (int i = positionOfTheChild - 1; i >= 0 && positionOfPreviousChild == -1; i--) {
			if (!(everything.get(i) instanceof Comment)) positionOfPreviousChild = i;
		}
		for (int i = positionOfPreviousChild + 1; i < positionOfTheChild; i++) {
			Node nodeToPrint = everything.get(i);
			if (!(nodeToPrint instanceof Comment))
				throw new RuntimeException("Expected comment, instead " + nodeToPrint.getClass() + ". Position of previous child: " + positionOfPreviousChild + ", position of child " + positionOfTheChild);
			nodeToPrint.accept(this, null);
		}
	}


	private void printOrphanCommentsEnding(final Node node) {
		List<Node> everything = new LinkedList<>();
		everything.addAll(node.getChildrenNodes());
		sortByBeginPosition(everything);
		if (everything.isEmpty()) {
			return;
		}

		int commentsAtEnd = 0;
		boolean findingComments = true;
		while (findingComments && commentsAtEnd < everything.size()) {
			Node last = everything.get(everything.size() - 1 - commentsAtEnd);
			findingComments = (last instanceof Comment);
			if (findingComments) {
				commentsAtEnd++;
			}
		}
		for (int i = 0; i < commentsAtEnd; i++) {
			everything.get(everything.size() - commentsAtEnd + i).accept(this, null);
		}
	}
}
