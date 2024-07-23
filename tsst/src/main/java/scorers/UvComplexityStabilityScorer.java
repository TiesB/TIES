package scorers;

import models.Test;

public class UvComplexityStabilityScorer extends Scorer {
    @Override
    public double calculateScore(Test test) {
        return Math.max(test.getUserValue(), 1) * Math.max(test.getComplexity(), 1)
                * (1 / Math.max(test.getStability(), 1.0));
    }
}
