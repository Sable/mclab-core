package natlab.toolkits.analysis.varorfun;

import ast.*;
import natlab.toolkits.analysis.*;

/**
 * A class to collect instances of RHS name uses that are determined
 * to be either a var or a function
 */

public class AssignedDataCollector extends AbstractDataCollector
{

    protected static boolean DEBUG = false;

    VFStructuralForwardAnalysis analysisResults;

    public AssignedDataCollector( ASTNode tree ){
        super( tree );

        analysisResults = new VFStructuralForwardAnalysis( tree );
        analysisResults.analyze();
    }

    public AssignedDataCollector(){
        super();
    }

    public void caseFunction( Function node )
    {
        //only look at body, don't count params
        node.getStmts().analyze(this);
    }

    public void caseCondition( Expr condExpr )
    {
        if(DEBUG)
            System.out.println("caseCond");
        condExpr.analyze( this );
    }

    public void caseVariableDecl( VariableDecl node )
    {
        //do nothing
        return;
    }
    public void caseGlobalStmt( GlobalStmt node )
    {
        //do nothing
        return;
    }
    public void casePersistentStmt( PersistentStmt node )
    {
        //do nothing
        return;
    }

    public void caseStmt( Stmt node )
    {
        if(DEBUG)
            System.out.println("caseStmt, setting currentStmtSet");
        currentStmtSet = analysisResults.getInFlowSets().get(node);
        caseASTNode( node );
    }

    public void caseName( Name node )
    {
        if(DEBUG)
            System.out.println("i'm in a name node");
        VFFlowset<String,VFDatum> set;
        if( currentStmtSet != null ){
            if(DEBUG)
                System.out.println("i'm incrementing the name from current stmt data");
            VFDatum d = currentStmtSet.contains( node.getID() );
            if(DEBUG)
                System.out.println("datum " + d);
            if( d != null && d.isAssignedVariable() )
                incrementData( node.getID() );
            if( d == null || !d.isFunction() )
                incrementData( node.getID() + "USE" );
        }
        else if( ( set = analysisResults.getInFlowSets().get( node )) != null ) {
            if(DEBUG)
                System.out.println("i'm incrementing the name from names data");
            VFDatum d = set.contains( node.getID() );
            if( d != null && d.isAssignedVariable() )
                incrementData( node.getID() );
            if( d == null && !d.isFunction() )
                incrementData( node.getID() + "USE" );
        }
        else{
            if(DEBUG)
                System.out.println("uh oh, no data to use");
        }
    }
}