package natlab.toolkits.analysis.varorfun;

import ast.*;
import natlab.toolkits.analysis.*;

/**
 * A class to collect instances of RHS name uses that are determined
 * to be either a var or a function
 */

public class VFDataCollector extends AbstractDataCollector
{

    public static boolean DEBUG = false;
    VFStructuralForwardAnalysis analysisResults;

    public VFDataCollector( ASTNode tree )
    {
        super( tree );
        analysisResults = new VFStructuralForwardAnalysis( tree );
        analysisResults.analyze();
    }

    public VFDataCollector(){
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

    public void caseAssignStmt( AssignStmt node )
    {
        System.out.println("case assignstmt");
        currentStmtSet = analysisResults.getInFlowSets().get(node);
        //only look at RHS
        node.getRHS().analyze( this );
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
        incrementData( node.getID() + "USE" );
        if(DEBUG)
            System.out.println("i'm in a name node");
        VFFlowset<String,VFDatum> set;
        if( currentStmtSet != null ){
            if(DEBUG)
                System.out.println("i'm incrementing the name from current stmt data");
            VFDatum d = currentStmtSet.contains( node.getID() );
            if(DEBUG)
                System.out.println("datum " + d);
            if( d != null && !d.isTop() && !d.isBottom() )
                incrementData( node.getID() );
        }
        else if( ( set = analysisResults.getInFlowSets().get( node )) != null ) {
            if(DEBUG)
                System.out.println("i'm incrementing the name from names data");
            VFDatum d = set.contains( node.getID() );
            if( d != null && !d.isTop() && !d.isBottom() )
                incrementData( node.getID() );
        }
        else{
            if(DEBUG)
                System.out.println("uh oh, no data to use");
        }
    }
}            