import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import {
  MatButtonToggleChange,
  MatButtonToggleModule,
} from '@angular/material/button-toggle';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import { MatTabChangeEvent, MatTabsModule } from '@angular/material/tabs';
import {
  ScalingFactors,
  TestType,
  TestsService,
} from './services/tests.service';
import { LetDirective } from './util/let.directive';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { combineLatest, distinctUntilChanged, map, startWith } from 'rxjs';

function parseInput(input: string | null, deflt = '1'): number {
  return parseFloat(input === '' || input === null ? deflt : input);
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterOutlet,
    MatButtonModule,
    MatButtonToggleModule,
    MatCardModule,
    MatCheckboxModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
    MatTabsModule,
    LetDirective,
  ],
  providers: [TestsService],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  protected TestType = TestType;

  protected defaultS = '1' as const;
  protected defaultEffortS = '0.25' as const;

  protected $type = this.testsService.getType();
  protected $useFib = this.testsService.getUseFib();
  protected $combineMethod = this.testsService.getCombineMethod();
  protected $fileName = this.testsService.getFileName();
  protected $tests = this.testsService.getTests();

  protected useFib = new FormControl(true);
  protected userValueS = new FormControl(this.defaultS, { nonNullable: true });
  protected complexityS = new FormControl(this.defaultS, { nonNullable: true });
  protected stabilityS = new FormControl(this.defaultS, { nonNullable: true });
  protected effortS = new FormControl(this.defaultEffortS, {
    nonNullable: true,
  });
  protected limitS = new FormControl('', { nonNullable: true });

  protected hideInputs = false;
  protected hideMetadata = false;

  constructor(private testsService: TestsService) {}

  ngOnInit(): void {
    combineLatest([
      this.useFib.valueChanges.pipe(startWith(true), distinctUntilChanged()),
      this.limitS.valueChanges.pipe(startWith(''), distinctUntilChanged()),
      this.userValueS.valueChanges.pipe(
        startWith(this.defaultS),
        distinctUntilChanged()
      ),
      this.complexityS.valueChanges.pipe(
        startWith(this.defaultS),
        distinctUntilChanged()
      ),
      this.stabilityS.valueChanges.pipe(
        startWith(this.defaultS),
        distinctUntilChanged()
      ),
      this.effortS.valueChanges.pipe(
        startWith(this.defaultEffortS),
        distinctUntilChanged()
      ),
    ])
      .pipe(
        map(
          ([useFib, limitS, userValueS, complexityS, stabilityS, effortS]) => ({
            useFib,
            limit: parseInput(limitS, '0'),
            userValue: parseInput(userValueS),
            complexity: parseInput(complexityS),
            stability: parseInput(stabilityS),
            effort: parseInput(effortS),
          })
        )
      )
      .subscribe((data) => {
        const { useFib, limit, ...scalingFactors } = data;
        this.testsService.setUseFib(!!useFib);
        this.testsService.setScalingFactors(scalingFactors);
        this.testsService.setLimit(limit);
      });
  }

  protected async parseFiles(event: Event) {
    const files = (event.target as HTMLInputElement)?.files;
    if (files) {
      this.testsService.parseFiles(files);
    }
  }

  protected handleTypeChange(change: MatTabChangeEvent): void {
    this.testsService.setType(
      change.index === 1 ? TestType.FUNCTIONAL : TestType.UNIT
    );
  }

  protected toggleHideInputs(): void {
    this.hideInputs = !this.hideInputs;
  }

  protected toggleHideMetadata(): void {
    this.hideMetadata = !this.hideMetadata;
  }

  protected setCombineMethod(change: MatSelectChange) {
    this.testsService.setCombineMethod(change.value);
  }

  protected parseScore(score: number): string {
    return score.toFixed(2);
  }
}
