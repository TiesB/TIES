package analyzer.visitors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.WhileStmt;

import analyzer.AbstractVoidVisitorAdapter;
import analyzer.collectors.Collector;
import analyzer.util.Util;

// https://www.jarchitect.com/Metrics#CC
public class ComplexityVisitor extends AbstractVoidVisitorAdapter<Collector> {

    private String fullMethodName;

    public void visit(MethodDeclaration declaration, Collector collector) {
        fullMethodName = Util.methodName(packageName, className, declaration.getNameAsString(),
                declaration.getParameters());

        super.visit(declaration, collector);
    }

    @Override
    public void visit(IfStmt ifStatement, Collector collector) {
        try {
            collector.incCyclomaticComplexity(fullMethodName);
        } catch (NoSuchMethodException e) {
            System.out.println("Not found: " + e.getMessage());
        } finally {
            super.visit(ifStatement, collector);
        }
    }

    @Override
    public void visit(WhileStmt whileStatement, Collector collector) {
        try {
            collector.incCyclomaticComplexity(fullMethodName);
        } catch (NoSuchMethodException e) {
            System.out.println("Not found: " + e.getMessage());
        } finally {
            super.visit(whileStatement, collector);
        }
    }

    @Override
    public void visit(ForStmt forStatement, Collector collector) {
        try {
            collector.incCyclomaticComplexity(fullMethodName);
        } catch (NoSuchMethodException e) {
            System.out.println("Not found: " + e.getMessage());
        } finally {
            super.visit(forStatement, collector);
        }
    }

    @Override
    public void visit(ForEachStmt forEachStatement, Collector collector) {
        try {
            collector.incCyclomaticComplexity(fullMethodName);
        } catch (NoSuchMethodException e) {
            System.out.println("Not found: " + e.getMessage());
        } finally {
            super.visit(forEachStatement, collector);
        }
    }

    @Override
    public void visit(SwitchEntry switchEntry, Collector collector) {
        try {
            collector.incCyclomaticComplexity(fullMethodName, switchEntry.getStatements().size());
        } catch (NoSuchMethodException e) {
            System.out.println("Not found: " + e.getMessage());
        } finally {
            super.visit(switchEntry, collector);
        }
    }

    @Override
    public void visit(ContinueStmt continueStatement, Collector collector) {
        try {
            collector.incCyclomaticComplexity(fullMethodName);
        } catch (NoSuchMethodException e) {
            System.out.println("Not found: " + e.getMessage());
        } finally {
            super.visit(continueStatement, collector);
        }
    }

    @Override
    public void visit(BinaryExpr binaryExpr, Collector collector) {
        try {
            switch (binaryExpr.getOperator()) {
                case AND:
                case OR:
                    collector.incCyclomaticComplexity(fullMethodName);
                    break;
                default:
                    break;
            }

        } catch (NoSuchMethodException e) {
            System.out.println("Not found: " + e.getMessage());
        } finally {
            super.visit(binaryExpr, collector);
        }
    }

    @Override
    public void visit(CatchClause catchClause, Collector collector) {
        try {
            collector.incCyclomaticComplexity(fullMethodName);
        } catch (NoSuchMethodException e) {
            System.out.println("Not found: " + e.getMessage());
        } finally {
            super.visit(catchClause, collector);
        }
    }

    @Override
    public void visit(ConditionalExpr conditionalExpr, Collector collector) {
        try {
            collector.incCyclomaticComplexity(fullMethodName);
        } catch (NoSuchMethodException e) {
            System.out.println("Not found: " + e.getMessage());
        } finally {
            super.visit(conditionalExpr, collector);
        }
    }
}
