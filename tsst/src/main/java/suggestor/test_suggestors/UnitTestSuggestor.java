package suggestor.test_suggestors;

import java.util.ArrayList;
import java.util.List;

import models.Data;
import models.Test;
import models.Test.TestType;

public class UnitTestSuggestor implements TestSuggestor {
    @Override
    public List<Test> suggestTests(Data data) {
        List<Test> result = new ArrayList<>();

        var functionMap = data.getFunctionMap();

        for (var method : data.methods) {
            double userValue = 0;
            double fibUserValue = 0;
            double maxUserValue = 0;
            double stabilitySum = 0;
            var stabilityN = 0;

            for (var functionName : data.getAllFunctions(method.getName())) {
                var function = functionMap.get(functionName);
                if (function == null) {
                    System.out.println("Missing: " + functionName);
                    continue;
                }

                userValue += function.getUserValue();
                fibUserValue += function.getFibUserValue();
                maxUserValue = Math.max(maxUserValue, function.getFibUserValue());
                stabilitySum += function.getStability();
                stabilityN += 1;
            }

            double avgStability = 0;
            if (stabilityN >= 1) {
                avgStability = (double) stabilitySum / stabilityN;
            }

            result.add(new Test(TestType.UNIT, method.getName(), userValue, fibUserValue, maxUserValue, avgStability,
                    method.getLength()));
        }

        return result;
    }
}
