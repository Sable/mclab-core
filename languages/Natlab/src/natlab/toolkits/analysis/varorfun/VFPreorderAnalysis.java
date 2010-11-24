package natlab.toolkits.analysis.varorfun;
import java.util.*;
import ast.*;
import natlab.toolkits.analysis.*;
import natlab.*;
/** 
 * An implementation of a preorder analysis for the var or fun
 * analysis. Note this implementation uses the FunctionVFDatum and not
 * the ScriptVFDatum. This is because the preorder analysis only makes
 * sense in the function case, so a script version is not needed.
 */
public class VFPreorderAnalysis extends AbstractPreorderAnalysis< VFFlowset<String, FunctionVFDatum> >
{

    //TODO-JD add case for handling new RHS names that aren't involved
    //in an @ expression and correspond to a visible function. Visible
    //functions will have to be estimated of course. This is only for
    //the function context, not scripts.
    
    
    private boolean inFunction=true;
    private boolean endExpr=false;

    

    public VFPreorderAnalysis( ASTNode tree )
    {
        super( tree );

        currentSet = newInitialFlow();
    }
    
    public FunctionVFDatum newVariableDatum()
    {
        FunctionVFDatum d = new FunctionVFDatum();
        d.makeVariable();
        return d;
    }

    public FunctionVFDatum newBottomDatum()
    {
        FunctionVFDatum d = new FunctionVFDatum();
	d.makeBottom();
        return d;
    }

    public FunctionVFDatum newLDVar()
    {
        FunctionVFDatum d = new FunctionVFDatum();
        d.makeLDVar();
        return d;
    }
 
    public FunctionVFDatum newAssignedVariableDatum()
    {
        FunctionVFDatum d = new FunctionVFDatum();
        d.makeAssignedVariable();
        return d;
    }

    public FunctionVFDatum newFunctionDatum()
    {
        FunctionVFDatum d = new FunctionVFDatum();
        d.makeFunction();
        return d;
    }

    public VFPreorderAnalysis()
    {
        currentSet = newInitialFlow();
    }

    public VFFlowset<String, FunctionVFDatum> newInitialFlow()
    {
        return new VFFlowset();
    }

    public void caseCondition( Expr condExpr )
    {
        caseASTNode( condExpr );
    }

    public void caseFunctionList( FunctionList node )
    {
	LookupFile.setCurrentProgram(node);
        for( Function f : node.getFunctions() ){
            currentSet = newInitialFlow();
            f.analyze(this);
        }
    }

    public void caseScript( Script node )
    {
	currentSet = newInitialFlow();
	LookupFile.setCurrentProgram(node);
	inFunction=false;
	node.getStmts().analyze(this);
    }

    public void caseEndExpr( Script node ){
	endExpr=true;
    }


    public void caseFunction( Function node )
    {
	inFunction=true;
	currentSet = newInitialFlow();
        if(DEBUG)
            System.err.println("in caseFunction");
        // Add output params to set
        for( Name n : node.getOutputParams() ){
            currentSet.add( new ValueDatumPair(n.getID(), newVariableDatum() ) );
        }
        
        // Add input params to set
        for( Name n : node.getInputParams() ){
            currentSet.add( new ValueDatumPair(n.getID(), newVariableDatum() ) );
        }
        
        // Process body
        node.getStmts().analyze(this);
        

        
        // Prepare to process nested functions
        //TODO 
        
        //backup currentSet
        VFFlowset<String, FunctionVFDatum> myFlowSet = currentSet;
        //Create a set consisting solely of values with variable
        //datums.
        VFFlowset<String, FunctionVFDatum> initForNested = newInitialFlow();
        
        for( ValueDatumPair<String, FunctionVFDatum> pair : currentSet.toList() ){
            if( pair.getDatum().isVariable() )
                initForNested.add( pair.clone() );
        }
        
        // loop through the nested, using a fresh clone of the init
        // set as the currentSet
        for( Function f : node.getNestedFunctions() ){
            currentSet = initForNested.clone();
            f.analyze( this );
        }
        
        currentSet = myFlowSet;
        flowSets.put( node, currentSet );
    }

    public void caseAssignStmt( AssignStmt node )
    {
        //get the names from the lhs
        Expr lhs = node.getLHS();
	//analyze the rhs
        node.getRHS().analyze( this );

        for( String s : lhs.getSymbols() )
            currentSet.add( new ValueDatumPair( s, newVariableDatum() ) );

	node.getLHS().analyze( this );
        
    }

    public void caseGlobalStmt( GlobalStmt node )
    {
        for( Name n : node.getNames() ){
            currentSet.add( new ValueDatumPair( n.getID(), newVariableDatum() ) );
	}
    }

    public void casePersistentStmt( PersistentStmt node )
    {
        for( Name n : node.getNames() ){
            currentSet.add( new ValueDatumPair( n.getID(), newVariableDatum() ) );
	}
    }

    public void caseFunctionHandleExpr( FunctionHandleExpr node )
    {
        currentSet.add( new ValueDatumPair( node.getName().getID(), newFunctionDatum() ) );
    }

    public void caseParameterizedExpr( ParameterizedExpr node )
    {
	ast.List args = node.getArgList();
	args.analyze( this );
 
	if ( node.getTarget() instanceof NameExpr)
            { 
	    String targetName=( (NameExpr) node.getTarget() ).getName().getID();	    

	    /* LOAD command */
	    if ( targetName.equals( "load" ) ) 
            {
		if( node.getChild( 1 ).getNumChild()==2 && 
		   ( node.getChild( 1 ).getChild( 1 ) instanceof StringLiteralExpr ) )
                {
		    String param = ( (StringLiteralExpr) node.getChild( 1 ).getChild( 1 ) ).getValue();
		    if (param.charAt(0)!='-')
			currentSet.add( new ValueDatumPair( param  , newLDVar() ) );
		}
	    }

	    /* END Expression */
	    if (endExpr){
		VFDatum d =  currentSet.contains( targetName );
		if ( d==null )
		{
		    d = newLDVar();
		    d.makeTop();
		    currentSet.add( new ValueDatumPair( targetName, d ) );
		}

		if (VFDatum.Value.BOT.equals( d ) || VFDatum.Value.LDVAR.equals( d ) || 
		    VFDatum.Value.VAR.equals( d ) )
		{
			endExpr = false;
			currentSet.add( new ValueDatumPair( targetName, newVariableDatum() ) );
		}
	    }
	}
	node.getTarget().analyze( this );
    }
    
    public void caseNameExpr( NameExpr node )
    {
	if ( inFunction )
        {
	    String s = node.getName().getID();
	    VFDatum d = currentSet.contains( s );
	    if ( s!=null && d==null || VFDatum.Value.BOT.equals( d ) )
            {
		if ( natlab.LookupFile.scriptOrFunctionExists( s ) ) 
                {
		    currentSet.add( new ValueDatumPair( s, newFunctionDatum() ) );
		}
		else 
		    currentSet.add( new ValueDatumPair( s, newBottomDatum() ) );
	    }
	    flowSets.put( node, currentSet );	
	}
	else{
	    String s = node.getName().getID();
	    VFDatum d = currentSet.contains( s );		    
	    if ( d==null || VFDatum.Value.BOT.equals(d) )
            {
		currentSet.add( new ValueDatumPair( s, newLDVar() ) );
	    }
	    flowSets.put( node, currentSet.clone() );	
	}
    }
}

