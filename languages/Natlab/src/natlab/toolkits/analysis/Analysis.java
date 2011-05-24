package natlab.toolkits.analysis;

import ast.*;
import java.util.*;

/**
 * Very General interface for analysis. Note: implementations should
 * supply a standard constructor that takes in ASTNode as argument.
 */

public interface Analysis extends NodeCaseHandler
{

    /**
     * Executes the analysis.
     */
    public void analyze();

    /**
     * Get the AST on which the analysis is being performed.
     *
     * @return ASTNode on which the analysis is performed.
     */
    public ASTNode getTree();

    /**
     * Returns a boolean signifying whether or not the analysis has
     * been performed on the given AST. This does not necessarily
     * take into account changes to the AST.
     *
     * @return Whether or not the analysis has been performed.
     */

    public boolean isAnalyzed();


    /**
     * Process the condition of a while loop or if stmt or any other
     * statement that includes a condition.
     *
     * @param condExpr  The condition expression to be handled.
     */
    public void caseCondition( Expr condExpr );

    /**
     * Process the condition of a while loop.
     *
     * @param condExpr  The condition expression to be handled.
     */
    public void caseWhileCondition( Expr condExpr );

    /**
     * Process the condition of an if statement.
     *
     * @param condExpr  The condition expression to be handled.
     */
    public void caseIfCondition( Expr condExpr );
    
    /**
     * Process the loop variable assignment statement from a for
     * loop. 
     *
     * @param loopVar  The loop variable assignment statement to be
     * handled. 
     */
    public void caseLoopVar( AssignStmt loopVar );


    /**
     * Process the main expression of a case statement. This is the
     * expression that you get as part of the switch header e.g.
     *<pre>
     * switch switchExpr
     *   case caseExpr
     *     ....
     * end
     *</pre>
     * 
     *
     * @param switchExpr  The switch expression to process.
     */
    public void caseSwitchExpr( Expr switchExpr );


    /**
     * Create a new initial flow set.
     *
     * @return A new initial flow set. This is used as starting value
     * for flow sets.
     */
    //public A newInitialFlow();
    
    
    /**
     * sets the NodeCaseHandler that gets called first by the node's analyze
     * method. The given NodeCaseHandler is responsible for calling back to
     * the corresponding node case of this analysis.
     * 
     * This should only be used internally.
     */
    public void setCallback(NodeCaseHandler handler);
    
}