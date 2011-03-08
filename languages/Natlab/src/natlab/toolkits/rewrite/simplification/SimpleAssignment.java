package natlab.toolkits.rewrite.simplification;

import java.util.*;

import ast.*;
import natlab.DecIntNumericLiteralValue;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.analysis.varorfun.*;


/**
 * Simplifies assignment statements. Ensures that in an assignments
 * statement, between the left and right hand sides, there is at most
 * one complex operation. Either the left or right hand side must be a
 * variable or literal.
 *
 * @author Jesse Doherty
 */
public class SimpleAssignment extends AbstractSimplification
{
    public SimpleAssignment( ASTNode tree, VFPreorderAnalysis kind )
    {
        super( tree, kind );
    }

    /**
     * Builds a singleton start set containing this class.
     */ 
    public static Set<Class<? extends AbstractSimplification>> getStartSet()
    {
        HashSet<Class<? extends AbstractSimplification>> set = new HashSet();
        set.add( SimpleAssignment.class );
        return set;
    }

    public Set<Class<? extends AbstractSimplification>> getDependencies()
    {
        HashSet<Class<? extends AbstractSimplification>> dependencies = new HashSet();
        return dependencies;
    }

    /*
      Exp1 = Exp2
      =========
      t1 = Exp2;
      Exp1 = t1
      ---------
      if Exp1 and Exp2 are both not simple variables

      Does not deal with multi assignment statements.
     */
    public void caseAssignStmt( AssignStmt node )
    {
        Expr lhs = node.getLHS();
        Expr rhs = node.getRHS();
        if( !(lhs instanceof MatrixExpr) ){
            if( !isVar(lhs) && !isVar(rhs) ){
                TempFactory tempF = TempFactory.genFreshTempFactory();
                AssignStmt a1 = new AssignStmt( tempF.genNameExpr(), rhs );
                AssignStmt a2 = new AssignStmt( lhs, tempF.genNameExpr() );
                
                newNode = new TransformedNode( a1 );
                newNode.add(a2);
            }
        }
    }

}
