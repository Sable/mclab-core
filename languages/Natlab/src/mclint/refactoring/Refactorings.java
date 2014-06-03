package mclint.refactoring;

import mclint.transform.StatementRange;
import natlab.refactoring.ExtractFunction;
import natlab.refactoring.MScriptInliner;
import ast.AssignStmt;
import ast.Expr;
import ast.Name;
import ast.Script;

public class Refactorings {
  public static RenameVariable renameVariable(RefactoringContext context, Name name, String newName) {
    return new RenameVariable(context, name, newName);
  }

  public static ExtractFunction extractFunction(RefactoringContext context, StatementRange statements, 
      String extractedFunctionName) {
    return new ExtractFunction(context, statements, extractedFunctionName);
  }
  
  public static ExtractVariable extractVariable(RefactoringContext context, Expr expression,
      String extractedVariableName) {
    return new ExtractVariable(context, expression, extractedVariableName);
  }
  
  public static InlineVariable inlineVariable(RefactoringContext context, AssignStmt definition) {
    return new InlineVariable(context, definition);
  }
  
  public static MScriptInliner inlineScript(RefactoringContext context, Script script) {
    return new MScriptInliner(context, script);
  }
}
