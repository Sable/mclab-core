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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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

import com.google.common.collect.Maps;

/** 
 * An implementation of a preorder analysis for the var or fun
 * analysis. 
 * 
 */
public class VFPreorderAnalysis extends AbstractDepthFirstAnalysis<Map<String, VFDatum>> implements VFAnalysis
{
    private boolean inFunction=true;
    private Function currentFunction = null;
    private Script currentScript = null;
    private FunctionOrScriptQuery lookupQuery = null;
    private LValueExpr outerParameterizedExpr=null;
    private List<NameExpr> endCandidates=null;
    private boolean boundEndExprToID=false;
    
    
    
    /**
     * initializes the VFPreorderAnalysis using LookupFile.getFunctionOrScriptQueryObject().
     * This is deprecated, because using this lookupFile should be made explicit.
     * With this function, an environment is assumed that may not be intended by the user.
     * @param tree
     */
    public VFPreorderAnalysis( ASTNode tree )
    {
        this(tree,LookupFile.getFunctionOrScriptQueryObject());
    }

    public VFPreorderAnalysis( ASTNode tree , FunctionOrScriptQuery lookup )
    {
        super( tree );
        lookupQuery = lookup;
        currentSet = newInitialFlow();
    }
    
    @Deprecated
    public static VFAnalysis analyzeTree(ASTNode n){
    	return new VFPreorderAnalysis(n);
    }
    
   	public static VFAnalysis analyzeTree(ASTNode n, FunctionOrScriptQuery lookup){
   		return new VFPreorderAnalysis(n, lookup);
   	}
    public Map<String, VFDatum> newInitialFlow()
    {
        return Maps.newHashMap();
    }

    public void caseCondition( Expr condExpr )
    {
        caseASTNode( condExpr );
    }

    public void caseFunctionList( FunctionList node )
    {
        for( Function f : node.getFunctions() ){
            currentSet = newInitialFlow();
            f.analyze(this);
        }
    }

    public void caseScript( Script node )
    {
        currentScript=node;
        currentSet = newInitialFlow();
        inFunction=false;
        node.getStmts().analyze(this);
        flowSets.put(node, currentSet);
        currentScript=null;
    }


    public void caseFunction( Function node )
    {
        inFunction=true;
        currentFunction=node;
        currentSet = newInitialFlow();
        if (node.getParent() != null && node.getParent().getParent() != null && node.getParent().getParent() instanceof Function){
            for( Map.Entry<String, VFDatum> pair : flowSets.get(node.getParent().getParent()).entrySet()) {
                if( pair.getValue()==VFDatum.VAR  || pair.getValue()==VFDatum.BOT)
                    MergeUtil.mergePut(currentSet, pair.getKey(), pair.getValue());
            }        	
        }

        log("in caseFunction " + node.getName());
        log(currentSet);

        // Add output params to set
        for( Name n : node.getOutputParams() ){
            MergeUtil.mergePut(currentSet, n.getID(), VFDatum.VAR);
        }

        // Add input params to set
        for( Name n : node.getInputParams() ){
            MergeUtil.mergePut(currentSet, n.getID(), VFDatum.VAR);
        }
        
        // Process body
        node.getStmts().analyze(this);

        flowSets.put( node, currentSet );
        for( Function f : node.getNestedFunctions() ){
            f.analyze( this );
        }
        currentSet=flowSets.get(node);
        currentFunction = null;
    }

    public void caseAssignStmt( AssignStmt node )
    {
        //get the names from the lhs
        Expr lhs = node.getLHS();
        //analyze the rhs
        node.getRHS().analyze( this );
        for( NameExpr n : lhs.getNameExpressions() ){
            MergeUtil.mergePut(currentSet, n.getName().getID(), VFDatum.VAR);
            annotateNode(n.getName());
        }
        node.getLHS().analyze( this );
    }

    public void caseGlobalStmt( GlobalStmt node )
    {
        for( Name n : node.getNames() ){
            MergeUtil.mergePut(currentSet, n.getID(), VFDatum.VAR);
            annotateNode(n);
        }
    }

    public void casePersistentStmt( PersistentStmt node )
    {
        for( Name n : node.getNames() ){
            MergeUtil.mergePut(currentSet, n.getID(), VFDatum.VAR);
            annotateNode(n);
        }
    }

    public void caseFunctionHandleExpr( FunctionHandleExpr node )
    {
        MergeUtil.mergePut(currentSet, node.getName().getID(), VFDatum.FUN);
        annotateNode(node.getName());        
    }

   private ASTNode getTarget(LValueExpr node){
    	ASTNode target = null;
    	if (node instanceof CellIndexExpr)
            target = ((CellIndexExpr) node).getTarget();
    	else if (node instanceof ParameterizedExpr)
            target = ((ParameterizedExpr) node).getTarget();
    	else
            log("in LValue without any target");
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
        
        VFDatum d =  currentSet.get( targetName ); 
        if ( d == null || d == VFDatum.BOT || VFDatum.LDVAR == d || d == VFDatum.TOP ) { 
            if (endCandidates == null ) System.out.println ("NULL");
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
                        MergeUtil.mergePut(currentSet, param, VFDatum.LDVAR);
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
                log("Cannot bind end to anything");
            else {
                NameExpr res = new ArrayList<NameExpr>(getTarget(outerParameterizedExpr).getNameExpressions()).get(0);
                log("No candidates, making " +res.getName().getID() + " a TOP" );
                bindError(res, e);
            }
            return;
        }

        if ( endCandidates.size() == 1 ){
            bindWarn(endCandidates.get(0), e);
        }

        if (inFunction && endCandidates.size() > 1 ){
            bindError(endCandidates.get(0), e);
            log("More than one candidate, making " +endCandidates.get(0).getName().getID() + " a TOP, Cands are :");
            for (NameExpr n: endCandidates)
                log("    " +n.getName().getID());
            return;
        }
        
        if ( (!inFunction) && endCandidates.size() > 1){
            NameExpr toBind = null;
            
            for (NameExpr n: endCandidates){
                if (!scriptOrFunctionExists(n.getName().getID())){
                    if ( toBind==null )
                        toBind=n;
                    else{
                        log("More than one candidate, making " + n.getName().getID() + " a TOP" );
                        bindError(n, e);
                    }
                }
            }            
            if (toBind == null){ // all cands exist in the lib
                bindWarn(endCandidates.get(0), e);
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
            MergeUtil.mergePut(currentSet, n.getName().getID(), VFDatum.VAR);
        annotateNode(n.getName());
    }
    
    public void caseCellIndexExpr(CellIndexExpr node){
        NameExpr n = new ArrayList<NameExpr>(((CellIndexExpr) node).getTarget().getNameExpressions()).get(0);
        MergeUtil.mergePut(currentSet, n.getName().getID(), VFDatum.VAR);
        annotateNode(n.getName());
    	caseMyLValue(node);
    }

    public void caseLambdaExpr(LambdaExpr node){
    	Map<String, VFDatum> backup = Maps.newHashMap(currentSet);
    	Set<String> inputSet=new TreeSet<String>();
    	for (Name inputParam: node.getInputParams()){
    	    MergeUtil.mergePut(currentSet, inputParam.getID(), VFDatum.VAR);
    		inputSet.add(inputParam.getID());
    	}
    	caseASTNode(node);
    	for (Map.Entry<String, VFDatum> p:currentSet.entrySet()){
    		if (! inputSet.contains(p.getKey()))
    		    MergeUtil.mergePut(backup, p.getKey(), p.getValue());
    	}
    	currentSet=backup;
    }
    
    
    public void caseNameExpr( NameExpr node )
    {
        if ( inFunction )
        {
            String s = node.getName().getID();
            VFDatum d = currentSet.get( s );
            if ( s!=null && (d==null || VFDatum.BOT.equals( d ) ))
            {
              VFDatum val = scriptOrFunctionExists(s) ? VFDatum.FUN
                  : packageExists(s) ? VFDatum.PREFIX : VFDatum.BOT;
              MergeUtil.mergePut(currentSet, s, val);
            }
        }
        else{
            String s = node.getName().getID();
            VFDatum d = currentSet.get( s );		    
            if ( d==null || VFDatum.BOT.equals(d) )
            {
                MergeUtil.mergePut(currentSet, s, VFDatum.LDVAR);
            }
        }
        annotateNode(node.getName());
    }
    
    private boolean scriptOrFunctionExists(String name){
        return 
        (currentFunction != null && currentFunction.lookupFunction(name) != null)
        || (currentScript != null && name.equals(currentScript.getName()))
        || lookupQuery.isFunctionOrScript(name);
    }
    
    private boolean packageExists(String name){
    	return  lookupQuery.isPackage(name);
    }

    
    private void annotateNode(Name n){
        if (inFunction)
        	flowSets.put(n, currentSet);
        else
        	flowSets.put(n, Maps.newHashMap(currentSet));
    }
    @Override
	public VFDatum getResult(Name n) {
		return flowSets.get(n).get(n.getID());
	}
    
    
    /**
     * returns the function/script query object used internally
     */
    public FunctionOrScriptQuery getQuery(){
        return lookupQuery;
    }
}
