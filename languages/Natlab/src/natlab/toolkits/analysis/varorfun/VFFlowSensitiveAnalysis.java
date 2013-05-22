package natlab.toolkits.analysis.varorfun;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import natlab.LookupFile;
import natlab.toolkits.filehandling.FunctionOrScriptQuery;
import analysis.AbstractStructuralForwardAnalysis;
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

public class VFFlowSensitiveAnalysis extends AbstractStructuralForwardAnalysis<VFFlowset> implements VFAnalysis{
  private Function currentFunction = null;
  private Script currentScript = null;
  private FunctionOrScriptQuery lookupQuery = null;
  private LValueExpr outerParameterizedExpr=null;
  private boolean inFunction=false;
  private java.util.List<NameExpr> endCandidates = null;

  @Override
  public void caseCondition(Expr condExpr) {
    if (condExpr instanceof NameExpr)
      caseNameExpr((NameExpr) condExpr);
    if (condExpr instanceof ParameterizedExpr)
      caseParameterizedExpr((ParameterizedExpr) condExpr);
    if (condExpr instanceof CellIndexExpr)
      caseCellIndexExpr((CellIndexExpr) condExpr);
    else
      caseASTNode( condExpr );	
  }

  @Override
  public VFFlowset newInitialFlow() {
    return new VFFlowset();
  }
  public VFFlowSensitiveAnalysis( ASTNode tree , FunctionOrScriptQuery lookup )
  {
    super( tree );
    lookupQuery = lookup;
  }

  @Deprecated
  public VFFlowSensitiveAnalysis( ASTNode tree )
  {
    this(tree,LookupFile.getFunctionOrScriptQueryObject());
  }

  private void putInVar(String name) {
    currentInSet.add(ValueDatumPair.create(name, VFDatum.VAR));
  }

  private void putOutVar(String name) {
    currentOutSet.add(ValueDatumPair.create(name, VFDatum.VAR));
  }

  public void caseScript( Script node )
  {
    inFunction=false;
    currentScript=node;
    currentInSet = newInitialFlow();
    currentOutSet = currentInSet;
    node.getStmts().analyze(this);
    currentScript=null;
    outFlowSets.put( node, currentOutSet.copy() );
  }

  @Override
  public void caseCellIndexExpr(CellIndexExpr node){
    NameExpr n = new ArrayList<NameExpr>(((CellIndexExpr) node).getTarget().getNameExpressions()).get(0);
    putOutVar(n.getName().getID());
    annotateNode(n.getName());
    caseMyLValue(node);
  }

  @Override
  public void caseFunction( Function node )
  {
    currentFunction=node;
    inFunction=true;
    currentInSet = newInitialFlow();
    currentOutSet = currentInSet;
    if (node.getParent().getParent() instanceof Function){
      for( Map.Entry<String, VFDatum> pair : outFlowSets.get(node.getParent().getParent())) {
        if( pair.getValue()==VFDatum.VAR  || pair.getValue()==VFDatum.BOT)
          currentInSet.add( pair );
      }        	
    }

    for( Name n : node.getOutputParams() ){
      putInVar(n.getID());
    }

    for( Name n : node.getInputParams() ){
      putInVar(n.getID());
    }

    node.getStmts().analyze(this);

    //HashSet<ASTNode> toPatch= new HashSet<ASTNode>(outFlowSets.keySet());
    //backup currentSet
    outFlowSets.put( node, currentOutSet );

    for( Function f : node.getNestedFunctions() ){
      f.analyze( this );
    }
    inFunction=false;
    //        for (ASTNode n: toPatch){
    //        	outFlowSets.put(n, myFlowSet);
    //        }
    currentFunction = null;
  }

  private boolean scriptOrFunctionExists(String name){
    return 
        ((currentFunction != null) && (currentFunction.lookupFunction(name) != null))
        || ((currentScript != null) && (false)) //TODO - should return if name is the name of the script
        || lookupQuery.isFunctionOrScript(name);
  }
  private boolean packageExists(String name){
    return lookupQuery.isPackage(name);
  }

  @Override
  public void caseAssignStmt( AssignStmt node )
  {
    node.getRHS().analyze( this );
    for( NameExpr n : node.getLHS().getNameExpressions() ){
      putOutVar(n.getName().getID());
      annotateNode(n.getName());
    }
    outFlowSets.put(node, currentOutSet);
    node.getLHS().analyze( this );
  }

  @Override
  public void caseGlobalStmt( GlobalStmt node )
  {
    for( Name n : node.getNames() ){
      putOutVar(n.getID());
      annotateNode(n);
    }
  }

  @Override
  public void casePersistentStmt( PersistentStmt node )
  {
    for( Name n : node.getNames() ){
      putOutVar(n.getID());
      annotateNode(n);
    }
  }

  @Override
  public void caseFunctionHandleExpr( FunctionHandleExpr node )
  {
    currentOutSet.add(ValueDatumPair.create(node.getName().getID(), VFDatum.FUN ));
    annotateNode(node.getName());
  }

  @Override
  public void caseNameExpr( NameExpr node )
  {
    currentOutSet = currentInSet;
    if ( inFunction )
    {
      String s = node.getName().getID();
      VFDatum d = currentOutSet.contains( s );
      if ( s!=null && d==null || VFDatum.BOT.equals( d ) )
      {
        if ( scriptOrFunctionExists( s ) ) 
          currentOutSet.add(ValueDatumPair.create( s, VFDatum.FUN ) );
        else if ( packageExists( s ) ) 
          currentOutSet.add(ValueDatumPair.create( s, VFDatum.PREFIX) );
        else 
          currentOutSet.add(ValueDatumPair.create( s, VFDatum.BOT ) );
      }
    }
    else{
      String s = node.getName().getID();
      VFDatum d = currentOutSet.contains( s );		    
      if ( d==null || VFDatum.BOT.equals(d) )
      {
        currentOutSet.add(ValueDatumPair.create( s, VFDatum.LDVAR ) );
      }
    }
    annotateNode(node.getName());
  }

  public void caseLambdaExpr(LambdaExpr node){/*
    	VFFlowset backup = currentOutSet.copy();
    	Set<String> inputSet=new TreeSet<String>();
    	for (Name inputParam: node.getInputParams()){
    		currentOutSet.add(new ValueDatumPair<String,VFDatum>(inputParam.getID(),VFDatum.VAR));
    		inputSet.add(inputParam.getID());
    	}
    	caseASTNode(node);
    	for (Map.Entry<String, VFDatum> p:currentOutSet.getMap().entrySet()){
    		if (! inputSet.contains(p.getKey()))
    			backup.add(new ValueDatumPair<String, VFDatum>(p.getKey(),p.getValue()));
    	}
    	currentOutSet=backup;*/
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

    target.analyze( this );


    VFDatum d =  currentOutSet.contains( targetName ); 
    if ( d == null || d == VFDatum.BOT || VFDatum.LDVAR == d || d == VFDatum.WAR || d == VFDatum.TOP ) { 
      endCandidates.add(res);
    }
    if (d == VFDatum.VAR){
      endCandidates = new ArrayList<NameExpr>();
      endCandidates.add(res);
    }

    args.analyze( this );


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
            putOutVar(param);
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
    currentOutSet.add(n.getName().getID(), VFDatum.TOP);
    annotateNode(n.getName());
  }

  private void bindWarn(NameExpr n, EndExpr e){
    VFDatum d = currentOutSet.contains(n.getName().getID());
    if (d != VFDatum.VAR)
      currentOutSet.add(n.getName().getID(), VFDatum.WAR);
    annotateNode(n.getName());
  }

  private void annotateNode(Name n){
    outFlowSets.put(n, currentOutSet);
  }
  @Override
  public VFDatum getResult(Name n) {
    return outFlowSets.get(n).contains(n.getID());
  }

  @Override
  public VFFlowset processBreaks() {
    return currentOutSet;
  }

  @Override
  public VFFlowset processContinues() {
    return currentOutSet;
  }


  @Override
  public void caseLoopVar(AssignStmt loopVar) {
    loopVar.analyze(this);
  }

  @Override
  public void caseLoopVarAsCondition(AssignStmt loopVar) {
    caseLoopVar( loopVar );
  }

  @Override
  public void caseLoopVarAsInit(AssignStmt loopVar) {
    caseLoopVar( loopVar );
  }

  @Override
  public void caseLoopVarAsUpdate(AssignStmt loopVar) {
    caseLoopVar( loopVar );
  }

  @Override
  public VFFlowset copy(VFFlowset source) {
    return source.copy();
  }

  @Override
  public VFFlowset merge(VFFlowset in1, VFFlowset in2) {
    VFFlowset out = new VFFlowset();
    in1.union(in2,out);
    return out;
  }
}
