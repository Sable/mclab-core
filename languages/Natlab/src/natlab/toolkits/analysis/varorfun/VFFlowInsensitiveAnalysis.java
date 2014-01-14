package natlab.toolkits.analysis.varorfun;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import natlab.LookupFile;
import natlab.toolkits.analysis.MergeUtil;
import natlab.toolkits.filehandling.FunctionOrScriptQuery;
import analysis.AbstractDepthFirstAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.CellIndexExpr;
import ast.EndExpr;
import ast.Expr;
import ast.Function;
import ast.FunctionHandleExpr;
import ast.GlobalStmt;
import ast.LValueExpr;
import ast.LambdaExpr;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.PersistentStmt;
import ast.Script;
import ast.StringLiteralExpr;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class VFFlowInsensitiveAnalysis extends AbstractDepthFirstAnalysis<Map<String, VFDatum>> implements VFAnalysis{
  private boolean inFunction=true;
  private Function currentFunction = null;
  private Script currentScript = null;
  private FunctionOrScriptQuery lookupQuery = null;
  private LValueExpr outerParameterizedExpr=null;
  private java.util.List<NameExpr> endCandidates = null;
  private java.util.Set<LValueExpr> withEndExpressions; 
  private int pass; 

  @Override
  public void caseCondition(Expr condExpr) {
    caseASTNode( condExpr );
  }

  @Override
  public Map<String, VFDatum> newInitialFlow() {
    return Maps.newHashMap();
  }

  public VFFlowInsensitiveAnalysis( ASTNode tree , FunctionOrScriptQuery lookup ) {
    super( tree );
    lookupQuery = lookup;
    currentSet = newInitialFlow();
  }

  @Deprecated
  public VFFlowInsensitiveAnalysis( ASTNode tree ) {
    this(tree,LookupFile.getFunctionOrScriptQueryObject());
  }

  private void putVar(String name) {
    MergeUtil.mergePut(currentSet, name, VFDatum.VAR);
  }

  private void putFun(String name) {
    MergeUtil.mergePut(currentSet, name, VFDatum.FUN);
  }

  @Override
  public void caseScript( Script node ) {
    currentScript=node;
    currentSet = newInitialFlow();
    inFunction=false;
    withEndExpressions = Sets.newHashSet();
    pass = 0;
    node.getStmts().analyze(this);
    analyzeEndExpressions();
    flowSets.put( node, currentSet );

    currentScript=null;
  }

  @Override
  public void caseFunction( Function node ) {
    pass = 0;
    inFunction=true;
    currentFunction=node;
    currentSet = newInitialFlow();

    // If a nested function, get the list of variables defined in the outer function
    if (node.getParent().getParent() instanceof Function){
      if (flowSets.containsKey(node.getParent().getParent()))
        for( Map.Entry<String, VFDatum> pair : flowSets.get(node.getParent().getParent()).entrySet()){
          if( pair.getValue()==VFDatum.VAR  || pair.getValue()==VFDatum.BOT)
            MergeUtil.mergePut(currentSet, pair.getKey(), pair.getValue());
        }
    }

    for( Name n : node.getOutputParams() ){
      putVar(n.getID());
    }

    for( Name n : node.getInputParams() ){
      putVar(n.getID());
    }

    // Process body
    withEndExpressions = new HashSet<LValueExpr>();
    node.getStmts().analyze(this);
    analyzeEndExpressions();

    //backup currentSet
    flowSets.put( node, currentSet );
    for( Function f : node.getNestedFunctions() ){
      f.analyze( this );
    }
    currentSet=flowSets.get(node);
    currentFunction = null;
  }

  private void analyzeEndExpressions(){
    pass = 1;
    for ( LValueExpr n: withEndExpressions ){
      caseMyLValue(n);
    }
  }

  private boolean scriptOrFunctionExists(String name){
    return 
        ((currentFunction != null) && (currentFunction.lookupFunction(name) != null))
        || ((currentScript != null) && (false)) //TODO - should return if name is the name of the script
        || lookupQuery.isFunctionOrScript(name);
  }

  @Override
  public void caseAssignStmt( AssignStmt node ) {

    //get the names from the lhs
    Expr lhs = node.getLHS();
    //analyze the rhs
    node.getRHS().analyze( this );
    for( NameExpr n : lhs.getNameExpressions() ) {
      putVar(n.getName().getID());
      annotateNode(n.getName());
    }
    node.getLHS().analyze( this );
  }

  @Override
  public void caseLambdaExpr(LambdaExpr node){
    return;/*
    	VFFlowset backup = currentSet.copy();
    	Set<String> inputSet=new TreeSet<String>();
    	for (Name inputParam: node.getInputParams()){
    		currentSet.add(new ValueDatumPair<String,VFDatum>(inputParam.getID(),VFDatum.VAR));
    		inputSet.add(inputParam.getID());
    	}
        System.out.println(currentSet);
    	caseASTNode(node);
    	for (Map.Entry<String, VFDatum> p:currentSet.getMap().entrySet()){
    		if (! inputSet.contains(p.getKey()))
    			backup.add(new ValueDatumPair<String, VFDatum>(p.getKey(),p.getValue()));
    	}
    	currentSet=backup;*/
  }


  @Override
  public void caseCellIndexExpr(CellIndexExpr node) {
    NameExpr n = new ArrayList<NameExpr>(((CellIndexExpr) node).getTarget().getNameExpressions()).get(0);
    putVar(n.getName().getID());
    annotateNode(n.getName());
    caseMyLValue(node);
  }

  @Override
  public void caseGlobalStmt( GlobalStmt node ) {

    for( Name n : node.getNames() ){
      putVar(n.getID());
      annotateNode(n);
    }
  }

  @Override
  public void casePersistentStmt( PersistentStmt node ) {
    for( Name n : node.getNames() ){
      putVar(n.getID());
      annotateNode(n);
    }
  }

  @Override
  public void caseFunctionHandleExpr( FunctionHandleExpr node ) {
    putFun(node.getName().getID());
    annotateNode(node.getName());
  }

  @Override
  public void caseNameExpr( NameExpr node ) {
    String s = node.getName().getID();
    VFDatum d = currentSet.get( s );
    if (s.equals("Inf") || s.equals("inf") || s.equals("NaN") || s.equals("nan"))
      MergeUtil.mergePut(currentSet, s, VFDatum.IFUN);
    if (inFunction){
      if ( s!=null && d==null || VFDatum.BOT.equals( d )  ) {
        VFDatum val = scriptOrFunctionExists(s) ? VFDatum.IFUN
            : packageExists(s) ? VFDatum.PREFIX : VFDatum.BOT;
        MergeUtil.mergePut(currentSet, s, val);
      }
    }
    else if ( d==null || VFDatum.BOT.equals(d) )
      MergeUtil.mergePut(currentSet, s, VFDatum.LDVAR);
    annotateNode(node.getName());        
  }

  private ASTNode getTarget(LValueExpr node){
    ASTNode target = null;
    if (node instanceof CellIndexExpr)
      target = ((CellIndexExpr) node).getTarget();
    else if (node instanceof ParameterizedExpr)
      target = ((ParameterizedExpr) node).getTarget();
    else
      System.err.println("in LValue without any target");
    return target;
  }

  private ASTNode getArgs(LValueExpr node){
    ASTNode args = null;
    if (node instanceof CellIndexExpr)
      args = ((CellIndexExpr) node).getArgs();
    if (node instanceof ParameterizedExpr)
      args = ((ParameterizedExpr) node).getArgs();
    return args;
  }

  public void caseMyLValue(LValueExpr node) {
    ASTNode target = getTarget(node);
    ASTNode args = getArgs(node);
    NameExpr res = new ArrayList<NameExpr>(target.getNameExpressions()).get(0);
    String targetName=res.getName().getID();

    List<NameExpr> candidates_backup;
    if (endCandidates!=null)
      candidates_backup = new ArrayList<NameExpr>(endCandidates);
    else
      candidates_backup = new ArrayList<NameExpr>();

    if (outerParameterizedExpr==null){
      endCandidates = new ArrayList<NameExpr>();
      outerParameterizedExpr=node;
    }

    if (pass == 1){
      VFDatum d =  currentSet.get( targetName ); 
      if ( d == null || d == VFDatum.BOT || VFDatum.LDVAR == d || d == VFDatum.WAR || d == VFDatum.TOP ) { 
        if (endCandidates == null ) System.out.println ("NULL");
        endCandidates.add(res);
      }
      if (d == VFDatum.VAR){
        endCandidates = new ArrayList<NameExpr>();
        endCandidates.add(res);
      }
    }

    args.analyze( this );
    target.analyze( this );


    endCandidates = candidates_backup;
    if (outerParameterizedExpr==node){ //Reached the outermost expression 
      endCandidates=null;
      outerParameterizedExpr=null;
    }
  }

  private void handleLoad(ParameterizedExpr node){
    if (! (node.getTarget() instanceof NameExpr)) return ;
    NameExpr target = (NameExpr)node.getTarget();
    if (target.getName().getID().equals( "load" ) ){
      ASTNode args = node.getChild(1);
      for (int i=1;i< args.getNumChild();i++)
        if( args.getChild( i ) instanceof StringLiteralExpr )  {
          String param = ( (StringLiteralExpr) args.getChild( i ) ).getValue();
          if (param.charAt(0)!='-'){
            putVar(param);
            annotateNode(target.getName());
          }
        }
    }
  }
  @Override public void caseParameterizedExpr( ParameterizedExpr node ) {
    handleLoad(node);
    caseMyLValue(node);
  }


  @Override
  public void caseEndExpr(EndExpr e) {
    if (pass == 0){
      withEndExpressions.add(outerParameterizedExpr);
      return ;
    }

    if (endCandidates == null || endCandidates.size() == 0 ){
      if (outerParameterizedExpr==null)
        System.err.println("Cannot bind end to anything");
      else {
        NameExpr res = new ArrayList<NameExpr>(getTarget(outerParameterizedExpr).getNameExpressions()).get(0);
        System.err.println("No candidates, making " +res.getName().getID() + " a TOP" );
        bindError(res, e);
      }
      return;
    }

    if ( endCandidates.size() == 1 ){
      bindWarn(endCandidates.get(0), e);
    }

    if (inFunction && endCandidates.size() > 1 ){
      bindError(endCandidates.get(0), e);
      System.err.println("More than one candidate, making " +endCandidates.get(0).getName().getID() + " a TOP" );
      return;
    }

    if ( (!inFunction) && endCandidates.size() > 1){
      NameExpr toBind = null;

      for (NameExpr n: endCandidates){
        if (!scriptOrFunctionExists(n.getName().getID())){
          if ( toBind==null )
            toBind=n;
          else{
            System.err.println("More than one candidate, making " + n.getName().getID() + " a TOP" );
            bindError(n, e);
          }
        }
      }            
      if (toBind == null){ // all cands exist in the lib{
        System.err.println("More than one candidate, making " +endCandidates.get(0).getName().getID() + " a TOP" );
        bindError(endCandidates.get(0), e);
      }
      else
        bindWarn(toBind, e);
    }
  }

  private void bindError(NameExpr n, EndExpr e){
    MergeUtil.mergePut(currentSet, n.getName().getID(), VFDatum.TOP);
    annotateNode(n.getName());
  }


  private void bindWarn(NameExpr n, EndExpr e){
    VFDatum d = currentSet.get(n.getName().getID());
    if (d != VFDatum.VAR)
      MergeUtil.mergePut(currentSet, n.getName().getID(), VFDatum.WAR);
    annotateNode(n.getName());
  }


  private boolean packageExists(String name) {
    return  lookupQuery.isPackage(name);
  }


  private void annotateNode(Name n) {
    flowSets.put(n, currentSet);
  }

  @Override
  public VFDatum getResult(Name n) {
    return flowSets.get(n).get(n.getID());
  }
}
