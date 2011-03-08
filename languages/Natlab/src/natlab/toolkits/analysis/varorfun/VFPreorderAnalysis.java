package natlab.toolkits.analysis.varorfun;
import java.util.*;
import ast.*;
import natlab.toolkits.analysis.*;
import natlab.toolkits.filehandling.FunctionOrScriptQuery;
import natlab.*;

/** 
 * An implementation of a preorder analysis for the var or fun
 * analysis. 
 * 
 */
public class VFPreorderAnalysis extends AbstractPreorderAnalysis< VFFlowset >
{
    private boolean inFunction=true;
    private boolean endExpr=false;
    private Function currentFunction = null;
    private Script currentScript = null;
    private FunctionOrScriptQuery lookupQuery = null;


    /**
     * initializes the VFPreorderAnalysis using LookupFile.getFunctionOrScriptQueryObject().
     * This is deprecated, because using this lookupFile should be made explicit.
     * With this function, an environment is assumed that may not be intended by the user.
     * @param tree
     */
    @Deprecated
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
        if(DEBUG)
            System.err.println("in caseFunction");
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



        // Prepare to process nested functions
        //TODO 

        //backup currentSet
        VFFlowset myFlowSet = currentSet;
        //Create a set consisting solely of values with variable
        //datums.
        VFFlowset initForNested = newInitialFlow();
        for( ValueDatumPair<String, VFDatum> pair : currentSet.toList() ){
            if( pair.getDatum()==VFDatum.VAR )
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

        currentFunction = null;
    }

    public void caseAssignStmt( AssignStmt node )
    {
        //get the names from the lhs
        Expr lhs = node.getLHS();
        //analyze the rhs
        node.getRHS().analyze( this );

        for( String s : lhs.getSymbols() )
            currentSet.add( new ValueDatumPair( s, VFDatum.VAR ) );

        node.getLHS().analyze( this );

    }

    public void caseGlobalStmt( GlobalStmt node )
    {
        for( Name n : node.getNames() ){
            currentSet.add( new ValueDatumPair( n.getID(), VFDatum.VAR ) );
        }
    }

    public void casePersistentStmt( PersistentStmt node )
    {
        for( Name n : node.getNames() ){
            currentSet.add( new ValueDatumPair( n.getID(), VFDatum.VAR ) );
        }
    }

    public void caseFunctionHandleExpr( FunctionHandleExpr node )
    {
        currentSet.add( new ValueDatumPair( node.getName().getID(), VFDatum.FUN ) );
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
                        currentSet.add( new ValueDatumPair( param  , VFDatum.LDVAR ) );
                }
            }

            /* END Expression */
            if (endExpr){
                VFDatum d =  currentSet.contains( targetName );
                if ( d==null )
                {
                    currentSet.add( new ValueDatumPair( targetName, VFDatum.TOP ) );
                }

                if (VFDatum.BOT.equals(d) || VFDatum.LDVAR.equals( d) || 
		    VFDatum.VAR.equals(d) )
                {
                    endExpr = false;
                    currentSet.add( new ValueDatumPair( targetName, VFDatum.VAR ) );
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
            if ( s!=null && d==null || VFDatum.BOT.equals( d ) )
            {
                if ( scriptOrFunctionExists( s ) ) 
                {
                    currentSet.add( new ValueDatumPair( s, VFDatum.FUN ) );
                }
                else 
                    currentSet.add( new ValueDatumPair( s, VFDatum.BOT ) );
            }
            flowSets.put( node, currentSet );	
        }
        else{
            String s = node.getName().getID();
            VFDatum d = currentSet.contains( s );		    
            if ( d==null || VFDatum.BOT.equals(d) )
            {
                currentSet.add( new ValueDatumPair( s, VFDatum.LDVAR ) );
            }
            flowSets.put( node, currentSet.clone() );	
        }
    }
    
    private boolean scriptOrFunctionExists(String name){
        return 
        ((currentFunction != null) && (currentFunction.lookupFunction(name) != null))
        || ((currentScript != null) && (false)) //TODO - should return if name is the name of the script
        || lookupQuery.isFunctionOrScript(name);
    }
}
