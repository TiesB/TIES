package analyzer.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClassData {
    public record Modification(Date date, boolean isFix) {
    }

    private final String path;
    private final List<Modification> modifications;

    public ClassData(String path) {
        this.path = path;
        this.modifications = new ArrayList<>();
    }

    public String getPath() {
        return path;
    }

    public List<Modification> getModifications() {
        return modifications;
    }
}
