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
import nodecases.AbstractNodeCaseHandler;
import analysis.AbstractSimpleStructuralForwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.CellIndexExpr;
import ast.DotExpr;
import ast.Function;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Script;
import ast.Stmt;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * A simple forward analysis example, computing reaching definitions.
 * 
 * @author Jesse Doherty
 */
public class ReachingDefs extends
        AbstractSimpleStructuralForwardAnalysis<HashMapFlowMap<String, Set<AssignStmt>>> {
  public static final AssignStmt UNDEF = new AssignStmt();
  public static final AssignStmt PARAM = new AssignStmt();
  public static final AssignStmt GLOBAL = new AssignStmt();

  private final Merger<Set<AssignStmt>> merger = Mergers.union();

  private final HashMapFlowMap<String, Set<AssignStmt>> startMap;
  private final NameCollector nameCollector;

  public ReachingDefs(ASTNode<?> tree) {
    super(tree);
    startMap = new HashMapFlowMap<String, Set<AssignStmt>>(merger);
    nameCollector = new NameCollector(tree);
    nameCollector.analyze();
    for (String var : nameCollector.getAllNames())
      startMap.put(var, new HashSet<AssignStmt>());
  }

  /**
   * Defines the merge operation for this analysis.
   * 
   * This implementation uses the union method defined by
   * {@link AbstractFlowMap}. Note that the union method deals with the cases
   * where {@literal in1==in2}, {@literal in1==out} or {@literal in2==out}.
   */
  @Override
  public void merge(HashMapFlowMap<String, Set<AssignStmt>> in1,
          HashMapFlowMap<String, Set<AssignStmt>> in2, HashMapFlowMap<String, Set<AssignStmt>> out) {
    Set<String> keys = Sets.newHashSet(in1.keySet());
    keys.addAll(in2.keySet());
    for (String s : keys) {
      Set<AssignStmt> res = new HashSet<AssignStmt>();
      res.addAll(in1.get(s));
      res.addAll(in2.get(s));
      out.put(s, res);
    }
  }

  /**
   * Creates a copy of the FlowMap with copies of the contained set.
   */
  @Override
  public void copy(HashMapFlowMap<String, Set<AssignStmt>> in,
          HashMapFlowMap<String, Set<AssignStmt>> out) {
    if (in == out)
      return;
    out.clear();
    for (String i : in.keySet())
      out.put(i, Sets.newHashSet(in.get(i)));
  }

  /**
   * Creates a copy of the given flow-map and returns it.
   */
  public HashMapFlowMap<String, Set<AssignStmt>> copy(HashMapFlowMap<String, Set<AssignStmt>> in) {
    HashMapFlowMap<String, Set<AssignStmt>> out = new HashMapFlowMap<String, Set<AssignStmt>>();
    copy(in, out);
    return out;
  }

  @Override
  public void caseFunction(Function f) {
    currentOutSet = newInitialFlow();
    currentInSet = currentOutSet;
    for (Name n : NodeFinder.of(Name.class).findIn(f)) {
      currentOutSet.put(n.getID(), Sets.<AssignStmt> newHashSet(UNDEF));
    }
    for (Name inputArg : f.getInputParamList()) {
      currentOutSet.put(inputArg.getID(), Sets.<AssignStmt> newHashSet(PARAM));
    }
    caseASTNode(f.getStmts());
    outFlowSets.put(f, currentOutSet);
  }

  @Override
  public void caseScript(Script f) {
    currentOutSet = newInitialFlow();
    currentInSet = currentOutSet;
    for (Name n : NodeFinder.of(Name.class).findIn(f)) {
      currentOutSet.put(n.getID(), Sets.<AssignStmt> newHashSet(UNDEF));
    }
    caseASTNode(f.getStmts());
    outFlowSets.put(f, currentOutSet);
  }

  @Override
  public HashMapFlowMap<String, Set<AssignStmt>> newInitialFlow() {
    return copy(startMap);
  }

  @Override
  public void caseAssignStmt(AssignStmt node) {
    inFlowSets.put(node, currentInSet);
    currentOutSet = copy(currentInSet);
    for (String n : nameCollector.getNames(node)) {
      currentOutSet.put(n, Sets.<AssignStmt> newHashSet(node));
    }
    outFlowSets.put(node, currentOutSet);
  }

  @Override
  public void caseStmt(Stmt node) {
    inFlowSets.put(node, currentInSet);
    currentOutSet = currentInSet;
    outFlowSets.put(node, currentOutSet);
  }
  
  private Set<AssignStmt> getReachingDefs(NameExpr node) {
    Stmt parent = NodeFinder.of(Stmt.class).findParent(node);
    return getInFlowSets().get(parent).get(node.getName().getID());
  }

  private UseDefDefUseChain udduChainCached = null;

  private void createUseDefDefUseChain() {
    if (!isAnalyzed()) {
      analyze();
    }
    final ImmutableSetMultimap.Builder<NameExpr, AssignStmt> useDefChainBuilder =
        ImmutableSetMultimap.builder();
    final ImmutableSetMultimap.Builder<AssignStmt, NameExpr> defUseChainBuilder =
        ImmutableSetMultimap.builder();
    tree.analyze(new AbstractNodeCaseHandler() {
      @Override public void caseASTNode(ASTNode node) {
        for (int i = 0; i < node.getNumChild(); i++) {
          node.getChild(i).analyze(this);
        }
      }

      @Override public void caseAssignStmt(AssignStmt node) {
        if (node.getLHS() instanceof ParameterizedExpr) {
          ((ParameterizedExpr) node.getLHS()).getArgs().analyze(this);
        } else if (node.getLHS() instanceof CellIndexExpr) {
          ((CellIndexExpr) node.getLHS()).getArgs().analyze(this);
        } else if (node.getLHS() instanceof NameExpr) {
        } else if (!(node.getLHS() instanceof DotExpr)) {
          node.getLHS().analyze(this);
        }
        node.getRHS().analyze(this);
      }
      
      @Override public void caseDotExpr(DotExpr node) {
        node.getTarget().analyze(this);
      }

      @Override public void caseNameExpr(NameExpr use) {
        Set<AssignStmt> defs = getReachingDefs(use);
        useDefChainBuilder.putAll(use, defs);
        for (AssignStmt def : defs) {
          if (def != GLOBAL && def != PARAM && def != UNDEF) {
            defUseChainBuilder.put(def, use);
          }
        }
      }
    });
    final ImmutableSetMultimap<NameExpr, AssignStmt> useDefChain = useDefChainBuilder.build();
    final ImmutableSetMultimap<AssignStmt, NameExpr> defUseChain = defUseChainBuilder.build();
    udduChainCached = new UseDefDefUseChain() {
      @Override public Set<NameExpr> getUses(AssignStmt def) {
        return defUseChain.get(def);
      }
      
      @Override public Set<AssignStmt> getDefs(NameExpr use) {
        return useDefChain.get(use);
      }
    };
  }
  
  public UseDefDefUseChain getUseDefDefUseChain() {
    if (udduChainCached == null) {
      createUseDefDefUseChain();
    }
    return udduChainCached;
  }
}
