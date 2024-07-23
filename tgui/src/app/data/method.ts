export class FunctionData {
  public name!: string;
  public description!: string;
  public userValue!: number;
  public stability!: number;
}

export class Method {
  public name!: string;
  public functions!: FunctionData[];
}
