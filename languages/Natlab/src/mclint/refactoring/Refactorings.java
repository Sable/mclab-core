package mclint.refactoring;

import mclint.transform.StatementRange;
import natlab.refactoring.ExtractFunction;
import ast.Name;

public class Refactorings {
  public static RenameVariable renameVariable(RefactoringContext context, Name name, String newName) {
    return new RenameVariable(context, name, newName);
  }

  public static ExtractFunction extractFunction(RefactoringContext context, StatementRange statements, 
      String extractedFunctionName) {
    return new ExtractFunction(context, statements, extractedFunctionName);
  }
}
