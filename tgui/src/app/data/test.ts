import { FunctionData } from './method';

export class Test {
  public testType!: 'UNIT' | 'FUNCTIONAL';
  public title!: string;
  public complexity!: number;
  public stability!: number;
  public fibUserValue!: number;
  public maxUserValue!: number;
  public userValue!: number;
  public functions?: FunctionData[];
  public score = 0;
}
