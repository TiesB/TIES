package analyzer.data;

public class FunctionData {
    private final String name;
    private final String description;
    private final int userValue;
    private final int stability;

    public FunctionData(String name, String description, int userValue, int stability) {
        this.name = name;
        this.description = description;
        this.userValue = userValue;
        this.stability = stability;
    }

    @Override
    public String toString() {
        return "Function name: " + name + " (\"" + description + "\"), userValue: " + userValue + ", stability: "
                + stability;
    }
}
