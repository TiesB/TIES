package analyzer;

import java.nio.file.Path;

public final class Config {
    public static final String[] PATHS = {};

    public static final String[] EXCLUDED_PATH_PARTS = { "src/test" };

    public static final String[] PACKAGE_PREFIXES = {};

    public static final String MAVEN_REPOSITORY_PATH = "~/.m2/repository";

    public static final boolean REPORT_MISSING_METHOD_JAVADOC = true;
    public static final boolean REPORT_MISSING_CLASS_JAVADOC = true;

    public static final String FUNCTION_TAG = "function";
    public static final String FUNCTION_DEFINITION_TAG = "functionDef";
}
