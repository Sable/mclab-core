package mclint.analyses;

import java.util.Set;

import mclint.AnalysisKit;
import mclint.Lint;
import mclint.LintAnalysis;
import mclint.Message;
import mclint.util.DefinitionVisitor;
import natlab.toolkits.analysis.HashSetFlowSet;
import natlab.toolkits.analysis.liveliness.LivelinessAnalysis;
import natlab.toolkits.utils.NodeFinder;
import ast.ASTNode;
import ast.ForStmt;
import ast.Function;
import ast.Name;
import ast.Stmt;

import com.google.common.collect.Sets;

public class UnusedVar extends DefinitionVisitor implements LintAnalysis {
  private static final String WARNING = "Unused variable %s.";

  private LivelinessAnalysis liveVar;

  /* We shouldn't warn that output parameters aren't used. */
  private Set<String> outputParams = Sets.newHashSet();

  protected Lint lint;

  private Message unused(ASTNode<?> node, String name) {
    return Message.regarding(node, "UNUSED_VAR", String.format(WARNING, name));
  }

  public UnusedVar(AnalysisKit kit) {
    super(kit.getAST());
    this.liveVar = kit.getLiveVariablesAnalysis();
  }

  @Override
  public void caseFunction(Function node) {
    Set<String> outputParamsCopy = Sets.newHashSet(outputParams);
    outputParams.addAll(node.getOutParamSet());
    super.caseFunction(node);
    outputParams.retainAll(outputParamsCopy);
  }

  private boolean isLive(Name node) {
    Stmt parentStmt = NodeFinder.findParent(node, Stmt.class);
    HashSetFlowSet<String> setToCheck;
    if (parentStmt.getParent() instanceof ForStmt) {
      Stmt first = ((ForStmt) (parentStmt.getParent())).getStmt(0);
      setToCheck = liveVar.getInFlowSets().get(first);
    } else {
      setToCheck = liveVar.getOutFlowSets().get(parentStmt);
    }
    return setToCheck.contains(node.getID());
  }

  @Override
  public void caseLHSName(Name node) {
    if (outputParams.contains(node.getID())) {
      return;
    }
    if (!isLive(node)) {
      lint.report(unused(node, node.getID()));
    }
  }

  @Override
  public void caseInParam(Name node) {
    Stmt firstStatement = NodeFinder.findParent(node, Function.class).getStmt(0);
    if (!liveVar.isLive(firstStatement, node.getID())) {
      lint.report(unused(node, node.getID()));
    }
  }

  @Override
  public void analyze(Lint lint) {
    this.lint = lint;
    tree.analyze(this);
  }
}
