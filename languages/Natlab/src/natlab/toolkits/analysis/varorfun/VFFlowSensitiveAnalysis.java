package natlab.toolkits.analysis.varorfun;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

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
import natlab.LookupFile;
import natlab.toolkits.analysis.AbstractStructuralForwardAnalysis;
import natlab.toolkits.filehandling.FunctionOrScriptQuery;

public class VFFlowSensitiveAnalysis extends AbstractStructuralForwardAnalysis<VFFlowset> implements VFAnalysis{
    private Function currentFunction = null;
    private Script currentScript = null;
    private FunctionOrScriptQuery lookupQuery = null;
    private boolean endExpr=false;
    private boolean boundEndExprToID=false;
    private ASTNode outerParameterizedExpr=null;
    private boolean inFunction=false;
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

    
    public void caseScript( Script node )
    {
        inFunction=false;
    	currentScript=node;
        currentInSet = newInitialFlow();
        currentOutSet = currentInSet;
        node.getStmts().analyze(this);
        currentScript=null;
        outFlowSets.put( node, currentOutSet.clone() );
    }
    
    public void caseEndExpr( EndExpr node ){
        endExpr=true;
    }
    
    @Override
    public void caseCellIndexExpr(CellIndexExpr node){
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
            for( ValueDatumPair<String, VFDatum> pair : outFlowSets.get(node.getParent().getParent()).toList() ){
                if( pair.getDatum()==VFDatum.VAR  || pair.getDatum()==VFDatum.BOT)
                    currentInSet.add( pair.clone() );
            }        	
        }
        if(DEBUG){
        	System.err.println("in caseFunction "+ node.getName());
    	}
        // Add output params to set
        for( Name n : node.getOutputParams() ){
            currentInSet.add( new ValueDatumPair(n.getID(), VFDatum.VAR ) );
        }

        // Add input params to set
        for( Name n : node.getInputParams() ){
            currentInSet.add( new ValueDatumPair(n.getID(), VFDatum.VAR ) );
        }
        
        // Process body
        node.getStmts().analyze(this);
        if(DEBUG){
        	System.err.println("in caseFunction "+ node.getName());
        	System.err.println(currentOutSet);
    	}
        //HashSet<ASTNode> toPatch= new HashSet<ASTNode>(outFlowSets.keySet());
        //backup currentSet
        VFFlowset myFlowSet = currentOutSet;
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
    	
        //get the names from the lhs
        Expr lhs = node.getLHS();
        //analyze the rhs
        node.getRHS().analyze( this );
        for( NameExpr n : lhs.getNameExpressions() ){
            currentOutSet.add( new ValueDatumPair( n.getName().getID(), VFDatum.VAR ) );
            annotateNode(n.getName());
        }
        outFlowSets.put(node, currentOutSet);
        node.getLHS().analyze( this );
    }

    @Override
    public void caseGlobalStmt( GlobalStmt node )
    {
        for( Name n : node.getNames() ){
            currentOutSet.add( new ValueDatumPair( n.getID(), VFDatum.VAR ) );
            annotateNode(n);
        }
    }

    @Override
    public void casePersistentStmt( PersistentStmt node )
    {
    	for( Name n : node.getNames() ){
            currentOutSet.add( new ValueDatumPair( n.getID(), VFDatum.VAR ) );
            annotateNode(n);
        }
    }

    @Override
    public void caseFunctionHandleExpr( FunctionHandleExpr node )
    {
    	currentOutSet.add( new ValueDatumPair( node.getName().getID(), VFDatum.FUN ) );
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
                    currentOutSet.add( new ValueDatumPair( s, VFDatum.FUN ) );
            	else if ( packageExists( s ) ) 
            		currentOutSet.add( new ValueDatumPair( s, VFDatum.PREFIX) );
                else 
                    currentOutSet.add( new ValueDatumPair( s, VFDatum.BOT ) );
            }
        }
        else{
            String s = node.getName().getID();
            VFDatum d = currentOutSet.contains( s );		    
            if ( d==null || VFDatum.BOT.equals(d) )
            {
                currentOutSet.add( new ValueDatumPair( s, VFDatum.LDVAR ) );
            }
        }
        annotateNode(node.getName());
    }
    
    public void caseLambdaExpr(LambdaExpr node){}
    
    @Override
    public void caseParameterizedExpr( ParameterizedExpr node )
    {
    	caseMyLValue(node);
    }

    public void caseMyLValue(LValueExpr node){
    	VFFlowset currentSet= currentOutSet;
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
//    	System.out.println(res.getName().getID()+" EndExpr: "+endExpr+ " boundToID: " + boundEndExprToID + " backup: "+endExprBackup);
//    	System.out.println(res.getName().getID()+" before: "+ currentSet);
	    String targetName=res.getName().getID();
	    if (outerParameterizedExpr==null){
	    	outerParameterizedExpr=node;
	    	endExpr=false;
	    	endExprBackup=false;
	    	boundEndExprToID=false;
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
	    				currentSet.add( new ValueDatumPair( param  , VFDatum.VAR ) );
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
//	    System.out.println(res.getName().getID()+"EndExpr: "+endExpr+ " boundToID: " + boundEndExprToID + " backup: "+endExprBackup);
	    if (endExpr){
	        VFDatum d =  currentSet.contains( targetName );
	        if (d == null || VFDatum.BOT.equals(d)||(d==VFDatum.LDVAR && inFunction))
	        {
	            endExpr = false;
	            if (inFunction)
	            boundEndExprToID=true;
	            currentSet.add( new ValueDatumPair( targetName, VFDatum.WAR) );
	            annotateNode(res.getName());
	        }
	        else if (VFDatum.VAR.equals(d)||VFDatum.WAR==d||(d==VFDatum.LDVAR && !inFunction))
	        {
	            endExpr = false;
	            currentSet.add( new ValueDatumPair( targetName, VFDatum.VAR) );
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
//	    System.out.println(res.getName().getID()+" after: "+ currentSet);
//	    System.out.println(res.getName().getID()+"EndExpr: "+endExpr+ " boundToID: " + boundEndExprToID + " backup: "+endExprBackup);
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
	public void caseLoopVarAsCondition(AssignStmt loopVar) {
		loopVar.analyze(this);
	}

	@Override
	public void caseLoopVarAsInit(AssignStmt loopVar) {
		loopVar.analyze(this);;
	}

	@Override
	public void caseLoopVarAsUpdate(AssignStmt loopVar) {
		loopVar.analyze(this);
	}

	@Override
	public void copy(VFFlowset source, VFFlowset dest) {
		source.copy(dest);		
	}

	@Override
	public void merge(VFFlowset in1, VFFlowset in2, VFFlowset out) {
		in1.union(in2,out);
	}
}
