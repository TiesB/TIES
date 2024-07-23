package analyzer.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MethodData {
    private final String name;
    private final List<String> functions;
    private final Set<String> dependencies = new HashSet<>();
    private final Set<String> dependents = new HashSet<>();
    private final int length;
    private int cyclomaticComplexity = 1;

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public int getCyclomaticComplexity() {
        return cyclomaticComplexity;
    }

    public MethodData(String name, List<String> functions, int length) {
        this.name = name;
        this.functions = functions;
        this.length = length;
    }

    public void addDependency(String dependency) {
        this.dependencies.add(dependency);
    }

    public void addDependent(String dependent) {
        this.dependents.add(dependent);
    }

    public void incCyclomaticComplexity(int amount) {
        this.cyclomaticComplexity += amount;
    }

    @Override
    public String toString() {
        return "Method name: " + name + ", functions: " + functions + ", #dependencies: "
                + dependencies.size() + ", #dependents: " + dependents.size() + ", length: " + length + ", CC: "
                + cyclomaticComplexity;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MethodData) {
            MethodData other = (MethodData) obj;
            return this.getName().equals(other.getName()) && this.functions.equals(other.functions)
                    && this.dependencies.equals(other.dependencies) && this.dependents.equals(other.dependents)
                    && this.length == other.length
                    && this.cyclomaticComplexity == other.cyclomaticComplexity;
        }

        return super.equals(obj);
    }
}
