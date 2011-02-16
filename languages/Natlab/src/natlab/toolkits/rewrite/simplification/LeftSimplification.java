package natlab.toolkits.rewrite.simplification;

import java.util.*;

import ast.*;
import natlab.DecIntNumericLiteralValue;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.rewrite.threeaddress.ExpressionCollector;
import natlab.toolkits.analysis.varorfun.*;


/**
 * Simplifies the left hand side of assignments. Removes complex
 * arguments from parameters. Parameters can only be variable uses or
 * structure accesses. 
 * @author Jesse Doherty
 */
public class LeftSimplification extends AbstractSimplification
{
    public LeftSimplification( ASTNode tree, VFPreorderAnalysis kind )
    {
        super( tree, kind );
    }

    /**
     * Builds a singleton start set containing this class.
     */ 
    public static Set<Class<? extends AbstractSimplification>> getStartSet()
    {
        HashSet<Class<? extends AbstractSimplification>> set = new HashSet();
        set.add( LeftSimplification.class );
        return set;
    }

    public Set<Class<? extends AbstractSimplification>> getDependencies()
    {
        HashSet<Class<? extends AbstractSimplification>> dependencies = new HashSet();
        dependencies.add( MultiAssignSimplification.class );
        dependencies.add(SimpleAssignment.class);
        //dependencies.add( EndSimplification.class );
        return dependencies;
    }

    public void caseAssignStmt( AssignStmt node )
    {
        rewriteChildren( node );
        Expr lhs = node.getLHS();
        Expr rhs = node.getRHS();
        ExpressionCollector ec = null;
        ec = new ExpressionCollector( lhs, kindAnalysis, true );

        Expr newLHS = (Expr)ec.transform();
        if( ec.getNewAssignments().size() > 0 ){
            newNode = new TransformedNode( ec.getNewAssignments() );
            node.setLHS( newLHS );
            newNode.add( node );
        }
    }

}
