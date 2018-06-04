package com.github.javaparser.ast.visitor;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.imports.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.extend.CompilationUnitWrapper;

import java.util.*;

import static com.github.javaparser.utils.PositionUtils.sortByBeginPosition;
import static com.github.javaparser.utils.Utils.assertNotNull;

public class MethodDescribeVisitor  implements VoidVisitor<Object> {

    private MethodRepresentation methodRepresentation;
    private String filePath;
    private CompilationUnitWrapper compilationUnitWrapper;

    public MethodDescribeVisitor() {
    }

    public MethodDescribeVisitor(String filePath, CompilationUnitWrapper compilationUnitWrapper) {
        assertNotNull(filePath);
        assertNotNull(compilationUnitWrapper);
        this.filePath = filePath;
        this.compilationUnitWrapper = compilationUnitWrapper;
    }

    @Override
    public void visit(CompilationUnit n, Object arg) {
        System.out.println("Visited Compilation Unit");

        for (final Iterator<TypeDeclaration<?>> i = n.getTypes().iterator(); i.hasNext(); ) {
            i.next().accept(this, arg);
            if (i.hasNext()) {
                System.out.println(i);
            }
        }
    }

    @Override
    public void visit(PackageDeclaration n, Object arg) {
        n.getName().accept(this, arg);
    }

    @Override
    public void visit(TypeParameter n, Object arg) {
        for (AnnotationExpr ann : n.getAnnotations()) {
            ann.accept(this, arg);
        }
        n.getTypeBound().ifNotEmpty(tb -> {
            for (final Iterator<ClassOrInterfaceType> i = tb.iterator(); i.hasNext(); ) {
                final ClassOrInterfaceType c = i.next();
                c.accept(this, arg);
                if (i.hasNext()) {
                }
            }
        });

    }

    @Override
    public void visit(LineComment n, Object arg) {

    }

    @Override
    public void visit(BlockComment n, Object arg) {

    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        n.getTypeParameters();
        for (final BodyDeclaration<?> member : n.getMembers()) {
            member.accept(this, arg);
        }
    }

    @Override
    public void visit(EnumDeclaration n, Object arg) {
        n.getAnnotations();

        if (!n.getImplements().isEmpty()) {
            for (final Iterator<ClassOrInterfaceType> i = n.getImplements().iterator(); i.hasNext(); ) {
                final ClassOrInterfaceType c = i.next();
                c.accept(this, arg);
                if (i.hasNext()) {
                }
            }
        }

        for (final Iterator<EnumConstantDeclaration> i = n.getEntries().iterator(); i.hasNext(); ) {
            final EnumConstantDeclaration e = i.next();
            e.accept(this, arg);
            if (i.hasNext()) {
            }
        }

        if (!n.getMembers().isEmpty()) {
        } else {
            if (!n.getEntries().isEmpty()) {
            }
        }
    }

    @Override
    public void visit(EmptyTypeDeclaration n, Object arg) {

    }

    @Override
    public void visit(EnumConstantDeclaration n, Object arg) {
        if (!n.getArgs().isEmpty()) {
            n.getArgs();
        }
    }

    @Override
    public void visit(AnnotationDeclaration n, Object arg) {
        n.getAnnotations();
        n.getModifiers();
        n.getName();
        n.getMembers();
    }

    @Override
    public void visit(AnnotationMemberDeclaration n, Object arg) {
        n.getAnnotations();
        n.getModifiers();
        n.getType().accept(this, arg);
        n.getName();
        n.getDefaultValue().ifPresent(dv -> {
            dv.accept(this, arg);
        });
    }

    @Override
    public void visit(FieldDeclaration n, Object arg) {
        n.getComment();
        n.getAnnotations();
        n.getModifiers();
        n.getElementType().accept(this, arg);
        for(ArrayBracketPair pair: n.getArrayBracketPairsAfterElementType()){
            pair.accept(this, arg);
        }

        for (final Iterator<VariableDeclarator> i = n.getVariables().iterator(); i.hasNext(); ) {
            final VariableDeclarator var = i.next();
            var.accept(this, arg);
            if (i.hasNext()) {
            }
        }
    }

    @Override
    public void visit(VariableDeclarator n, Object arg) {
        n.getId().accept(this, arg);
        n.getInit().ifPresent( i -> {
            i.accept(this, arg);
        });
    }

    @Override
    public void visit(VariableDeclaratorId n, Object arg) {
        n.getName();
        for(ArrayBracketPair pair: n.getArrayBracketPairsAfterId()){
            pair.accept(this, arg);
        }
    }

    @Override
    public void visit(ConstructorDeclaration n, Object arg) {
    }

    @Override
    public void visit(MethodDeclaration n, Object arg) {
        MethodRepresentation methodRepresentation =
                new MethodRepresentation(filePath, compilationUnitWrapper.getClassOrInterfaceName());
        methodRepresentation.setParameters(n.getParameters());
        methodRepresentation.setThrows(n.getThrows());
        methodRepresentation.setStringifiedWithoutComments(n.toStringWithoutComments());
        methodRepresentation.setMethodName(n.getName());
        setMethodRepresentation(methodRepresentation);
     }

    @Override
    public void visit(Parameter n, Object arg) {
        n.getElementType().accept(this, arg);
        for(ArrayBracketPair pair: n.getArrayBracketPairsAfterElementType()){
            pair.accept(this, arg);
        }
        if (n.isVarArgs()) {
        }
        n.getId().accept(this, arg);
    }

    @Override
    public void visit(EmptyMemberDeclaration n, Object arg) {

    }

    @Override
    public void visit(InitializerDeclaration n, Object arg) {
        if (n.isStatic()) {
        }
        n.getBlock().accept(this, arg);
    }

    @Override
    public void visit(JavadocComment n, Object arg) {

    }

    @Override
    public void visit(ClassOrInterfaceType n, Object arg) {
        n.getScope().ifPresent(s -> {
            s.accept(this, arg);
        });
        for (AnnotationExpr ae : n.getAnnotations()) {
            ae.accept(this, arg);
        }

        if (n.isUsingDiamondOperator()) {
        } else {
        }
    }

    @Override
    public void visit(PrimitiveType n, Object arg) {
        switch (n.getType()) {
            case Boolean:
                break;
            case Byte:
                break;
            case Char:
                break;
            case Double:
                break;
            case Float:
                break;
            case Int:
                break;
            case Long:
                break;
            case Short:
                break;
        }
    }

    @Override
    public void visit(ArrayType n, Object arg) {
        final List<ArrayType> arrayTypeBuffer = new LinkedList<>();
        Type type = n;
        while (type instanceof ArrayType) {
            final ArrayType arrayType = (ArrayType) type;
            arrayTypeBuffer.add(arrayType);
            type = arrayType.getComponentType();
        }

        type.accept(this, arg);
        for (ArrayType arrayType : arrayTypeBuffer) {
            arrayType.getAnnotations();
        }
    }

    @Override
    public void visit(ArrayCreationLevel n, Object arg) {
        n.getAnnotations();
        n.getDimension().ifPresent(d->d.accept(this, arg));
    }

    @Override
    public void visit(IntersectionType n, Object arg) {
        n.getAnnotations();
        boolean isFirst = true;
        for (ReferenceType element : n.getElements()) {
            element.accept(this, arg);
            if (isFirst) {
                isFirst = false;
            } else {
            }
        }
    }

    @Override
    public void visit(UnionType n, Object arg) {
        n.getAnnotations();
        boolean isFirst = true;
        for (ReferenceType element : n.getElements()) {
            if (isFirst) {
                isFirst = false;
            } else {
            }
            element.accept(this, arg);
        }
    }

    @Override
    public void visit(VoidType n, Object arg) {
        n.getAnnotations();
    }

    @Override
    public void visit(WildcardType n, Object arg) {
        n.getAnnotations();
        n.getExtends().ifPresent(e -> {
            e.accept(this, arg);
        });
        n.getSuper().ifPresent( s -> {
            s.accept(this, arg);
        });
    }

    @Override
    public void visit(UnknownType n, Object arg) {

    }

    @Override
    public void visit(ArrayAccessExpr n, Object arg) {
        n.getName().accept(this, arg);
        n.getIndex().accept(this, arg);
    }

    @Override
    public void visit(ArrayCreationExpr n, Object arg) {
        n.getElementType().accept(this, arg);
        for (ArrayCreationLevel level : n.getLevels()) {
            level.accept(this, arg);
        }
        n.getInitializer().ifPresent(i -> {
            i.accept(this, arg);
        });
    }

    @Override
    public void visit(ArrayInitializerExpr n, Object arg) {
        n.getValues().ifNotEmpty(v -> {
            for (final Iterator<Expression> i = v.iterator(); i.hasNext(); ) {
                final Expression expr = i.next();
                expr.accept(this, arg);
                if (i.hasNext()) {
                }
            }
        });
    }

    @Override
    public void visit(AssignExpr n, Object arg) {
        n.getTarget().accept(this, arg);
        switch (n.getOperator()) {
            case assign:
                break;
            case and:
                break;
            case or:
                break;
            case xor:
                break;
            case plus:
                break;
            case minus:
                break;
            case rem:
                break;
            case slash:
                break;
            case star:
                break;
            case lShift:
                break;
            case rSignedShift:
                break;
            case rUnsignedShift:
                break;
        }
        n.getValue().accept(this, arg);
    }

    @Override
    public void visit(BinaryExpr n, Object arg) {
        n.getLeft().accept(this, arg);
        switch (n.getOperator()) {
            case or:
                break;
            case and:
                break;
            case binOr:
                break;
            case binAnd:
                break;
            case xor:
                break;
            case equals:
                break;
            case notEquals:
                break;
            case less:
                break;
            case greater:
                break;
            case lessEquals:
                break;
            case greaterEquals:
                break;
            case lShift:
                break;
            case rSignedShift:
                break;
            case rUnsignedShift:
                break;
            case plus:
                break;
            case minus:
                break;
            case times:
                break;
            case divide:
                break;
            case remainder:
                break;
        }
        n.getRight().accept(this, arg);
    }

    @Override
    public void visit(CastExpr n, Object arg) {
        n.getType().accept(this, arg);
        n.getExpr().accept(this, arg);
    }

    @Override
    public void visit(ClassExpr n, Object arg) {
        n.getType().accept(this, arg);
    }

    @Override
    public void visit(ConditionalExpr n, Object arg) {
        n.getCondition().accept(this, arg);
        n.getThenExpr().accept(this, arg);
        n.getElseExpr().accept(this, arg);
    }

    @Override
    public void visit(EnclosedExpr n, Object arg) {
        n.getInner().ifPresent(i -> i.accept(this, arg));
    }

    @Override
    public void visit(FieldAccessExpr n, Object arg) {
        n.getScope().accept(this, arg);
    }

    @Override
    public void visit(InstanceOfExpr n, Object arg) {
        n.getExpr().accept(this, arg);
        n.getType().accept(this, arg);
    }

    @Override
    public void visit(StringLiteralExpr n, Object arg) {
        n.getValue();
    }

    @Override
    public void visit(IntegerLiteralExpr n, Object arg) {
        n.getValue();
    }

    @Override
    public void visit(LongLiteralExpr n, Object arg) {
        n.getValue();
    }

    @Override
    public void visit(IntegerLiteralMinValueExpr n, Object arg) {
        n.getValue();
    }

    @Override
    public void visit(LongLiteralMinValueExpr n, Object arg) {
        n.getValue();
    }

    @Override
    public void visit(CharLiteralExpr n, Object arg) {
        n.getValue();
    }

    @Override
    public void visit(DoubleLiteralExpr n, Object arg) {
        n.getValue();
    }

    @Override
    public void visit(BooleanLiteralExpr n, Object arg) {
        n.getValue();
    }

    @Override
    public void visit(NullLiteralExpr n, Object arg) {

    }

    @Override
    public void visit(MethodCallExpr n, Object arg) {
        n.getScope().ifPresent(s -> {
            s.accept(this, arg);
        });
        n.getName();
        n.getArgs();
    }

    @Override
    public void visit(NameExpr n, Object arg) {
        n.getName();
    }

    @Override
    public void visit(ObjectCreationExpr n, Object arg) {
        n.getScope().ifPresent(s -> {
            s.accept(this, arg);
        });

        n.getTypeArguments();

        n.getType().accept(this, arg);

        n.getArgs();

        n.getTypeArguments();

        n.getAnonymousClassBody().ifPresent(acb -> {
        });
    }

    @Override
    public void visit(QualifiedNameExpr n, Object arg) {
        n.getQualifier().accept(this, arg);
        n.getName();
    }

    @Override
    public void visit(ThisExpr n, Object arg) {
        n.getClassExpr().ifPresent(ce -> {
            ce.accept(this, arg);
        });
    }

    @Override
    public void visit(SuperExpr n, Object arg) {
        n.getClassExpr().ifPresent(ce ->{
            ce.accept(this, arg);
        });
    }

    @Override
    public void visit(UnaryExpr n, Object arg) {
        switch (n.getOperator()) {
            case positive:
                break;
            case negative:
                break;
            case inverse:
                break;
            case not:
                break;
            case preIncrement:
                break;
            case preDecrement:
                break;
            default:
        }

        n.getExpr().accept(this, arg);

        switch (n.getOperator()) {
            case postIncrement:
                break;
            case postDecrement:
                break;
            default:
        }
    }

    @Override
    public void visit(VariableDeclarationExpr n, Object arg) {
        n.getAnnotations();
        n.getModifiers();

        n.getElementType().accept(this, arg);
        for(ArrayBracketPair pair: n.getArrayBracketPairsAfterElementType()){
            pair.accept(this, arg);
        }
        for (final Iterator<VariableDeclarator> i = n.getVariables().iterator(); i.hasNext(); ) {
            final VariableDeclarator v = i.next();
            v.accept(this, arg);
            if (i.hasNext()) {
            }
        }
    }

    @Override
    public void visit(MarkerAnnotationExpr n, Object arg) {
        n.getName().accept(this, arg);
    }

    @Override
    public void visit(SingleMemberAnnotationExpr n, Object arg) {
        n.getName().accept(this, arg);
        n.getMemberValue().accept(this, arg);
    }

    @Override
    public void visit(NormalAnnotationExpr n, Object arg) {
        n.getName().accept(this, arg);
        for (final Iterator<MemberValuePair> i = n.getPairs().iterator(); i.hasNext(); ) {
            final MemberValuePair m = i.next();
            m.accept(this, arg);
            if (i.hasNext()) {
            }
        }
    }

    @Override
    public void visit(MemberValuePair n, Object arg) {
        n.getName();
        n.getValue().accept(this, arg);
    }

    @Override
    public void visit(ExplicitConstructorInvocationStmt n, Object arg) {
        if (n.isThis()) {
        } else {
            n.getExpr().ifPresent(e -> {
                e.accept(this, arg);
            });
        }
        n.getArgs();
    }

    @Override
    public void visit(TypeDeclarationStmt n, Object arg) {
        n.getTypeDeclaration().accept(this, arg);
    }

    @Override
    public void visit(AssertStmt n, Object arg) {
        n.getCheck().accept(this, arg);
        n.getMessage().ifPresent(m -> {
            m.accept(this, arg);
        });
    }

    @Override
    public void visit(BlockStmt n, Object arg) {
        for (final Statement s : n.getStmts()) {
            methodRepresentation.addToBody(s);
            s.accept(this, arg);
        }
    }

    @Override
    public void visit(LabeledStmt n, Object arg) {
        n.getLabel();
        n.getStmt().accept(this, arg);
    }

    @Override
    public void visit(EmptyStmt n, Object arg) {

    }

    @Override
    public void visit(ExpressionStmt n, Object arg) {
//        methodRepresentation.addToBody(n);
        n.getExpression().accept(this, arg);
    }

    @Override
    public void visit(SwitchStmt n, Object arg) {
        n.getSelector().accept(this, arg);
        for (final SwitchEntryStmt e : n.getEntries()) {
            e.accept(this, arg);
        }
    }

    @Override
    public void visit(SwitchEntryStmt n, Object arg) {
        if (n.getLabel().isPresent()) {
            n.getLabel().get().accept(this, arg);
        } else {
        }
        for (final Statement s : n.getStmts()) {
            s.accept(this, arg);
        }
    }

    @Override
    public void visit(BreakStmt n, Object arg) {
        n.getId().ifPresent(id -> {
        });
    }

    @Override
    public void visit(ReturnStmt n, Object arg) {
        n.getExpr().ifPresent( e-> {
            e.accept(this, arg);
        });
    }

    @Override
    public void visit(IfStmt n, Object arg) {
        n.getCondition().accept(this, arg);
        final boolean thenBlock = n.getThenStmt() instanceof BlockStmt;
        if (thenBlock) {
        }
        else {
        }
        n.getThenStmt().accept(this, arg);
        if (!thenBlock)
        n.getElseStmt().ifPresent(es -> {
            if (thenBlock) {

            }
            else {

            }
            final boolean elseIf = es instanceof IfStmt;
            final boolean elseBlock = es instanceof BlockStmt;
            if (elseIf || elseBlock) {
            }
            else {
            }
            es.accept(this, arg);
            if (!(elseIf || elseBlock)) {
            }
        });
    }

    @Override
    public void visit(WhileStmt n, Object arg) {
        n.getCondition().accept(this, arg);
        n.getBody().accept(this, arg);
    }

    @Override
    public void visit(ContinueStmt n, Object arg) {
        n.getId().ifPresent(id -> {
        });
    }

    @Override
    public void visit(DoStmt n, Object arg) {
        n.getBody().accept(this, arg);
        n.getCondition().accept(this, arg);
    }

    @Override
    public void visit(ForeachStmt n, Object arg) {
        n.getVariable().accept(this, arg);
        n.getIterable().accept(this, arg);
        n.getBody().accept(this, arg);
    }

    @Override
    public void visit(ForStmt n, Object arg) {
        for (final Iterator<Expression> i = n.getInit().iterator(); i.hasNext(); ) {
            final Expression e = i.next();
            e.accept(this, arg);
            if (i.hasNext()) {
            }
        }
        n.getCompare().ifPresent(c -> c.accept(this, arg));
        for (final Iterator<Expression> i = n.getUpdate().iterator(); i.hasNext(); ) {
            final Expression e = i.next();
            e.accept(this, arg);
            if (i.hasNext()) {
            }
        }
        n.getBody().accept(this, arg);
    }

    @Override
    public void visit(ThrowStmt n, Object arg) {
        n.getExpr().accept(this, arg);
    }

    @Override
    public void visit(SynchronizedStmt n, Object arg) {
        n.getExpr().accept(this, arg);
        n.getBody().accept(this, arg);
    }

    @Override
    public void visit(TryStmt n, Object arg) {
        if (!n.getResources().isEmpty()) {
            Iterator<VariableDeclarationExpr> resources = n.getResources().iterator();
            boolean first = true;
            while (resources.hasNext()) {
                visit(resources.next(), arg);
                if (resources.hasNext()) {
                    if (first) {
                    }
                }
                first = false;
            }
            if (n.getResources().size() > 1) {
            }
        }
        n.getTryBlock().accept(this, arg);
        for (final CatchClause c : n.getCatchs()) {
            c.accept(this, arg);
        }
        n.getFinallyBlock().ifPresent(fb -> {
            fb.accept(this, arg);
        });
    }

    @Override
    public void visit(CatchClause n, Object arg) {
        n.getParam().accept(this, arg);
        n.getBody().accept(this, arg);
    }

    @Override
    public void visit(LambdaExpr n, Object arg) {

        final NodeList<Parameter> parameters = n.getParameters();
        final boolean printPar = n.isParametersEnclosed();

        if (printPar) {
        }
        for (Iterator<Parameter> i = parameters.iterator(); i.hasNext(); ) {
            Parameter p = i.next();
            p.accept(this, arg);
            if (i.hasNext()) {
            }
        }
        if (printPar) {
        }

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
        n.getScope().accept(this, arg);
        n.getIdentifier();
    }

    @Override
    public void visit(TypeExpr n, Object arg) {
        n.getType().accept(this, arg);
    }

    @Override
    public void visit(ArrayBracketPair arrayBracketPair, Object arg) {
        arrayBracketPair.getAnnotations();
    }

    @Override
    public void visit(NodeList n, Object arg) {
        for(Object node: n){
            ((Node)node).accept(this, arg);
        }
    }

    @Override
    public void visit(EmptyImportDeclaration n, Object arg) {

    }

    @Override
    public void visit(SingleStaticImportDeclaration n, Object arg) {
        n.getType().accept(this, arg);
    }

    @Override
    public void visit(SingleTypeImportDeclaration n, Object arg) {
        n.getType().accept(this, arg);
    }

    @Override
    public void visit(StaticImportOnDemandDeclaration n, Object arg) {
        n.getType().accept(this, arg);
    }

    @Override
    public void visit(TypeImportOnDemandDeclaration n, Object arg) {
        n.getName().accept(this, arg);
    }

    public List<Node> getNodes() {
        return null;
    }

    public void setMethodRepresentation(MethodRepresentation methodRepresentation) {
        this.methodRepresentation = methodRepresentation;
    }

    public MethodRepresentation getMethodRepresentation() {
        return methodRepresentation;
    }
}
