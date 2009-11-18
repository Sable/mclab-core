package natlab.toolkits.analysis.varorfun;

import ast.*;
import natlab.toolkits.analysis.*;

/**
 * A class to collect instances of RHS name uses that are determined
 * to be either a var or a function
 */

public class UseDataCollector extends AbstractDataCollector
{

    protected static boolean DEBUG = false;

    public UseDataCollector( ASTNode tree )
    {
        super( tree );
    }

    public UseDataCollector(){
        super();
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

    public void caseFunction( Function node )
    {
        //only look at body, don't count params
        node.getStmts().analyze(this);
    }

    public void caseName( Name node )
    {
        incrementData( node.getID() );
    }
}            