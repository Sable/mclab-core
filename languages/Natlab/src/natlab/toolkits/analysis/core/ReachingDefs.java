// =========================================================================== //
//                                                                             //
// Copyright 2011 Jesse Doherty and McGill University.                         //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

package natlab.toolkits.analysis.core;

import java.util.HashSet;
import java.util.Set;

import natlab.toolkits.analysis.AbstractFlowMap;
import natlab.toolkits.analysis.HashMapFlowMap;
import natlab.toolkits.analysis.Merger;
import natlab.toolkits.analysis.Mergers;
import natlab.utils.NodeFinder;
import analysis.AbstractSimpleStructuralForwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Function;
import ast.GlobalStmt;
import ast.Name;
import ast.Script;
import ast.Stmt;

import com.google.common.collect.Sets;

/**
 * A simple forward analysis example, computing reaching definitions.
 * 
 * @author Jesse Doherty
 */
public class ReachingDefs extends
        AbstractSimpleStructuralForwardAnalysis<HashMapFlowMap<String, Set<Def>>> {
  public static Def UNDEF = new AssignStmt();

  private final Merger<Set<Def>> merger = Mergers.union();

  private final HashMapFlowMap<String, Set<Def>> startMap;
  private final NameCollector nameCollector;

  public ReachingDefs(ASTNode<?> tree) {
    super(tree);
    startMap = new HashMapFlowMap<String, Set<Def>>(merger);
    nameCollector = new NameCollector(tree);
    nameCollector.analyze();
    for (String var : nameCollector.getAllNames())
      startMap.put(var, new HashSet<Def>());
  }

  /**
   * Defines the merge operation for this analysis.
   * 
   * This implementation uses the union method defined by
   * {@link AbstractFlowMap}. Note that the union method deals with the cases
   * where {@literal in1==in2}, {@literal in1==out} or {@literal in2==out}.
   */
  @Override
  public void merge(HashMapFlowMap<String, Set<Def>> in1,
          HashMapFlowMap<String, Set<Def>> in2, HashMapFlowMap<String, Set<Def>> out) {
    in1.union(merger, in2, out);
  }

  /**
   * Creates a copy of the FlowMap with copies of the contained set.
   */
  @Override
  public void copy(HashMapFlowMap<String, Set<Def>> in,
          HashMapFlowMap<String, Set<Def>> out) {
    if (in == out)
      return;
    out.clear();
    for (String i : in.keySet())
      out.put(i, Sets.newHashSet(in.get(i)));
  }

  /**
   * Creates a copy of the given flow-map and returns it.
   */
  public HashMapFlowMap<String, Set<Def>> copy(HashMapFlowMap<String, Set<Def>> in) {
    HashMapFlowMap<String, Set<Def>> out = new HashMapFlowMap<String, Set<Def>>();
    copy(in, out);
    return out;
  }

  @Override
  public void caseFunction(Function f) {
    currentOutSet = newInitialFlow();
    currentInSet = currentOutSet;
    for (Name n : NodeFinder.find(Name.class, f.getStmts())) {
      currentOutSet.put(n.getID(), Sets.<Def> newHashSet(UNDEF));
    }
    for (Name inputArg : f.getInputParamList()) {
      currentOutSet.put(inputArg.getID(), Sets.<Def> newHashSet(inputArg));
    }
    caseASTNode(f.getStmts());
    outFlowSets.put(f, currentOutSet);
  }

  @Override
  public void caseScript(Script f) {
    currentOutSet = newInitialFlow();
    currentInSet = currentOutSet;
    for (Name n : NodeFinder.find(Name.class, f)) {
      currentOutSet.put(n.getID(), Sets.<Def> newHashSet(UNDEF));
    }
    caseASTNode(f.getStmts());
    outFlowSets.put(f, currentOutSet);
  }

  @Override
  public HashMapFlowMap<String, Set<Def>> newInitialFlow() {
    return copy(startMap);
  }

  @Override
  public void caseAssignStmt(AssignStmt node) {
    inFlowSets.put(node, currentInSet);
    currentOutSet = copy(currentInSet);
    for (String n : nameCollector.getNames(node)) {
      currentOutSet.put(n, Sets.<Def> newHashSet(node));
    }
    outFlowSets.put(node, currentOutSet);
  }

  @Override
  public void caseStmt(Stmt node) {
    inFlowSets.put(node, currentInSet);
    currentOutSet = currentInSet;
    outFlowSets.put(node, currentOutSet);
  }
  
  @Override
  public void caseGlobalStmt(GlobalStmt node) {
    inFlowSets.put(node, currentInSet);
    currentOutSet = currentInSet;
    
    for (Name name : node.getNames()) {
      currentOutSet.put(name.getID(), Sets.<Def>newHashSet(node));
    }
    outFlowSets.put(node, currentOutSet);
  }

  private UseDefDefUseChain udduChainCached = null;

  public UseDefDefUseChain getUseDefDefUseChain() {
    if (udduChainCached == null) {
      udduChainCached = UseDefDefUseChain.fromReachingDefs(this);
    }
    return udduChainCached;
  }
}
