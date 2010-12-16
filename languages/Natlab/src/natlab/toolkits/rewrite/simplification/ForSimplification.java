package natlab.toolkits.rewrite.simplification;

import java.util.*;

import ast.*;
import natlab.toolkits.rewrite.AbstractLocalRewrite;
import natlab.toolkits.analysis.varorfun.*;

/**
 * Simplifies for statements. Reduces them so that there are only
 * simple range for loops. 
 *
 * @author Jesse Doherty
 */
public class ForSimplification extends AbstractSimplification
{

    public ForSimplification( ASTNode tree,
                              AbstractLocalRewrite callback,
                              VFStructuralForwardAnalysis kind )
    {
        super( tree, callback, kind );
    }

    public Set<AbstractSimplification> getDependencies()
    {
        return new HashSet();
    }

    public void caseForStmt( ForStmt node )
    {
        AssignStmt assignStmt = node.getAssignStmt();

        Expr iterableExpr = assignStmt.getRHS();
        if( !(iterableExpr instanceof RangeExpr) ){
            
            LinkedList<AssignStmt> newStmts;

            //newStmts = buildTempAssigns( iterableExpr );
            
            //RangeExpr = buildRangeExpr
        }
    }

    
}