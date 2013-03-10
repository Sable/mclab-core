package mclint.refactoring;

import mclint.transform.Transformer;
import natlab.refactoring.ExtractFunction;
import natlab.toolkits.analysis.core.UseDefDefUseChain;
import ast.Function;
import ast.Name;

public class Refactorings {
  public static Refactoring renameVariable(Transformer transformer, Name name, String newName,
      UseDefDefUseChain udduChain) {
    return new RenameVariable(transformer, name, newName, udduChain);
  }
  
  public static Refactoring extractFunction(Transformer transformer, Function f, int from, int to,
      String extractedFunctionName) {
    return new ExtractFunction(transformer, f, from, to, extractedFunctionName);
  }
}
