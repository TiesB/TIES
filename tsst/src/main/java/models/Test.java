package models;

public class Test implements Comparable<Test> {
    public enum TestType {
        UNIT("Unit"),
        FUNCTIONAL("Functional");

        private final String title;

        TestType(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return this.title;
        }
    }

    private final TestType testType;
    private final String title;
    private final double userValue;
    private final double stability;
    private final double complexity;

    private transient double score;

    public Test(
        TestType testType, String title, double userValue, double stability, int complexity) {
        this.testType = testType;
        this.title = title;
        this.userValue = userValue;
        this.stability = stability;
        this.complexity = complexity;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public TestType getTestType() {
        return testType;
    }

    public String getTitle() {
        return title;
    }

    public double getUserValue() {
        return userValue;
    }

    public double getStability() {
        return stability;
    }

    public double getComplexity() {
        return complexity;
    }

    public double getScore() {
        return score;
    }

    @Override
    public String toString() {
        return String.format("%s test \"%s\" (%.2f, %.2f, %.2f, %.2f)", testType, title, userValue, complexity,
                stability,
                getScore());
    }

    @Override
    public int compareTo(Test other) {
        return Double.compare(this.getScore(), other.getScore());
    }
}
