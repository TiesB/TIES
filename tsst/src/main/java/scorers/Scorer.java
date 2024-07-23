package scorers;

import java.util.Collection;

import models.Test;

public abstract class Scorer {
    public void apply(Test test) {
        test.setScore(calculateScore(test));
    }

    public void apply(Collection<Test> tests) {
        for (var test : tests) {
            apply(test);
        }
    }

    abstract double calculateScore(Test test);
}
