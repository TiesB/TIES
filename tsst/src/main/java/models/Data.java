package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Data {
    public List<MethodData> methods;
    public List<FunctionData> functions;

    private Map<String, MethodData> methodMap;
    private Map<String, FunctionData> functionMap;
    private Map<String, Set<String>> methodToFunctionsMap;
    private Map<String, Set<String>> functionToMethodsMap;

    public Map<String, MethodData> getMethodMap() {
        if (methodMap != null) {
            return methodMap;
        }

        Map<String, MethodData> result = new HashMap<>();
        for (var method : this.methods) {
            result.put(method.getName(), method);
        }

        this.methodMap = result;

        return result;
    }

    public Map<String, FunctionData> getFunctionMap() {
        if (functionMap != null) {
            return functionMap;
        }

        Map<String, FunctionData> result = new HashMap<>();
        for (var function : functions) {
            result.put(function.getName(), function);
        }

        this.functionMap = result;

        return result;
    }

    public Set<String> getAllFunctions(String methodName) {
        if (methodToFunctionsMap == null) {
            methodToFunctionsMap = new HashMap<>();
        }

        if (methodToFunctionsMap.containsKey(methodName)) {
            return methodToFunctionsMap.get(methodName);
        }

        // System.out.println(method + "..." + this.getMethodMap().keySet());

        Set<String> result = new HashSet<>();

        if (this.getMethodMap().containsKey(methodName)) {
            var method = this.getMethodMap().get(methodName);

            result.addAll(method.getFunctions());

            for (var dependent : method.getDependents()) {
                result.addAll(this.getAllFunctions(dependent));
            }

            methodToFunctionsMap.put(methodName, result);
        }

        return result;
    }

    public Set<String> getAllMethods(String functionName) {
        if (functionToMethodsMap == null) {
            functionToMethodsMap = new HashMap<>();
        }

        if (functionToMethodsMap.containsKey(functionName)) {
            return functionToMethodsMap.get(functionName);
        }

        Set<String> result = new HashSet<>();

        for (var methodName : getMethodMap().keySet()) {
            if (this.getAllFunctions(methodName).contains(functionName)) {
                result.add(methodName);
            }
        }

        functionToMethodsMap.put(functionName, result);

        return result;
    }

    record JsonMethod(String name, List<FunctionData> functions) {
    }

    public List<JsonMethod> getMethodsForJson() {
        return this.methodMap.keySet().stream().map(
                methodName -> new JsonMethod(methodName, this.getAllFunctions(methodName).stream()
                        .map(functionName -> this.functionMap.get(functionName)).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    public List<String> getFunctionsForJson() {
        return new ArrayList<>(this.functionMap.keySet());
    }

    @Override
    public String toString() {
        return functions.toString();
    }
}
