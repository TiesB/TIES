import {
  Node,
  NodeFlags,
  SignatureDeclaration,
  SignatureKind,
  SymbolFlags,
  SyntaxKind,
  TypeChecker,
  forEachChild,
  getJSDocTags,
  isAccessor,
  isArrowFunction,
  isBinaryExpression,
  isBlock,
  isCallExpression,
  isCallSignatureDeclaration,
  isClassDeclaration,
  isConstructorDeclaration,
  isFunctionDeclaration,
  isFunctionLike,
  isFunctionTypeNode,
  isGetAccessor,
  isGetAccessorDeclaration,
  isMethodDeclaration,
  isNewExpression,
  isPropertyAccessExpression,
  isPropertyAssignment,
  isSetAccessor,
  isSetAccessorDeclaration,
  isShorthandPropertyAssignment,
} from "typescript";
import { Collector } from "./collector";
import {
  NotInPathError,
  functionLength,
  getFunctionLikeDeclaration,
  isFunctionLikeDeclaration,
  parseClassJsDocTags,
  parseMethodJsDocTags,
  signatureToFullName,
  sourceFileAndNameToFullName,
} from "./util";

export const visit = (typeChecker: TypeChecker, collector: Collector) =>
  function (node?: Node): void {
    if (node === undefined) {
      console.warn("Node is undefined");
      return;
    }

    try {
      if (isClassDeclaration(node)) {
        const { newFunctions } = parseClassJsDocTags(getJSDocTags(node));
        for (const newFunction of newFunctions) {
          collector.addFunction(newFunction);
        }
      } else if (isFunctionLikeDeclaration(node)) {
        const name = signatureToFullName(node);
        const length = functionLength(node);

        const method = collector.addMethod({
          name,
          length,
        });
        const { functions, newFunctions } = parseMethodJsDocTags(
          getJSDocTags(node)
        );

        for (const f of functions) {
          method.functions.add(f);
        }
        for (const newFunction of newFunctions) {
          collector.addFunction(newFunction);
        }
      } else if (isCallExpression(node) || isNewExpression(node)) {
        const signature = typeChecker.getResolvedSignature(node);

        if (signature !== undefined) {
          const callerFunctionDeclaration = getFunctionLikeDeclaration(node);

          if (callerFunctionDeclaration) {
            const calleeDeclaration = signature.getDeclaration();

            if (calleeDeclaration !== undefined) {
              if (isFunctionTypeNode(calleeDeclaration)) {
                // @ts-ignore
                if (calleeDeclaration.parent.name?.escapedText) {
                  collector.registerDependency(
                    signatureToFullName(callerFunctionDeclaration),
                    sourceFileAndNameToFullName(
                      calleeDeclaration.getSourceFile(),
                      // @ts-ignore
                      calleeDeclaration.parent.name?.escapedText
                    )
                  );
                } else {
                  console.warn(
                    `Unhandled call "${node.expression.getText(
                      node.getSourceFile()
                    )}" (${SyntaxKind[node.kind]}, ${
                      SyntaxKind[calleeDeclaration.kind]
                    }) (parents ${SyntaxKind[node.parent.kind]} ${
                      SyntaxKind[calleeDeclaration.parent.kind]
                    }) (callee sourceFile ${
                      calleeDeclaration.getSourceFile().fileName
                    }) ${node.getText(
                      node.getSourceFile()
                    )} ${calleeDeclaration.getText(
                      calleeDeclaration.getSourceFile()
                    )}`
                  );
                }
              } else if (
                isArrowFunction(calleeDeclaration) ||
                isCallSignatureDeclaration(calleeDeclaration)
              ) {
                //
              } else {
                collector.registerDependency(
                  signatureToFullName(callerFunctionDeclaration),
                  signatureToFullName(calleeDeclaration)
                );
              }
            }
          }
        }
      } else if (isPropertyAccessExpression(node)) {
        const declarations =
          typeChecker.getSymbolAtLocation(node)?.declarations;

        if (
          declarations &&
          declarations.some((declaration) => isAccessor(declaration))
        ) {
          const isAssignment =
            isBinaryExpression(node.parent) && node.parent.left === node;
          const declaration = declarations.find((d) =>
            isAssignment ? isSetAccessor(d) : isGetAccessor(d)
          );

          if (!declaration) {
            console.log(
              declarations.map((d) =>
                signatureToFullName(d as SignatureDeclaration)
              )
            );
            throw new Error();
          } else {
            const callerFunctionDeclaration = getFunctionLikeDeclaration(node);
            collector.registerDependency(
              signatureToFullName(callerFunctionDeclaration),
              signatureToFullName(declaration as SignatureDeclaration)
            );
          }
        }
      }
    } catch (e) {
      if (!(e instanceof NotInPathError)) {
        throw e;
      }
    }

    forEachChild(node, visit(typeChecker, collector));
  };
