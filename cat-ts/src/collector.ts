import { FunctionData, JsonFriendlyMethod, Method, MethodData } from "./data";

export class Collector {
  private methods = new Map<string, Method>();
  private functions = new Map<string, FunctionData>();

  public addMethod(methodData: MethodData): Method {
    let method: Method;
    if (this.methods.has(methodData.name)) {
      method = this.methods.get(methodData.name)!;
      if (method.length !== null) {
        return method;
        throw new Error(
          `Method with name \"${methodData.name}\" already discovered.`
        );
      }

      method.length = methodData.length;
    } else {
      method = new Method(methodData.name, methodData.length);
    }

    this.methods.set(methodData.name, method);
    return method;
  }

  public addFunction(f: FunctionData): void {
    if (this.functions.has(f.name)) {
      throw new Error(`Function with name \"${f.name}\" already exists.`);
    }

    this.functions.set(f.name, f);
  }

  public registerDependency(
    dependentName: string,
    dependencyName: string
  ): void {
    if (dependentName === dependencyName) {
      console.log(`Recursive function: ${dependentName}`);
      return;
      // throw new Error(
      //   `$Dependent (${dependentName}) and dependency (${dependencyName}) are the same.`
      // );
    }

    let dependent: Method;
    let dependency: Method;

    if (this.methods.has(dependentName)) {
      dependent = this.methods.get(dependentName)!;
    } else {
      dependent = new Method(dependentName);
      this.methods.set(dependentName, dependent);
    }

    if (this.methods.has(dependencyName)) {
      dependency = this.methods.get(dependencyName)!;
    } else {
      dependency = new Method(dependencyName);
      this.methods.set(dependencyName, dependency);
    }

    dependent.dependencies.add(dependencyName);
    dependency.dependents.add(dependentName);
  }

  public getMethods(): JsonFriendlyMethod[] {
    return Array.from(this.methods.values()).map((method) =>
      method.jsonFriendly()
    );
  }

  public getFunctions(): FunctionData[] {
    return Array.from(this.functions.values());
  }
}
