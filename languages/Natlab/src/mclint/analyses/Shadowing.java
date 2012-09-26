package mclint.analyses;

import java.util.HashSet;
import java.util.Set;

import mclint.AnalysisKit;
import mclint.Lint;
import mclint.LintAnalysis;
import mclint.Message;
import mclint.util.DefinitionVisitor;
import natlab.tame.builtin.Builtin;
import natlab.toolkits.path.BuiltinQuery;
import ast.ASTNode;
import ast.Name;

public class Shadowing extends DefinitionVisitor implements LintAnalysis {
  private static final String WARNING = "Definition of %s shadows a builtin function or constant.";

  private BuiltinQuery query = Builtin.getBuiltinQuery();
  private Set<String> reported = new HashSet<String>();

  protected Lint lint;

  public Shadowing(AnalysisKit kit) {
    super(kit.getAST());
  }

  private Message shadow(ASTNode node, String name) {
    return Message.regarding(node, "SHADOW_BUILTIN", String.format(WARNING, name));
  }

  @Override
  public void caseDefinition(Name node) {
    if (reported.contains(node.getID()))
      return;
    if (query.isBuiltin(node.getID())) {
      lint.report(shadow(node, node.getID()));
      reported.add(node.getID());
    }
  }

  @Override
  public void analyze(Lint lint) {
    this.lint = lint;
    tree.analyze(this);
  }
}
