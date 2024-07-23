import { config } from "./config";
import { stripPath } from "./util";

export type FunctionData = {
  name: string;
  description: string;
  userValue: number;
  stability: number;
};

export type MethodData = {
  name: string;
  length: number;
};

export type JsonFriendlyMethod = {
  name: string;
  length: number | null;
  functions: string[];
  dependencies: string[];
  dependents: string[];
};

export class Method {
  public name: string;
  public functions = new Set<string>();
  public dependencies = new Set<string>();
  public dependents = new Set<string>();
  public length: number | null = null;

  constructor(name: string, length?: number) {
    this.name = name;
    if (length) {
      this.length = length;
    }
  }

  public jsonFriendly(): JsonFriendlyMethod {
    return {
      name: stripPath(this.name),
      length: this.length,
      functions: Array.from(this.functions.values()),
      dependencies: Array.from(this.dependencies.values()).map(stripPath),
      dependents: Array.from(this.dependents.values()).map(stripPath),
    };
  }
}
