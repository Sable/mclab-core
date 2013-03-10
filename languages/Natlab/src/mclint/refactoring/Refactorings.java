package mclint.refactoring;

import mclint.transform.Transformer;

import natlab.toolkits.analysis.core.UseDefDefUseChain;
import ast.Name;

public class Refactorings {
  public static Refactoring renameVariable(Transformer transformer, Name name, String newName,
      UseDefDefUseChain udduChain) {
    return new RenameVariable(transformer, name, newName, udduChain);
  }
}
