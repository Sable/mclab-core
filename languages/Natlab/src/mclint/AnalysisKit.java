package mclint;

import java.util.Set;

import natlab.toolkits.analysis.core.LivenessAnalysis;
import natlab.toolkits.analysis.core.ReachingDefs;
import natlab.toolkits.analysis.varorfun.VFAnalysis;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import analysis.Analysis;
import ast.ASTNode;

import com.google.common.collect.Sets;

public class AnalysisKit {
  private ASTNode<?> tree;
  private VFAnalysis varorfun;
  private ReachingDefs reachingDefs;
  private LivenessAnalysis liveVars;
  
  private Set<Analysis> dirtyAnalyses;

  public static AnalysisKit forAST(ASTNode<?> tree) {
    return new AnalysisKit(tree, new VFPreorderAnalysis(tree), new ReachingDefs(tree),
        new LivenessAnalysis(tree));
  }

  public ASTNode<?> getAST() {
    return tree;
  }

  private Analysis ensureAnalyzed(Analysis analysis) {
    if (dirtyAnalyses.contains(analysis)) {
      analysis.analyze();
      dirtyAnalyses.remove(analysis);
    };
    return analysis;
  }

  public VFAnalysis getKindAnalysis() {
    return (VFAnalysis) ensureAnalyzed(varorfun);
  }

  public ReachingDefs getReachingDefinitionsAnalysis() {
    return (ReachingDefs) ensureAnalyzed(reachingDefs);
  }

  public LivenessAnalysis getLiveVariablesAnalysis() {
    return (LivenessAnalysis) ensureAnalyzed(liveVars);
  }
  
  public void notifyTreeChanged() {
    dirtyAnalyses = Sets.newHashSet(varorfun, reachingDefs, liveVars);
  }

  private AnalysisKit(ASTNode<?> tree, VFAnalysis vf, ReachingDefs rd, LivenessAnalysis la) {
    this.tree = tree;
    this.varorfun = vf;
    this.reachingDefs = rd;
    this.liveVars = la;
    
    dirtyAnalyses = Sets.newHashSet(varorfun, reachingDefs, liveVars);
  }
}
