package mclint.analyses;

import java.util.HashSet;
import java.util.Set;

import mclint.Lint;
import mclint.LintAnalysis;
import mclint.Message;
import mclint.Project;
import mclint.util.DefinitionVisitor;
import ast.ForStmt;
import ast.Name;

import com.google.common.collect.Sets;

public class ChangedLoopVar extends DefinitionVisitor implements LintAnalysis {
  private static final String WARNING = "Loop variable %s is changed inside the loop.";

  private boolean inLoopStmt = false;
  private Set<String> currentLoopVars = Sets.newHashSet();

  protected Lint lint;

  private Message changedLoopVar(Name node) {
    return Message.regarding(node, "CHANGED_LOOP_VAR", String.format(WARNING, node.getID()));
  }

  public ChangedLoopVar(Project project) {
    super(project.asCompilationUnits());
  }

  @Override
  public void caseLHSName(Name node) {
    if (inLoopStmt)
      currentLoopVars.add(node.getID());
    else if (currentLoopVars.contains(node.getID()))
      lint.report(changedLoopVar(node));
  }

  @Override
  public void caseForStmt(ForStmt stmt) {
    Set<String> loopVarsCopy = new HashSet<String>(currentLoopVars);
    inLoopStmt = true;
    stmt.getAssignStmt().analyze(this);
    inLoopStmt = false;
    stmt.getStmts().analyze(this);
    currentLoopVars.retainAll(loopVarsCopy);
  }

  @Override
  public void analyze(Lint lint) {
    this.lint = lint;
    tree.analyze(this);
  }
}
