package natlab.toolkits.analysis;

import ast.*;
import java.util.*;

/**
 * Very General interface for analysis. Note: implementations should
 * supply a standard constructor that takes in ASTNode as argument.
 */

public interface Analysis<A extends FlowSet> extends NodeCaseHandler
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
     * Create a new initial flow set.
     *
     * @return A new initial flow set. This is used as starting value
     * for flow sets.
     */
    public A newInitialFlow();
}