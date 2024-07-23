package models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MethodData {
    private final String name;
    private final List<String> functions;
    private final Set<String> dependencies = new HashSet<>();
    private final Set<String> dependents = new HashSet<>();
    private final int length;

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public List<String> getFunctions() {
        return functions;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public Set<String> getDependents() {
        return dependents;
    }

    public MethodData(String name, List<String> functions, int length) {
        this.name = name;
        this.functions = functions;
        this.length = length;
    }

    public void addDependency(String dependency) {
        dependencies.add(dependency);
    }

    @Override
    public String toString() {
        return "Method name: " + name + ", functions: " + functions + ", length: " + length
                + ", dependencies: " + dependencies;
    }
}
