package mclint.refactoring;

import natlab.refactoring.ExtractFunction;
import ast.Function;
import ast.Name;

public class Refactorings {
  public static Refactoring renameVariable(RefactoringContext context, Name name, String newName) {
    return new RenameVariable(context, name, newName);
  }

  public static Refactoring extractFunction(RefactoringContext context, Function f, int from, int to,
      String extractedFunctionName) {
    return new ExtractFunction(context, f, from, to, extractedFunctionName);
  }
}
