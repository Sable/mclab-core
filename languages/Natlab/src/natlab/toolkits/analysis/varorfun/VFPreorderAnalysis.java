package natlab.toolkits.analysis.varorfun;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BoundedRangeModel;

import ast.*;
import natlab.toolkits.analysis.*;
import natlab.toolkits.filehandling.FunctionOrScriptQuery;
import natlab.*;

/** 
 * An implementation of a preorder analysis for the var or fun
 * analysis. 
 * 
 */
public class VFPreorderAnalysis extends AbstractPreorderAnalysis< VFFlowset > implements VFAnalysis
{
    private boolean inFunction=true;
    private Function currentFunction = null;
    private Script currentScript = null;
    private FunctionOrScriptQuery lookupQuery = null;
    private boolean endExpr=false;
    private ASTNode outerParameterizedExpr=null;
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
    public VFFlowset newInitialFlow()
    {
        return new VFFlowset();
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
        currentScript=null;
    }

    public void caseEndExpr( EndExpr node ){
        endExpr=true;
    }


    public void caseFunction( Function node )
    {
        inFunction=true;
        currentFunction=node;
        currentSet = newInitialFlow();
        if (node.getParent().getParent() instanceof Function){
            for( ValueDatumPair<String, VFDatum> pair : flowSets.get(node.getParent().getParent()).toList() ){
                if( pair.getDatum()==VFDatum.VAR  || pair.getDatum()==VFDatum.BOT)
                    currentSet.add( pair.clone() );
            }        	
        }
        if(DEBUG){
        	System.err.println("in caseFunction "+ node.getName());
        	System.err.println(currentSet);
    	}
        // Add output params to set
        for( Name n : node.getOutputParams() ){
            currentSet.add( new ValueDatumPair(n.getID(), VFDatum.VAR ) );
        }

        // Add input params to set
        for( Name n : node.getInputParams() ){
            currentSet.add( new ValueDatumPair(n.getID(), VFDatum.VAR ) );
        }
        
        // Process body
        node.getStmts().analyze(this);

        //backup currentSet
        VFFlowset myFlowSet = currentSet;
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
            currentSet.add( new ValueDatumPair( n.getName().getID(), VFDatum.VAR ) );
            annotateNode(n.getName());
        }
        node.getLHS().analyze( this );
    }

    public void caseGlobalStmt( GlobalStmt node )
    {
        for( Name n : node.getNames() ){
            currentSet.add( new ValueDatumPair( n.getID(), VFDatum.VAR ) );
            annotateNode(n);
        }
    }

    public void casePersistentStmt( PersistentStmt node )
    {
        for( Name n : node.getNames() ){
            currentSet.add( new ValueDatumPair( n.getID(), VFDatum.VAR ) );
            annotateNode(n);
        }
    }

    public void caseFunctionHandleExpr( FunctionHandleExpr node )
    {
        currentSet.add( new ValueDatumPair( node.getName().getID(), VFDatum.FUN ) );
        annotateNode(node.getName());        
    }
    
    public void caseMyLValue(LValueExpr node){
    	ASTNode target = null;
    	boolean endExprBackup=endExpr;
    	endExpr=false;
    	if (node instanceof CellIndexExpr)
    		target = ((CellIndexExpr) node).getTarget();
    	else if (node instanceof ParameterizedExpr)
    		target = ((ParameterizedExpr) node).getTarget();
    	else
    		System.err.println("in LValue without any target");
    	
    	NameExpr res = new ArrayList<NameExpr>(target.getNameExpressions()).get(0);
    	
	    String targetName=res.getName().getID();
	    if (outerParameterizedExpr==null){
	    	outerParameterizedExpr=node;
	    	boundEndExprToID=false;
	    	endExprBackup=false;
	    }

    	ASTNode args = null;
    	if (node instanceof CellIndexExpr)
    		args = ((CellIndexExpr) node).getArgs();
    	if (node instanceof ParameterizedExpr)
    		args = ((ParameterizedExpr) node).getArgs();

	    args.analyze( this );
	    if (node instanceof ParameterizedExpr && target instanceof NameExpr && targetName.equals( "load" ) ) 
	    {
	    	for (int i=1;i<node.getChild(1).getNumChild();i++){
	    		if( node.getChild( 1 ).getChild( i ) instanceof StringLiteralExpr ) 
	    		{		
	    			String param = ( (StringLiteralExpr) node.getChild( 1 ).getChild( i ) ).getValue();
	    			if (param.charAt(0)!='-'){
	    				currentSet.add( new ValueDatumPair( param  , VFDatum.LDVAR ) );
	    				annotateNode(res.getName());
	    			}
	    		}
	    	}
	    }
	    target.analyze( this );

	    /* END Expression */
	    if (boundEndExprToID){
	    	VFDatum d =  currentSet.contains( targetName );
	    	if (d==VFDatum.VAR || d==VFDatum.BOT||d==null )
	        {
	            endExpr = false;
	            currentSet.add( new ValueDatumPair( targetName, VFDatum.TOP) );
	            annotateNode(res.getName());
	        }
	    	boundEndExprToID=false;
	    }
			
	    if (endExpr){
	        VFDatum d =  currentSet.contains( targetName );
	        if (d == null || VFDatum.BOT.equals(d)||d==VFDatum.LDVAR  )
	        {
	            endExpr = false;
	            boundEndExprToID=true;
	            currentSet.add( new ValueDatumPair( targetName, VFDatum.VAR) );
	            annotateNode(res.getName());
	        }
	        if (VFDatum.VAR.equals(d) )
	        {
	            endExpr = false;
	            currentSet.add( new ValueDatumPair( targetName, VFDatum.VAR ) );
	            annotateNode(res.getName());
	        }
	    }
		endExpr|=endExprBackup;
	    if (outerParameterizedExpr==node){
	    	if (endExpr){
	    		currentSet.add( new ValueDatumPair( targetName, VFDatum.TOP ) );
	    		annotateNode(res.getName());
	    		endExpr=false;
	    		boundEndExprToID=false;
	    	}
	    	outerParameterizedExpr=null;
	    }    	
    }
    public void caseCellIndexExpr(CellIndexExpr node){
    	caseMyLValue(node);
    }
    public void caseLambdaExpr(LambdaExpr node){
    	VFFlowset backup = currentSet.clone();
    	Set<String> inputSet=new TreeSet<String>();
    	for (Name inputParam: node.getInputParams()){
    		currentSet.add(new ValueDatumPair(inputParam.getID(),VFDatum.VAR));
    		inputSet.add(inputParam.getID());
    	}
    	caseASTNode(node);
    	for (Entry<String, VFDatum> p:currentSet.getMap().entrySet()){
    		if (! inputSet.contains(p.getKey()))
    			backup.add(new ValueDatumPair<String, VFDatum>(p.getKey(),p.getValue()));
    	}
    	currentSet=backup;
    }
    
    public void caseParameterizedExpr( ParameterizedExpr node )
    {
    	caseMyLValue(node);
    }

	private void bindEndExpr(NameExpr res) {
		String targetName=res.getName().getID();
		/* END Expression */
		if (boundEndExprToID){
            currentSet.add( new ValueDatumPair( targetName, VFDatum.TOP) );
            annotateNode(res.getName());
            boundEndExprToID=false;
		}
        if (endExpr){
            VFDatum d =  currentSet.contains( targetName );
            if (d == null || VFDatum.BOT.equals(d) || VFDatum.LDVAR.equals( d))
            {
                endExpr = false;
                currentSet.add( new ValueDatumPair( targetName, VFDatum.VAR ) );
                annotateNode(res.getName());
                boundEndExprToID=false;
            }
        }
	}

	private void applyLoad(ParameterizedExpr node, String targetName) {
		if (node.getTarget() instanceof NameExpr && targetName.equals( "load" ) ) 
        {
            if( node.getChild( 1 ).getNumChild()==2 && 
                    ( node.getChild( 1 ).getChild( 1 ) instanceof StringLiteralExpr ) )
            {
                String param = ( (StringLiteralExpr) node.getChild( 1 ).getChild( 1 ) ).getValue();
                if (param.charAt(0)!='-')
                    currentSet.add( new ValueDatumPair( param  , VFDatum.LDVAR ) );
            }
        }
        }
    
    public void caseNameExpr( NameExpr node )
    {
        if ( inFunction )
        {
            String s = node.getName().getID();
            VFDatum d = currentSet.contains( s );
            if ( s!=null && (d==null || VFDatum.BOT.equals( d ) ))
            {
            	if ( scriptOrFunctionExists( s ) ) 
                    currentSet.add( new ValueDatumPair( s, VFDatum.FUN ) );
            	else if ( packageExists( s ) ) 
                   currentSet.add( new ValueDatumPair( s, VFDatum.PREFIX) );
                else 
                    currentSet.add( new ValueDatumPair( s, VFDatum.BOT ) );
            }
        }
        else{
            String s = node.getName().getID();
            VFDatum d = currentSet.contains( s );		    
            if ( d==null || VFDatum.BOT.equals(d) )
            {
                currentSet.add( new ValueDatumPair( s, VFDatum.LDVAR ) );
            }
        }
        annotateNode(node.getName());
    }
    
    private boolean scriptOrFunctionExists(String name){
        return 
        ((currentFunction != null) && (currentFunction.lookupFunction(name) != null))
        || ((currentScript != null) && (false)) //TODO - should return if name is the name of the script
        || lookupQuery.isFunctionOrScript(name);
    }
    
    private boolean packageExists(String name){
    	return  lookupQuery.isPackage(name);
    }

    
    private void annotateNode(Name n){
        if (inFunction)
        	flowSets.put(n, currentSet);
        else
        	flowSets.put(n, currentSet.clone());
    }
    @Override
	public VFDatum getResult(Name n) {
		return flowSets.get(n).contains(n.getID());
	}
}
