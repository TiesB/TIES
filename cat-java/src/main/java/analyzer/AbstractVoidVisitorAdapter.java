package analyzer;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public abstract class AbstractVoidVisitorAdapter<A> extends VoidVisitorAdapter<A> {

    protected String packageName;
    protected String className;

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, A arg) {
        packageName = declaration.resolve().getPackageName();
        className = declaration.getNameAsString();

        super.visit(declaration, arg);
    }
}
