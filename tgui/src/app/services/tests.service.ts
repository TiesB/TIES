import { Injectable } from '@angular/core';
import {
  BehaviorSubject,
  Observable,
  combineLatest,
  distinctUntilChanged,
  map,
} from 'rxjs';
import { Test } from '../data/test';
import { UvComplexityStabilityScorer } from '../data/scorers/uv_complexity_stability.scorer';
import { Data } from '../data/data';
import { Method } from '../data/method';
import random from 'random';
import seedrandom from 'seedrandom';

export type ScalingFactors = {
  userValue: number;
  complexity: number;
  stability: number;
  effort: number;
};

const compareTests =
  (descending = true) =>
  (a: Test, b: Test) =>
    descending ? b.score - a.score : a.score - b.score;

function shuffleArray<T>(array: T[]): void {
  // @ts-ignore
  random.use(seedrandom(new Date().toISOString()));
  for (let i = array.length - 1; i > 0; i--) {
    const j = Math.floor(random.float() * (i + 1));
    [array[i], array[j]] = [array[j], array[i]];
  }
}

export enum TestType {
  UNIT,
  FUNCTIONAL,
}

export enum CombineMethod {
  SUM = 'sum',
  MAX = 'max',
}

@Injectable()
export class TestsService {
  private _fileName: BehaviorSubject<string> = new BehaviorSubject('');
  private _type: BehaviorSubject<TestType> = new BehaviorSubject(
    TestType.UNIT as TestType
  );
  private _useFib: BehaviorSubject<boolean> = new BehaviorSubject(true);
  private _combineMethod: BehaviorSubject<CombineMethod> = new BehaviorSubject(
    CombineMethod.SUM as CombineMethod
  );
  private _scalingFactors: BehaviorSubject<ScalingFactors> =
    new BehaviorSubject({
      userValue: 1,
      complexity: 1,
      stability: 1,
      effort: 0.25,
    });
  private _data: BehaviorSubject<Data> = new BehaviorSubject({
    tests: [],
    methods: [],
  } as Data);
  private _limit: BehaviorSubject<number> = new BehaviorSubject(0);

  private _filteredData: Observable<Data> = combineLatest([
    this._limit.asObservable(),
    this._data.asObservable(),
  ]).pipe(
    distinctUntilChanged(),
    map(([limit, data]) => {
      let unitTests = data.tests
        .filter((test) => test.testType === 'UNIT');
      let functionalTests = data.tests.filter(
        (test) => test.testType === 'FUNCTIONAL'
      );

      if (limit) {
        shuffleArray(unitTests);
        unitTests = unitTests.slice(0, limit);

        shuffleArray(functionalTests);
        functionalTests = functionalTests.slice(0, limit);
      }

      return {
        methods: data.methods,
        tests: unitTests.concat(functionalTests),
      };
    })
  );

  private scorer = new UvComplexityStabilityScorer();

  public async parseFiles(files: FileList): Promise<void> {
    const file = files[0];
    this._fileName.next(file.name);

    const parsed = JSON.parse(await file.text()) as {
      tests: object[];
      methods: object[];
    };
    const methods = parsed.methods.map((obj) =>
      Object.assign(new Method(), obj)
    );
    const tests = parsed.tests.map((obj) => {
      const test = Object.assign(new Test(), obj);

      if (test.testType === 'UNIT') {
        const method = methods.find((m) => m.name === test.title);

        if (method) {
          test.functions = method.functions;
        }
      }

      return test;
    });
    const data: Data = { tests, methods };

    this._data.next(data);
  }

  public getType(): Observable<TestType> {
    return this._type.asObservable();
  }

  public setType(type: TestType): void {
    if (type !== this._type.value) {
      this._type.next(type);
    }
  }

  public getUseFib(): Observable<boolean> {
    return this._useFib.asObservable();
  }

  public setUseFib(useFib: boolean): void {
    if (useFib !== this._useFib.value) {
      this._useFib.next(useFib);
    }
  }

  public getCombineMethod(): Observable<CombineMethod> {
    return this._combineMethod.asObservable();
  }

  public setCombineMethod(combineMethod: CombineMethod): void {
    if (combineMethod !== this._combineMethod.value) {
      this._combineMethod.next(combineMethod);
    }
  }

  public getFileName(): Observable<string> {
    return this._fileName.asObservable();
  }

  public getTests(): Observable<Test[]> {
    return combineLatest([
      this._filteredData,
      this._type,
      this._useFib,
      this._combineMethod,
      this._scalingFactors,
    ]).pipe(
      map(([data, type, useFib, combineMethod, scalingFactors]) => {
        const tests = data.tests.filter(
          (test) =>
            test.testType ===
            (type === TestType.FUNCTIONAL ? 'FUNCTIONAL' : 'UNIT')
        );

        this.scorer.scoreTests(tests, useFib, combineMethod, scalingFactors);

        shuffleArray(tests);

        tests.sort(compareTests());

        console.log('# of tests', tests.length);

        console.log(JSON.stringify(tests.map((test) => test.title)));

        return tests;
      })
    );
  }

  public setScalingFactors(scalingFactors: ScalingFactors): void {
    const currentValue = this._scalingFactors.value;
    if (
      scalingFactors.complexity !== currentValue.complexity ||
      scalingFactors.effort !== currentValue.effort ||
      scalingFactors.stability !== currentValue.stability ||
      scalingFactors.userValue !== currentValue.userValue
    ) {
      this._scalingFactors.next(scalingFactors);
    }
  }

  public setLimit(limit: number): void {
    if (limit !== this._limit.value) {
      this._limit.next(limit);
    }
  }
}
