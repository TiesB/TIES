package suggestor.test_suggestors;

import java.util.List;
import java.util.stream.Collectors;

import models.Data;
import models.Test;
import models.Test.TestType;

public class FunctionalTestSuggestor implements TestSuggestor {
    @Override
    public List<Test> suggestTests(Data data) {
        return data.functions.stream().map((function) -> {
            var methods = data.getAllMethods(function.getName());
            var complexitySum = 0;
            for (var methodName : methods) {
                if (data.getMethodMap().containsKey(methodName)) {
                    var method = data.getMethodMap().get(methodName);
                    complexitySum += method.getLength();
                }
            }
            
            return new Test(TestType.FUNCTIONAL, function.getDescription(), function.getUserValue(),
                    function.getFibUserValue(), function.getFibUserValue(), function.getStability(),
                    complexitySum);
        }).collect(Collectors.toList());
    }
}