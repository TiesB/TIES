package analyzer.visitors;

import analyzer.AbstractVoidVisitorAdapter;
import analyzer.Config;
import analyzer.collectors.Collector;
import analyzer.data.FunctionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;

public class FunctionDefVisitor extends AbstractVoidVisitorAdapter<Collector> {

    Pattern functionDefPattern = Pattern
            .compile("^(?<name>\\w+) \"(?<description>.*)\" (?<userValue>\\d+) (?<stability>\\d+)$");

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Collector collector) {
        String className = declaration.getNameAsString();
        List<FunctionData> functions = new ArrayList<>();

        Optional<Javadoc> javadoc = declaration.getJavadoc();
        if (javadoc.isPresent()) {
            for (JavadocBlockTag blockTag : javadoc.get().getBlockTags()) {
                if (blockTag.getTagName().equals(Config.FUNCTION_DEFINITION_TAG)) {
                    String content = blockTag.getContent().toText();
                    Matcher matcher = functionDefPattern.matcher(content);
                    if (matcher.matches()) {
                        functions.add(new FunctionalData(matcher.group("name"), matcher.group("description"),
                                Integer.parseInt(matcher.group("userValue")),
                                Integer.parseInt(matcher.group("stability"))));
                    }
                }
            }
        } else if (Config.REPORT_MISSING_CLASS_JAVADOC) {
            collector.addWarning("Class " + className + " has no Javadoc");
        }

        collector.addFunctions(functions);

        super.visit(declaration, collector);
    }

    /**
     * Increment method and parameter count
     *
     * @param declaration
     * @param collector
     */
    @Override
    public void visit(MethodDeclaration declaration, Collector collector) {

        // for (AnnotationExpr annotation : declaration.getAnnotations()) {
        // if (annotation.getName().equals("Override"))
        // collector.incrementMetric("Overridden Methods");

        // }

        // if (declaration.toString().startsWith("public")) {
        // collector.incrementMetric("Public Methods");
        // }

        // if (declaration.toString().startsWith("private")) {
        // collector.incrementMetric("Private Methods");
        // }

        // if (declaration.toString().startsWith("protected")) {
        // collector.incrementMetric("Protected Methods");
        // }

        // if (!declaration.hasJavaDocComment()) {
        // collector.incrementMetric("Methods without Javadoc");
        // }

        // collector.incrementMetric("Methods");

        // collector.incrementMetric("Parameters", declaration.getParameters().size());

        super.visit(declaration, collector);

    }

    /**
     * Increment field count
     *
     * @param declaration
     * @param collector
     */
    @Override
    public void visit(VariableDeclarationExpr declaration, Collector collector) {

        // collector.incrementMetric("Fields", declaration.getVariables().size());

    }

}
