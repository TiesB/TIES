import { CombineMethod, ScalingFactors } from '../../services/tests.service';
import { Test } from '../test';
import { Scorer } from './scorer';

export class UvComplexityStabilityScorer extends Scorer {
  override score(
    test: Test,
    useFib: boolean,
    combineMethod: CombineMethod,
    scalingFactors: ScalingFactors
  ): number {
    const userValueToUse = Math.max(
      combineMethod === CombineMethod.SUM
        ? useFib && test.testType === 'UNIT'
          ? test.fibUserValue
          : test.userValue
        : test.maxUserValue,
      1
    );
    const calculatedUserValue = Math.pow(
      userValueToUse,
      scalingFactors.userValue
    );

    const calculatedComplexity = Math.pow(
      Math.max(test.complexity, 1),
      scalingFactors.complexity
    );

    const stabilityToUse = test.stability === 0 ? 1 : test.stability;
    const calculatedStability = Math.pow(
      stabilityToUse / 5,
      scalingFactors.stability
    );

    const effortToUse = test.testType === 'UNIT' ? test.complexity : 1;
    const calculatedEffort = Math.pow(effortToUse, scalingFactors.effort);

    const score =
      (calculatedUserValue * calculatedComplexity * calculatedStability) /
      (calculatedEffort !== 0 ? calculatedEffort : 1);

    return score;
  }
}
