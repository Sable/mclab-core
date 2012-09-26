package mclint.analyses;

import java.util.List;

import mclint.util.Mergers;
import natlab.toolkits.analysis.HashMapFlowMap;
import natlab.toolkits.utils.NodeFinder;
import analysis.AbstractSimpleStructuralForwardAnalysis;
import ast.ASTNode;
import ast.Function;

public class PurityAnalysis extends AbstractSimpleStructuralForwardAnalysis<HashMapFlowMap<Function, Boolean>> {

  private List<Function> allFunctions;
  private HashMapFlowMap<Function, Boolean> initialFlow;

  private Function getFunctionByName(String name) {
    for (Function f : allFunctions)
      if (f.getName().equals(name))
        return f;
    return null;
  }

  public PurityAnalysis(ASTNode tree) {
    super(tree);
    allFunctions = NodeFinder.find(tree, Function.class);
    for (Function f : allFunctions)
      initialFlow.put(f, true);
  }

  @Override
  public void merge(HashMapFlowMap<Function, Boolean> in1, HashMapFlowMap<Function, Boolean> in2,
      HashMapFlowMap<Function, Boolean> out) {
    in1.union(Mergers.OR, in2, out);
  }

  @Override
  public void copy(HashMapFlowMap<Function, Boolean> source, HashMapFlowMap<Function, Boolean> dest) {
    source.copy(dest);
  }

  @Override
  public HashMapFlowMap<Function, Boolean> newInitialFlow() {
    return initialFlow.copy();
  }

}
