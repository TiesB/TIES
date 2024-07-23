package analyzer.collectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import analyzer.data.FunctionData;
import analyzer.data.MethodData;

public class Collector {

    private List<String> warnings = new ArrayList<>();
    private Map<String, MethodData> methods = new HashMap<>();
    private List<FunctionData> functions = new ArrayList<>();
    private List<String> unresolvedExpressions = new ArrayList<>();

    @Nullable()
    private Map<String, Integer> frequencyMap;

    public List<String> getWarnings() {
        return warnings;
    }

    public Map<String, MethodData> getMethods() {
        return methods;
    }

    public List<FunctionData> getFunctions() {
        return functions;
    }

    public Map<String, Integer> getFrequencyMap() {
        return frequencyMap;
    }

    public void addMethod(MethodData methodData) {
        if (methods.containsKey(methodData.getName())) {
            System.out.println(methodData.getName() + " already exists. Equals: "
                    + methods.get(methodData.getName()).equals(methodData));
            System.out.println(methods.get(methodData.getName()) + "\nvs\n" + methodData);
        }
        methods.put(methodData.getName(), methodData);
    }

    public void addFunctions(List<FunctionData> functionsData) {
        functions.addAll(functionsData);
    }

    public void registerDependency(String dependent, String dependency) {
        if (dependency.equals(dependent)) {
            return;
        }

        if (methods.containsKey(dependent)) {
            methods.get(dependent).addDependency(dependency);
        } else {
            MethodData method = new MethodData(dependent, new ArrayList<>(), 0);
            method.addDependency(dependency);
            methods.put(dependent, method);
        }

        if (methods.containsKey(dependency)) {
            methods.get(dependency).addDependent(dependent);
        }
    }

    public void incCyclomaticComplexity(String methodName) throws NoSuchMethodException {
        incCyclomaticComplexity(methodName, 1);
    }

    public void incCyclomaticComplexity(String methodName, int amount) throws NoSuchMethodException {

        if (methods.containsKey(methodName)) {
            methods.get(methodName).incCyclomaticComplexity(amount);
        } else {
            throw new NoSuchMethodException(methodName);
            // System.err.println("AIIIII: " + methodName);
        }
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public void addUnresolvedExpression(String expression) {
        this.unresolvedExpressions.add(expression);
    }

    public void setFrequencyMap(Map<String, Integer> frequencyMap) {
        this.frequencyMap = frequencyMap;
    }

    public List<String> getUnresolvedExpressions() {
        return unresolvedExpressions;
    }
}
