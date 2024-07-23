package analyzer.visitors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.text.CaseUtils;

import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;

import analyzer.AbstractVoidVisitorAdapter;
import analyzer.Config;
import analyzer.collectors.Collector;
import analyzer.data.FunctionData;
import analyzer.data.MethodData;
import analyzer.util.Util;

public class MethodDataVisitor extends AbstractVoidVisitorAdapter<Collector> {

    Pattern functionDefPattern = Pattern
            .compile("^\"(?<description>.*)\" (?<userValue>\\d+) (?<stability>\\d+)$");

    /**
     * @function "Visit a method declaration" 6 9
     */
    @Override
    public void visit(MethodDeclaration declaration, Collector collector) {
        registerDeclaration(declaration, collector);
        super.visit(declaration, collector);
    }

    @Override
    public void visit(ConstructorDeclaration declaration, Collector collector) {
        registerDeclaration(declaration, collector);
        super.visit(declaration, collector);
    }

    private void registerDeclaration(CallableDeclaration<?> declaration, Collector collector) {
        if (declaration.isAbstract()) {
            return;
        }

        String methodName = declaration.getNameAsString();

        int length = 0;
        var childNodes = declaration.getChildNodes();
        if (childNodes.size() > 0) {
            var last = childNodes.get(childNodes.size() - 1);
            var begin = last.getBegin();
            var end = last.getEnd();
            if (begin.isPresent() && end.isPresent()) {
                length = end.get().line - begin.get().line + 1;
            }
        }

        List<FunctionData> newFunctions = new ArrayList<>();
        List<String> functions = new ArrayList<>();

        Optional<Javadoc> javadoc = declaration.getJavadoc();
        if (javadoc.isPresent()) {
            for (JavadocBlockTag blockTag : javadoc.get().getBlockTags()) {
                if (blockTag.getTagName().equals(Config.FUNCTION_TAG)) {
                    String content = blockTag.getContent().toText();
                    Matcher matcher = functionDefPattern.matcher(content);

                    if (matcher.matches()) {
                        String functionName = className + "_" + methodName + "_"
                                + CaseUtils.toCamelCase(matcher.group("description"), false, ' ');
                        var f = new FunctionData(functionName, matcher.group("description"),
                                Integer.parseInt(matcher.group("userValue")),
                                Integer.parseInt(matcher.group("stability")));
                        // System.out.println(f);
                        newFunctions
                                .add(f);
                        functions.add(functionName);
                    } else {
                        if (content.split(" ").length != 1) {
                            throw new RuntimeException("Invalid function definition: " + content);
                        }

                        functions.add(content);
                    }
                }
            }
        } else if (Config.REPORT_MISSING_METHOD_JAVADOC) {
            collector.addWarning("Method " + methodName + " in class " + className + " has no Javadoc");
        }

        // System.out.println("a: " + declaration.getDeclarationAsString(false, false,
        // false));
        // System.out.println("b: " + declaration.getDeclarationAsString(false, false,
        // false));
        // System.out.println("c: " + declaration.getSignature());
        // System.out.println("d: " + Util.methodName(packageName, className,
        // methodName));

        // System.out.println("e: " + declaration.getTypeParameters());

        // System.out.println();

        collector.addFunctions(newFunctions);
        collector.addMethod(
                new MethodData(
                        Util.methodName(packageName, className,
                                methodName, declaration.getParameters()),
                        functions, length));
    }
}
