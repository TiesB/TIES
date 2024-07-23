import * as ts from "typescript";
import { visit } from "./visitor";
import { Collector } from "./collector";
import { writeFileSync } from "fs";
import { glob } from "glob";
import { config } from "./config";

async function run() {
  const filePaths = await glob(config.PATH + config.GLOB_PATTERN);

  const program = ts.createProgram(filePaths, {});
  const typeChecker = program.getTypeChecker();

  const collector = new Collector();

  for (const filePath of filePaths) {
    console.log(`Starting: ${filePath}`);
    const sourceFile = program.getSourceFile(filePath);
    visit(typeChecker, collector)(sourceFile);
  }

  writeFileSync(
    "test.json",
    JSON.stringify({
      methods: collector
        .getMethods()
        .filter((method) => method.length !== null),
      functions: collector.getFunctions(),
    })
  );
}

run();
