package scorers;

import models.Test;

public class UvScorer extends Scorer {
    @Override
    public double calculateScore(Test test) {
        return test.getUserValue();
    }
}
