package suggestor.test_suggestors;

import java.util.List;

import models.Data;
import models.Test;

public interface TestSuggestor {
    public List<Test> suggestTests(Data data);
}
