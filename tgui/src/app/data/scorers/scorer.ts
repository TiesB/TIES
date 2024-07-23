import { CombineMethod, ScalingFactors } from '../../services/tests.service';
import { Test } from '../test';

export abstract class Scorer {
  protected abstract score(
    test: Test,
    useFib: boolean,
    combineMethod: CombineMethod,
    scalingFactors: ScalingFactors
  ): number;

  public scoreTests(
    tests: Test[],
    useFib: boolean,
    combineMethod: CombineMethod,
    scalingFactors: ScalingFactors
  ): void {
    for (const test of tests) {
      test.score = this.score(test, useFib, combineMethod, scalingFactors);
    }
  }
}
