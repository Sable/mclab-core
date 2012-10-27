package mclint;

import natlab.toolkits.analysis.liveliness.LivelinessAnalysis;
import natlab.toolkits.analysis.test.ReachingDefs;
import natlab.toolkits.analysis.varorfun.VFAnalysis;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import analysis.Analysis;
import ast.ASTNode;

public class AnalysisKit {
  private ASTNode<?> tree;
  private VFAnalysis varorfun;
  private ReachingDefs reachingDefs;
  private LivelinessAnalysis liveVars;

  public static AnalysisKit forAST(ASTNode<?> tree) {
    return new AnalysisKit(tree, new VFPreorderAnalysis(tree), new ReachingDefs(tree),
        new LivelinessAnalysis(tree));
  }

  public ASTNode<?> getAST() {
    return tree;
  }

  private Analysis ensureAnalyzed(Analysis analysis) {
    if (!analysis.isAnalyzed())
      analysis.analyze();
    return analysis;
  }

  public VFAnalysis getKindAnalysis() {
    return (VFAnalysis) ensureAnalyzed(varorfun);
  }

  public ReachingDefs getReachingDefinitionsAnalysis() {
    return (ReachingDefs) ensureAnalyzed(reachingDefs);
  }

  public LivelinessAnalysis getLiveVariablesAnalysis() {
    return (LivelinessAnalysis) ensureAnalyzed(liveVars);
  }

  private AnalysisKit(ASTNode<?> tree, VFAnalysis vf, ReachingDefs rd, LivelinessAnalysis la) {
    this.tree = tree;
    this.varorfun = vf;
    this.reachingDefs = rd;
    this.liveVars = la;
  }
}
