package analyzer.visitors;

import java.nio.file.Path;
import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.resolution.Resolvable;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodLikeDeclaration;

import analyzer.AbstractVoidVisitorAdapter;
import analyzer.Config;
import analyzer.collectors.Collector;
import analyzer.util.Util;

public class DependencyVisitor extends AbstractVoidVisitorAdapter<Collector> {
    String methodName;
    NodeList<Parameter> parameters;

    @Override
    public void visit(MethodCallExpr expr, Collector collector) {
        registerCall(expr, collector);
        super.visit(expr, collector);
    }

    @Override
    public void visit(ObjectCreationExpr expr, Collector collector) {
        registerCall(expr, collector);
        super.visit(expr, collector);
    }

    /**
     * @function "Visit a method declaration" 12
     */
    @Override
    public void visit(MethodDeclaration declaration, Collector collector) {
        methodName = declaration.getNameAsString();
        parameters = declaration.getParameters();

        super.visit(declaration, collector);
    }

    private <R extends ResolvedMethodLikeDeclaration, E extends Expression & Resolvable<R>> void registerCall(E expr,
            Collector collector) {
        {
            if (methodName == null) {
                methodName = className;
            }

            try {
                R resolvedDeclaration = expr.resolve();

                // if (expr.isDescendantOf(expr))

                // System.out.println(resolvedDeclaration);

                // Optional<Path> directory = expr.findRootNode().findCompilationUnit()
                // .flatMap(cU -> cU.getStorage().map(storage -> storage.getDirectory()));
                // resolvedDeclaration.

                if (!Util.includePackage(resolvedDeclaration.getPackageName())) {
                    // System.out.println("Not in PATH: " + resolvedDeclaration);
                    return;
                }

                // System.out.println(resolvedDeclaration.toString() + ", " + directory);

                // System.out.println("Root: " + expr.findRootNode().findCompilationUnit()
                // .map(cU -> cU.getStorage().map(storage -> "" + storage.getFileName() + ","
                // + storage.getDirectory() + "," + storage.getPath() + "," +
                // storage.getSourceRoot())));

                // for (int i = 0; i < resolvedDeclaration.getNumberOfParams(); i++) {
                // System.out.println("xxx: " + resolvedDeclaration.getParam(i) + " ____ "
                // + resolvedDeclaration.getParam(i).describeType());
                // }

                collector.registerDependency(Util.methodName(packageName, className,
                        methodName, parameters),
                        Util.methodName(resolvedDeclaration.getPackageName(),
                                resolvedDeclaration.getClassName(), resolvedDeclaration.getName()));
            } catch (UnsolvedSymbolException e) {
                // System.out.println("XXXX: " + expr.toString());
                collector.addUnresolvedExpression(expr.toString());

                // e.printStackTrace();
            } catch (Exception e) {
                System.out.println("\n============================");
                System.out.println("Expression: " + expr);
                // System.out.println(e.getMessage());
                e.printStackTrace();
                System.out.println("============================\n");
            }
        }
    }
}
