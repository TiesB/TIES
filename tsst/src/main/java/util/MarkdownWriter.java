package util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import models.Test;
import models.Test.TestType;

public class MarkdownWriter {
    public static Map<TestType, List<Test>> groupTests(List<Test> tests) {
        return tests.stream().collect(Collectors.groupingBy(test -> test.getTestType()));
    }

    private static String getMarkdown(List<Test> tests) {
        var result = tests.stream().map(Test::toString).collect(Collectors.toList());

        Collections.shuffle(result);

        return String.join("\n", result);
    }

    private static String getSortedMarkdown(List<Test> tests) {
        var result = "";

        var groupedTests = groupTests(tests);

        if (Config.ENABLE_FUNCTIONAL_TESTS) {
            var functionalTests = groupedTests.get(TestType.FUNCTIONAL);
            Collections.sort(functionalTests, Collections.reverseOrder());
            var functionalTestStrings = functionalTests.stream().map(Test::toString)
                    .collect(Collectors.toList());
            var functionalTestsString = "# Functional tests:\n" + String.join("\n", functionalTestStrings) + "\n";
            result += functionalTestsString;
        }

        var unitTests = groupedTests.get(TestType.UNIT);
        Collections.sort(unitTests, Collections.reverseOrder());
        var unitTestStrings = unitTests.stream().map(Test::toString).collect(Collectors.toList());
        var unitTestsString = "# Unit tests:\n" + String.join("\n", unitTestStrings) + "\n";
        result += unitTestsString;

        return result;
    }

    public static void writeToFile(List<Test> tests, String fileName, boolean sorted)
            throws IOException {
        try (PrintWriter out = new PrintWriter(fileName)) {
            out.println(sorted ? getSortedMarkdown(tests) : getMarkdown(tests));
        }
    }
}
