package scorers;

import models.Test;

public class UvComplexityScorer extends Scorer {
    @Override
    public double calculateScore(Test test) {
        return Math.max(test.getUserValue(), 1) * Math.max(test.getComplexity(), 1);
    }
}
