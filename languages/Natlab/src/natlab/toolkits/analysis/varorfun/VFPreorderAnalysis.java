/*
Copyright 2011 Anton Dubrau, Jesse Doherty, Soroush Radpour and McGill University.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

 */

package natlab.toolkits.analysis.varorfun;
import java.util.List;
import java.util.Map;
import java.util.Set;

import natlab.LookupFile;
import natlab.toolkits.analysis.AbstractPreorderAnalysis;
import natlab.toolkits.filehandling.FunctionOrScriptQuery;
import ast.ASTNode;
import ast.AssignStmt;
import ast.CellIndexExpr;
import ast.DotExpr;
import ast.EndExpr;
import ast.Expr;
import ast.Function;
import ast.FunctionHandleExpr;
import ast.FunctionList;
import ast.GlobalStmt;
import ast.LValueExpr;
import ast.LambdaExpr;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.PersistentStmt;
import ast.Script;
import ast.StringLiteralExpr;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/** 
 * An implementation of a preorder analysis for the var or fun
 * analysis. 
 */
public class VFPreorderAnalysis extends AbstractPreorderAnalysis< VFFlowset > implements VFAnalysis
{
  private boolean inFunction = true;
  private Function currentFunction = null;
  private Script currentScript = null;
  private FunctionOrScriptQuery lookupQuery = null;
  private LValueExpr outerParameterizedExpr = null;
  private List<NameExpr> endCandidates = null;

  /**
   * initializes the VFPreorderAnalysis using LookupFile.getFunctionOrScriptQueryObject().
   * This is deprecated, because using this lookupFile should be made explicit.
   * With this function, an environment is assumed that may not be intended by the user.
   * @param tree
   */
  public VFPreorderAnalysis(ASTNode tree) {
    this(tree,LookupFile.getFunctionOrScriptQueryObject());
  }

  public VFPreorderAnalysis(ASTNode tree , FunctionOrScriptQuery lookup) {
    super(tree);
    lookupQuery = lookup;
    currentSet = newInitialFlow();
  }

  public VFFlowset newInitialFlow() {
    return new VFFlowset();
  }

  private boolean isNestedFunction(Function f) {
    return f != null && f.getParent() != null && f.getParent().getParent() instanceof Function;
  }
  
  private void putVar(String name) {
    currentSet.add(ValueDatumPair.create(name, VFDatum.VAR));
  }
  
  private void putFun(String name) {
    currentSet.add(ValueDatumPair.create(name, VFDatum.FUN));
  }

  public void caseCondition(Expr condExpr) {
    caseASTNode(condExpr);
  }

  public void caseFunctionList(FunctionList node) {
    for (Function f : node.getFunctions()) {
      currentSet = newInitialFlow();
      f.analyze(this);
    }
  }

  public void caseScript(Script node) {
    currentScript = node;
    currentSet = newInitialFlow();
    inFunction = false;
    node.getStmts().analyze(this);
    flowSets.put(node, currentSet);
    currentScript = null;
  }

  public void caseFunction(Function node) {
    inFunction = true;
    currentFunction = node;
    currentSet = newInitialFlow();
    if (isNestedFunction(node)) {
      for (Map.Entry<String, VFDatum> pair : flowSets.get(node.getParent().getParent())) {
        if (pair.getValue() == VFDatum.VAR  || pair.getValue() == VFDatum.BOT) {
          currentSet.add( pair );
        }
      }
    }

    for (Name n : node.getOutputParams()) {
      putVar(n.getID());
    }

    for (Name n : node.getInputParams()) {
      putVar(n.getID());
    }

    node.getStmts().analyze(this);

    //backup currentSet
    flowSets.put(node, currentSet);
    for (Function f : node.getNestedFunctions()) {
      f.analyze(this);
    }
    currentSet = flowSets.get(node);
    currentFunction = null;
  }

  public void caseAssignStmt(AssignStmt node) {
    node.getRHS().analyze(this);
    for (NameExpr n : node.getLHS().getNameExpressions()) {
      putVar(n.getName().getID());
      annotateNode(n.getName());
    }
    node.getLHS().analyze( this );
  }

  public void caseGlobalStmt(GlobalStmt node) {
    for (Name n : node.getNames()) {
      putVar(n.getID());
      annotateNode(n);
    }
  }

  public void casePersistentStmt(PersistentStmt node) {
    for (Name n : node.getNames()) {
      putVar(n.getID());
      annotateNode(n);
    }
  }

  public void caseFunctionHandleExpr(FunctionHandleExpr node) {
    putFun(node.getName().getID());
    annotateNode(node.getName());
  }

  private ASTNode getTarget(LValueExpr node) {
    if (node instanceof CellIndexExpr) {
      return ((CellIndexExpr) node).getTarget();
    }
    if (node instanceof ParameterizedExpr) {
      return ((ParameterizedExpr) node).getTarget();
    }
    if (node instanceof DotExpr) {
      return ((DotExpr) node).getTarget();
    }
    System.err.println("in LValue without any target");
    return null;
  }

  private ASTNode getArgs(LValueExpr node) {
    if (node instanceof CellIndexExpr) {
      return ((CellIndexExpr) node).getArgs();
    }
    if (node instanceof ParameterizedExpr) {
      return ((ParameterizedExpr) node).getArgs();
    }
    return null;
  }

  public void caseMyLValue(LValueExpr node) {
    ASTNode target = getTarget(node);
    ASTNode args = getArgs(node);
    NameExpr res = Iterables.getFirst(target.getNameExpressions(), null);
    String targetName = res.getName().getID();

    List<NameExpr> candidatesBackup;
    if (endCandidates != null) {
      candidatesBackup = Lists.newArrayList(endCandidates);
    } else {
      candidatesBackup = Lists.newArrayList();
    }

    if (outerParameterizedExpr == null){
      endCandidates = Lists.newArrayList();
      outerParameterizedExpr = node;
    }

    target.analyze( this );

    VFDatum d = currentSet.contains(targetName);
    if (d.isID()) {
      endCandidates.add(res);
    } else if (d == VFDatum.VAR) {
      endCandidates = Lists.newArrayList();
      endCandidates.add(res);
    }

    args.analyze( this );

    endCandidates = candidatesBackup;
    if (outerParameterizedExpr == node) {
      endCandidates = null;
      outerParameterizedExpr = null;
    }
  }

  private void handleLoad(ParameterizedExpr node) {
    if (!(node.getTarget() instanceof NameExpr)) {
      return;
    }
    NameExpr target = (NameExpr) node.getTarget();
    if (!target.getName().getID().equals("load")) {
      return;
    }
    for (Expr arg : node.getArgs()) {
      if (arg instanceof StringLiteralExpr) {
        String param = ((StringLiteralExpr) arg).getValue();
        if (param.charAt(0) != '-'){
          currentSet.add(ValueDatumPair.create(param, VFDatum.LDVAR));
        }
      }
    }
  }

  @Override
  public void caseParameterizedExpr(ParameterizedExpr node) {
    handleLoad(node);
    caseMyLValue(node);
  }

  @Override
  public void caseEndExpr(EndExpr e) {
    if (endCandidates == null || endCandidates.isEmpty()) {
      if (outerParameterizedExpr==null) {
        System.err.println("Cannot bind end to anything");
      } else {
        NameExpr res = Iterables.getFirst(getTarget(outerParameterizedExpr).getNameExpressions(), null);
        System.err.println("No candidates, making " +res.getName().getID() + " a TOP" );
        bindError(res, e);
      }
      return;
    }

    if (endCandidates.size() == 1) {
      bindWarn(endCandidates.get(0), e);
      return;
    }

    if (inFunction) {
      bindError(endCandidates.get(0), e);
      System.err.println("More than one candidate, making " + endCandidates.get(0).getName().getID() + " a TOP, Cands are :");
      for (NameExpr n : endCandidates)
        System.err.println("    " +n.getName().getID());
      return;
    }

    NameExpr toBind = null;
    for (NameExpr n: endCandidates) {
      if (!scriptOrFunctionExists(n.getName().getID())) {
        if (toBind == null) {
          toBind = n;
        } else {
          System.err.println("More than one candidate, making " + n.getName().getID() + " a TOP" );
          bindError(n, e);
        }
      }
    }
    if (toBind == null) { // all cands exist in the lib
      bindWarn(endCandidates.get(0), e);
    } else {
      bindWarn(toBind, e);
    }
  }

  private void bindError(NameExpr n, EndExpr e){
    currentSet.add(n.getName().getID(), VFDatum.TOP);
    annotateNode(n.getName());
  }

  private void bindWarn(NameExpr n, EndExpr e){
    VFDatum d = currentSet.contains(n.getName().getID());
    if (d != VFDatum.VAR) {
      currentSet.add(n.getName().getID(), VFDatum.VAR);
    }
    annotateNode(n.getName());
  }

  public void caseCellIndexExpr(CellIndexExpr node){
    NameExpr n = Iterables.getFirst(node.getTarget().getNameExpressions(), null);
    currentSet.add(ValueDatumPair.create(n.getName().getID(), VFDatum.VAR));
    annotateNode(n.getName());
    caseMyLValue(node);
  }

  public void caseLambdaExpr(LambdaExpr node){
    VFFlowset backup = currentSet.copy();
    Set<String> inputSet = Sets.newTreeSet();
    for (Name inputParam: node.getInputParams()){
      currentSet.add(ValueDatumPair.create(inputParam.getID(),VFDatum.VAR));
      inputSet.add(inputParam.getID());
    }
    caseASTNode(node);
    for (Map.Entry<String, VFDatum> p : currentSet){
      if (!inputSet.contains(p.getKey())) {
        backup.add(ValueDatumPair.create(p.getKey(), p.getValue()));
      }
    }
    currentSet=backup;
  }

  public void caseNameExpr( NameExpr node ) {
    String s = node.getName().getID();
    VFDatum d = currentSet.contains(s);
    VFDatum kind = null;
    if (inFunction) {
      if (s != null && VFDatum.BOT.equals(d)) {
        if (scriptOrFunctionExists(s)) {
          kind = VFDatum.FUN;
        } else if (packageExists(s)) {
          kind = VFDatum.PREFIX;
        } else {
          kind = VFDatum.BOT;
        }
      }
    } else if (VFDatum.BOT.equals(d)) {
      kind = VFDatum.LDVAR;
    }
    currentSet.add(ValueDatumPair.create(s, kind));
    annotateNode(node.getName());
  }

  private boolean scriptOrFunctionExists(String name){
    return ((currentFunction != null) && (currentFunction.lookupFunction(name) != null))
        || ((currentScript != null) && false) //TODO - should return if name is the name of the script
        || lookupQuery.isFunctionOrScript(name);
  }

  private boolean packageExists(String name){
    return lookupQuery.isPackage(name);
  }

  private void annotateNode(Name n){
    if (inFunction) {
      flowSets.put(n, currentSet);
    } else {
      flowSets.put(n, currentSet.copy());
    }
  }

  @Override
  public VFDatum getResult(Name n) {
    return flowSets.get(n).contains(n.getID());
  }

  public FunctionOrScriptQuery getQuery() {
    return lookupQuery;
  }
}
