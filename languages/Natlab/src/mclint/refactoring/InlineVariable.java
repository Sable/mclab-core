package mclint.refactoring;

import mclint.transform.Transformer;
import natlab.toolkits.analysis.core.UseDefDefUseChain;
import ast.AssignStmt;
import ast.MatrixExpr;
import ast.Name;
import ast.NameExpr;

public class InlineVariable extends Refactoring {
  private AssignStmt definition;

  InlineVariable(RefactoringContext context, AssignStmt definition) {
    super(context);
    this.definition = definition;
  }

  @Override
  public boolean checkPreconditions() {
    // TODO(isbadawi): I suppose you would also check that the RHS has no side effects...
    return definition.getLHS() instanceof NameExpr
        || definition.getLHS() instanceof MatrixExpr && definition.getNumChild() == 1;
  }

  @Override
  public void apply() {
    Transformer transformer = context.getTransformer(definition);

    UseDefDefUseChain udduChain = definition.getMatlabProgram().analyze().getUseDefDefUseChain();
    for (Name name : udduChain.getUses(definition)) {
      transformer.replace(name.getParent(), transformer.copy(definition.getRHS()));
    }
    transformer.remove(definition);
  }
}