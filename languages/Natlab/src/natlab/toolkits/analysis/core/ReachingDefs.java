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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import natlab.toolkits.analysis.MergeUtil;
import natlab.toolkits.analysis.Merger;
import natlab.toolkits.analysis.Mergers;
import natlab.utils.NodeFinder;
import analysis.ForwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Function;
import ast.GlobalStmt;
import ast.MatrixExpr;
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
        ForwardAnalysis<Map<String, Set<Def>>> {
  public static Def UNDEF = new AssignStmt();

  private Set<Name> defs = Sets.newHashSet();
  private static final Merger<Set<Def>> UNION_MERGER = Mergers.union();

  private Map<String, Set<Def>> startMap;
  private DefinitelyAssignedAnalysis definiteAssignment;
  private NameCollector nameCollector;

  public ReachingDefs(ASTNode<?> tree) {
    super(tree);
  }
  
  @Override
  public void analyze() {
    nameCollector = new NameCollector(tree);
    nameCollector.analyze();
    definiteAssignment = new DefinitelyAssignedAnalysis(tree);
    definiteAssignment.analyze();
    
    startMap = nameCollector.getAllNames().stream()
        .map(Name::getID)
        .distinct()
        .collect(Collectors.toMap(name -> name, name -> new HashSet<>()));
    super.analyze();
  }

  @Override
  public Map<String, Set<Def>> merge(Map<String, Set<Def>> in1, Map<String, Set<Def>> in2) {
    return MergeUtil.unionMerge(in1, in2, UNION_MERGER);
  }

  @Override
  public Map<String, Set<Def>> copy(Map<String, Set<Def>> in) {
    return in.entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey(), e -> Sets.newHashSet(e.getValue())));
  }

  @Override
  public void caseFunction(Function f) {
    currentOutSet = newInitialFlow();
    currentInSet = currentOutSet;
    
    currentOutSet.putAll(NodeFinder.find(Name.class, f.getStmts())
        .map(Name::getID)
        .distinct()
        .collect(Collectors.toMap(name -> name, name -> Sets.newHashSet(UNDEF))));
    currentOutSet.putAll(f.getInputParams().stream()
        .collect(Collectors.toMap(Name::getID, Sets::<Def>newHashSet)));
    caseASTNode(f.getStmts());
    outFlowSets.put(f, currentOutSet);
  }

  @Override
  public void caseScript(Script f) {
    currentOutSet = newInitialFlow();
    currentInSet = currentOutSet;
    
    currentOutSet.putAll(NodeFinder.find(Name.class, f)
        .map(Name::getID)
        .distinct()
        .collect(Collectors.toMap(name -> name, name -> Sets.newHashSet(UNDEF))));
    caseASTNode(f.getStmts());
    outFlowSets.put(f, currentOutSet);
  }

  @Override
  public Map<String, Set<Def>> newInitialFlow() {
    return copy(startMap);
  }
  
  private boolean isSimpleLValue(Name node) {
    return node.getParent().getParent() instanceof AssignStmt
        || node.getParent().getParent() instanceof MatrixExpr;
  }

  private Set<Name> getSimpleDefs(final AssignStmt node) {
    return nameCollector.getNames(node).stream()
        .filter(this::isSimpleLValue)
        .collect(Collectors.toSet());
  }
  
  private Set<Name> getImplicitDefs(final AssignStmt node) {
    return nameCollector.getNames(node).stream()
        .filter(name -> !isSimpleLValue(name))
        .filter(name -> !definiteAssignment.isDefinitelyAssignedAtInputOf(node, name.getID()))
        .collect(Collectors.toSet());
  }

  @Override
  public void caseAssignStmt(AssignStmt node) {
    currentOutSet = copy(currentInSet);
    for (Name n : getSimpleDefs(node)) {
      defs.add(n);
      currentOutSet.put(n.getID(), Sets.<Def> newHashSet(node));
    }
    for (Name n : getImplicitDefs(node)) {
      defs.add(n);
      currentInSet.get(n.getID()).remove(UNDEF);
      currentInSet.get(n.getID()).add(node);
      currentOutSet.get(n.getID()).remove(UNDEF);
      currentOutSet.get(n.getID()).add(node);
    }
    inFlowSets.put(node, currentInSet);
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
    currentOutSet = copy(currentInSet);
    currentOutSet.putAll(node.getNames().stream()
        .collect(Collectors.toMap(Name::getID, name -> Sets.<Def>newHashSet(node))));
    outFlowSets.put(node, currentOutSet);
  }
  
  public boolean isDef(Name name) {
    return defs.contains(name);
  }

  private UseDefDefUseChain udduChainCached = null;

  public UseDefDefUseChain getUseDefDefUseChain() {
    if (udduChainCached == null) {
      udduChainCached = UseDefDefUseChain.fromReachingDefs(this);
    }
    return udduChainCached;
  }
}
