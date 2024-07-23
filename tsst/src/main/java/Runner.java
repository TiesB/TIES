import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.Data;
import models.Test;
import scorers.UvComplexityStabilityScorer;
import scorers.UvComplexityScorer;
import scorers.UvScorer;
import suggestor.test_suggestors.FunctionalTestSuggestor;
import suggestor.test_suggestors.UnitTestSuggestor;
import util.Config;
import util.JsonFileReader;
import util.JsonWriter;
import util.MarkdownWriter;

public class Runner {

    public static void main(String[] args) {
        try {
            run(Config.PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void run(String path) {
        run(new File(path));
    }
    
    private static void run(File file) {
        try {
            Data data = JsonFileReader.readJson(file);

            List<Test> tests = new ArrayList<>();
            if (Config.ENABLE_FUNCTIONAL_TESTS) {
                tests.addAll(new FunctionalTestSuggestor().suggestTests(data));
            }
            tests.addAll(new UnitTestSuggestor().suggestTests(data));

            Collections.shuffle(tests);

            JsonWriter.outputJson(data, tests);

            new UvScorer().apply(tests);
            MarkdownWriter.writeToFile(tests, "u_sorted_tests.md", true);

            new UvComplexityScorer().apply(tests);
            MarkdownWriter.writeToFile(tests, "uc_sorted_tests.md", true);

            new UvComplexityStabilityScorer().apply(tests);
            MarkdownWriter.writeToFile(tests, "ucm_sorted_tests.md", true);
            // MarkdownWriter.writeToFile(tests, "tests.md", false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
