package mclint;

import natlab.toolkits.analysis.core.LivenessAnalysis;
import natlab.toolkits.analysis.core.ReachingDefs;
import natlab.toolkits.analysis.core.UseDefDefUseChain;
import natlab.toolkits.analysis.varorfun.VFAnalysis;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import analysis.Analysis;
import ast.ASTNode;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

public class AnalysisKit {
  private ASTNode<?> tree;
  private ClassToInstanceMap<Analysis> analysisCache = MutableClassToInstanceMap.create();

  public static AnalysisKit forAST(ASTNode<?> tree) {
    return new AnalysisKit(tree);
  }

  public ASTNode<?> getAST() {
    return tree;
  }
  
  private <T extends Analysis> T construct(Class<T> clazz) {
    T analysis = null;
    try {
      analysis = clazz.getConstructor(ASTNode.class).newInstance(tree);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    analysis.analyze();
    return analysis;
  }
  
  private <T extends Analysis> T getOrCreate(Class<T> clazz) {
    if (!analysisCache.containsKey(clazz)) {
      analysisCache.putInstance(clazz, construct(clazz));
    }
    return analysisCache.getInstance(clazz);
  }

  public VFAnalysis getKindAnalysis() {
   return getOrCreate(VFPreorderAnalysis.class);
  }

  public ReachingDefs getReachingDefinitionsAnalysis() {
    return getOrCreate(ReachingDefs.class);
  }
  
  public UseDefDefUseChain getUseDefDefUseChain() {
    return getReachingDefinitionsAnalysis().getUseDefDefUseChain();
  }

  public LivenessAnalysis getLiveVariablesAnalysis() {
    return getOrCreate(LivenessAnalysis.class);
  }
  
  public void notifyTreeChanged() {
    analysisCache.clear();
  }

  private AnalysisKit(ASTNode<?> tree) {
    this.tree = tree;
  }
}
