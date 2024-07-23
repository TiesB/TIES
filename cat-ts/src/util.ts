import {
  ClassLikeDeclaration,
  FunctionDeclaration,
  FunctionLikeDeclaration,
  JSDocTag,
  Node,
  SignatureDeclaration,
  SourceFile,
  SyntaxKind,
  isAccessor,
  isClassLike,
  isConstructorDeclaration,
  isConstructorTypeNode,
  isFunctionDeclaration,
  isGetAccessor,
  isMethodDeclaration,
  isSetAccessor,
} from "typescript";
import { FunctionData } from "./data";
import { config } from "./config";

export function stripPath(input: string): string {
  if (!config.STRIP_PATH) {
    return input;
  }

  return input.replace(config.PATH, "");
}

export function signatureToFullName(
  declaration?: SignatureDeclaration
): string {
  if (declaration === undefined) {
    throw new Error();
  }

  let res = "";

  const klass = getClassLikeDeclaration(declaration);
  // console.log(klass);

  if (klass) {
    res += klass.name?.escapedText + "_";
  }

  const name = isConstructorDeclaration(declaration)
    ? "_constructor"
    : isGetAccessor(declaration)
    ? // @ts-ignore
      declaration.name?.escapedText + "__get"
    : isSetAccessor(declaration)
    ? // @ts-ignore
      declaration.name?.escapedText + "__set"
    : // @ts-ignore
      declaration.name?.escapedText;

  res += name;

  // @ts-ignore
  // if (name === undefined) {
  //   console.log('Undefined:', declaration);
  // }

  return sourceFileAndNameToFullName(declaration.getSourceFile(), res);
}

export class NotInPathError extends Error {
  constructor(fileName: string) {
    super(`"${fileName}" is not in path.`);
  }
}

export function sourceFileAndNameToFullName(
  sourceFile: SourceFile,
  methodName: string
): string {
  const fileName = sourceFile.fileName;

  if (!fileName.startsWith(config.PATH)) {
    throw new NotInPathError(fileName);
  }

  return fileNameAndNameToFullName(fileName, methodName);
}

function fileNameAndNameToFullName(
  fileName: string,
  methodName: string
): string {
  if (!fileName || !methodName) {
    throw new Error(
      `FileName (${fileName}) and/or methodName (${methodName}) is falsy.`
    );
  }
  return `${fileName}_${methodName}`;
}

export function functionLength(node: FunctionLikeDeclaration): number {
  const source = node.body ?? node;
  const { line: startLine } = source
    .getSourceFile()
    .getLineAndCharacterOfPosition(source.getStart());
  const { line: endLine } = source
    .getSourceFile()
    .getLineAndCharacterOfPosition(source.getEnd());
  return endLine - startLine + 1;
}

export type ParsedFunctions = {
  functions: Set<string>;
  newFunctions: Set<FunctionData>;
};

const functionPattern =
  /"(?<description>.*)" (?<userValue>\d+) (?<stability>\d+)/;
const functionDefPattern =
  /(?<name>\w+) "(?<description>.*)" (?<userValue>\d+) (?<stability>\d+)/;

function parseJsDocTags(tags: readonly JSDocTag[]): ParsedFunctions {
  const functions = new Set<string>();
  const newFunctions = new Set<FunctionData>();

  for (const tag of tags) {
    const tagName = tag.tagName.escapedText;
    const value = tag.comment;

    if (tagName === "function") {
      if (typeof value !== "string") {
        throw new Error();
      }

      const matched = functionPattern.exec(value);
      if (
        matched === null ||
        !matched.groups ||
        !matched.groups.description ||
        !matched.groups.userValue ||
        !matched.groups.stability
      ) {
        if (value.split(" ").length != 1) {
          throw new Error(`Invalid jsDocTag value ${value}`);
        }

        functions.add(value);
        continue;
      }

      const { description, userValue, stability } = matched!.groups!;

      const name = description.split(" ").join("_");

      functions.add(name);
      newFunctions.add({
        name,
        description,
        userValue: parseInt(userValue),
        stability: parseInt(stability),
      });
    } else if (tagName === "functionDef") {
      if (typeof value !== "string") {
        throw new Error();
      }

      const matched = functionDefPattern.exec(value);
      if (
        matched === null ||
        !matched.groups ||
        !matched.groups.name ||
        !matched.groups.description ||
        !matched.groups.userValue ||
        !matched.groups.stability
      ) {
        throw new Error(`Invalid jsDocTag value ${value}`);
      }

      const { name, description, userValue, stability } = matched!.groups!;

      newFunctions.add({
        name,
        description,
        userValue: parseInt(userValue),
        stability: parseInt(stability),
      });
    }
  }

  return { functions, newFunctions };
}

export function parseMethodJsDocTags(
  tags: readonly JSDocTag[]
): ParsedFunctions {
  return parseJsDocTags(tags);
}

export function parseClassJsDocTags(
  tags: readonly JSDocTag[]
): ParsedFunctions {
  const parsed = parseJsDocTags(tags);

  if (parsed.functions.size > 0) {
    throw Error("Class should not use @function tag.");
  }

  return parsed;
}

export function isFunctionLikeDeclaration(
  node: Node
): node is FunctionLikeDeclaration {
  return (
    isFunctionDeclaration(node) ||
    isMethodDeclaration(node) ||
    isConstructorDeclaration(node) ||
    isAccessor(node)
  );
}

export function getFunctionLikeDeclaration(
  node: Node
): FunctionLikeDeclaration | undefined {
  if (!node) {
    return undefined;
  }

  if (isFunctionLikeDeclaration(node)) {
    return node;
  }

  return getFunctionLikeDeclaration(node.parent);
}

export function getClassLikeDeclaration(
  node: Node
): ClassLikeDeclaration | undefined {
  if (!node) {
    return undefined;
  }

  if (isClassLike(node)) {
    return node;
  }

  return getClassLikeDeclaration(node.parent);
}
