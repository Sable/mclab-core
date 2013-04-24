package mclint.analyses;

import java.util.Set;

import mclint.Lint;
import mclint.LintAnalysis;
import mclint.Message;
import mclint.Project;
import mclint.util.DefinitionVisitor;
import natlab.tame.builtin.Builtin;
import natlab.toolkits.path.BuiltinQuery;
import ast.Name;

import com.google.common.collect.Sets;

public class Shadowing extends DefinitionVisitor implements LintAnalysis {
  private BuiltinQuery query = Builtin.getBuiltinQuery();
  private Set<String> reported = Sets.newHashSet();

  protected Lint lint;

  public Shadowing(Project project) {
    super(project.asCompilationUnits());
  }

  private void reportShadowing(Name node) {
    if (!reported.contains(node.getID())) {
      lint.report(Message.regarding(node, "SHADOW_BUILTIN",
          "Definition of " + node.getID() + " shadows a builtin function or constant."));
      reported.add(node.getID());
    }
  }

  @Override
  public void caseDefinition(Name node) {
    if (query.isBuiltin(node.getID())) {
      reportShadowing(node);
    }
  }

  @Override
  public void analyze(Lint lint) {
    this.lint = lint;
    tree.analyze(this);
  }
}
