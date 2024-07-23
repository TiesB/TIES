package analyzer.util;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;

import analyzer.Config;

public final class Util {

    public static String methodName(String packageName, String className, String methodName) {
        return methodName(packageName, className, methodName, new NodeList<>());
    }

    public static String methodName(String packageName, String className, String methodName,
            NodeList<Parameter> parameters) {
        return packageName + "_" + className + "_" + methodName;
        // + "(" + String.join(",", parameters.stream()
        // .map(parameter -> parameter.getTypeAsString()).collect(Collectors.toList()))
        // + ")";
    }

    public static boolean includePackage(String packageName) {
        for (var prefix : Config.PACKAGE_PREFIXES) {
            if (packageName.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }

    public static boolean pathIsSubPath(Path root, Path subPath) {
        if (root.normalize().equals(subPath.normalize())) {
            return true;
        }

        Path parent = subPath.getParent();

        if (parent != null) {
            return pathIsSubPath(root, parent);
        }

        return false;
    }

}
