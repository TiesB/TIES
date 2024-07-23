package models;

import util.Util;

public class FunctionData {
    private final String name;
    private final String description;
    private final int userValue;
    private final int stability;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getUserValue() {
        return userValue;
    }

    public int getFibUserValue() {
        return Util.fib(userValue + 1);
    }

    public int getStability() {
        return stability;
    }

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
