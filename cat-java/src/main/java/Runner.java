import analyzer.Analyzer;
import analyzer.Config;
import analyzer.collectors.Collector;
import analyzer.printers.WarningPrinter;
import analyzer.util.JsonWriter;
import analyzer.util.Maven;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.github.javaparser.ParserConfiguration.LanguageLevel;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.MemoryTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver.ExceptionHandlers;

public class Runner {

    public static void main(String[] args) {
        try {
            Collector collector = new Collector();

            for (var path : Config.PATHS) {
                run(path, collector);
            }

            new WarningPrinter(collector).print();

            System.out.println("Number of unresolved expressions: " + collector.getUnresolvedExpressions().size());
            System.out.println("Number of unique unresolved expressions: "
                    + new HashSet<>(collector.getUnresolvedExpressions()).size());
            System.out.println("Number of methods: " + collector.getMethods().size());
            System.out.println("Number of functions: " +
                    collector.getFunctions().size());

            JsonWriter.outputJson(collector);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void run(String pathString, Collector collector) {
        Path pathPath = Path.of(pathString);

        try {
            List<String> jarPaths = new ArrayList<>();

            try {
                var paths = Maven.getDependencyJarPaths(pathString);
                System.out.println("Maven dependency paths: " + paths);
                jarPaths.addAll(paths);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ParserConfiguration parserConfiguration = new ParserConfiguration();

            parserConfiguration.setLanguageLevel(LanguageLevel.JAVA_8);

            // Set up a minimal type solver that only looks at the classes used to run
            CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
            combinedTypeSolver.setExceptionHandler(ExceptionHandlers.IGNORE_UNSUPPORTED_AND_UNSOLVED);
            combinedTypeSolver.add(new ReflectionTypeSolver(), false);
            combinedTypeSolver.add(new MemoryTypeSolver());

            String[] extensions = { "jar" };

            Collection<File> files = FileUtils.listFiles(
                    new File(Config.MAVEN_REPOSITORY_PATH),
                    extensions,
                    true);

            for (File jarPath : files) {
                System.out.println("Maven repository path: " + jarPath);
                combinedTypeSolver.add(new JarTypeSolver(jarPath), false);
            }

            Collection<File> filesA = FileUtils.listFiles(
                    new File(pathString),
                    extensions,
                    true);

            for (File jarPath : filesA) {
                System.out.println("JarPath: " + jarPath);
                // combinedTypeSolver.add(new JarTypeSolver(jarPath));
            }

            // File file = new File(Config.PATH);
            // String[] directories = file.list(new FilenameFilter() {
            // @Override
            // public boolean accept(File current, String name) {
            // return new File(current, name).isDirectory() && name.equals("src");
            // }
            // });

            List<String> sourcePaths = null;

            try (Stream<Path> stream = Files.walk(pathPath)) {
                sourcePaths = stream.filter((p) -> {
                    return Files.isDirectory(p) && p.getFileName().endsWith("src");
                }).map(p -> p.toString() + "/main/java").toList();
            }

            for (var sourcePath : sourcePaths) {
                System.out.println("SourcePath: " + sourcePath);
                combinedTypeSolver
                        .add(new JavaParserTypeSolver(sourcePath.toString(),
                                parserConfiguration), false);
            }

            // Configure JavaParser to use type resolution
            JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
            parserConfiguration.setSymbolResolver(symbolSolver);

            JavaParser javaParser = new JavaParser(parserConfiguration);

            // StaticJavaParser.getParserConfiguration().setSymbolResolver(symbolSolver);

            Files.walkFileTree(pathPath, new Analyzer(javaParser, collector));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
