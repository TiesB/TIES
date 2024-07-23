package analyzer;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import analyzer.collectors.Collector;
import analyzer.visitors.ComplexityVisitor;
import analyzer.visitors.DependencyVisitor;
import analyzer.visitors.FunctionDefVisitor;
import analyzer.visitors.MethodDataVisitor;

public class Analyzer extends SimpleFileVisitor<Path> {

    private JavaParser javaParser;
    private Collector collector;

    public Analyzer(JavaParser javaParser, Collector collector) {
        this.javaParser = javaParser;
        this.collector = collector;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
            throws IOException {

        for (var part : Config.EXCLUDED_PATH_PARTS) {
            if (file.normalize().toString().contains(part)) {
                System.out.println("Skipping: " + file.normalize());
                return FileVisitResult.SKIP_SIBLINGS;
            }
        }

        if (isNotJava(file))
            return FileVisitResult.CONTINUE;

        // initialize compilation unit
        CompilationUnit unit = javaParser.parse(file.toFile()).getResult().get();

        // collect all the stats
        new MethodDataVisitor().visit(unit, collector);
        new FunctionDefVisitor().visit(unit, collector);
        new ComplexityVisitor().visit(unit, collector);
        new DependencyVisitor().visit(unit, collector);

        return FileVisitResult.CONTINUE;
    }

    /**
     * We only need to analyze the Java files
     */
    private boolean isNotJava(Path file) {

        return !file.toString().endsWith("java");
    }

}
