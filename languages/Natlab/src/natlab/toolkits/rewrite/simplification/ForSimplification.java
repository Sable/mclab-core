package natlab.toolkits.rewrite.simplification;

import java.util.*;

import ast.*;
import natlab.toolkits.rewrite.*;
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

    /*
      for v=E
        ...
      end
      ==========
      t1=E;
      t2=size(t1);
      t3=prod(t2(2:end));
      for i=1:t3
        ...
      end
     */
    public void caseForStmt( ForStmt node )
    {
        AssignStmt assignStmt = node.getAssignStmt();

        Expr iterableExpr = assignStmt.getRHS();
        if( !(iterableExpr instanceof RangeExpr) ){
            
            LinkedList<AssignStmt> newStmts = new LinkedList();

            TempFactory t1Fact = TempFactory.genFreshTempFactory();
            TempFactory t2Fact = TempFactory.genFreshTempFactory();
            TempFactory t3Fact = TempFactory.genFreshTempFactory();

            
        }
    }

    
}