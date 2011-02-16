package natlab.toolkits.rewrite.simplification;

import java.util.*;

import ast.*;
import natlab.DecIntNumericLiteralValue;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.rewrite.threeaddress.ExpressionCollector;
import natlab.toolkits.analysis.varorfun.*;


/**
 * Simplifies expressions so that there is at most one complex
 * operation, all operands must me literals or variables. This makes
 * short circuiting control flow explicit.
 * 
 * This simplification introduces the CheckScalar non-syntactic node for
 * short-circuiting expansion.
 * @author Jesse Doherty
 */
public class RightSimplification extends AbstractSimplification
{
    protected boolean inAssignStmt = false;
    protected boolean inExprStmt = false;

    public RightSimplification( ASTNode tree, VFPreorderAnalysis kind )
    {
        super( tree, kind );
    }

    /**
     * Builds a singleton start set containing this class.
     */ 
    public static Set<Class<? extends AbstractSimplification>> getStartSet()
    {
        HashSet<Class<? extends AbstractSimplification>> set = new HashSet();
        set.add( RightSimplification.class );
        return set;
    }

    public Set<Class<? extends AbstractSimplification>> getDependencies()
    {
        HashSet<Class<? extends AbstractSimplification>> dependencies = new HashSet();
        dependencies.add(LeftSimplification.class);
        dependencies.add(ForSimplification.class);
        dependencies.add(ConditionalSimplification.class);
        dependencies.add(SimpleAssignment.class);
        //dependencies.add(CommaSepListRightSimplification.class);
        return dependencies;
    }


    /*
      E1 && E2
      ==========
      t1 = E1;
      CheckScalarStmt(t1);
      if t1
        t2 = E2;
        CheckScalarStmt( t2 );
      else
        t2 = false;
      end
      ----------
      E1 || E2
      ==========
      t1 = E1;
      CheckScalarStmt(t1);
      if ~t1
        t2 = E2;
        CheckScalarStmt(t2);
      else
        t2 = false;
      end
     */

    protected LinkedList<Stmt> newStmts;

    public void caseExprStmt( ExprStmt node )
    {
        inExprStmt = true;
        caseStmt( node );
        inExprStmt = false;
    }
    public void caseAssignStmt( AssignStmt node )
    {
        inAssignStmt = true;
        caseStmt( node );
        inAssignStmt = false;
    }
    public void caseStmt( Stmt node )
    {
        newStmts = new LinkedList();
        rewriteChildren( node );
        if( !newStmts.isEmpty() ){
            newStmts.add( node );
            newNode = new TransformedNode( newStmts );
        }
    }

    String deep = "";
    public void caseExpr( Expr node )
    {
        //deep += "!";
        //System.out.println(deep + " " + node.getPrettyPrinted());
        ExpressionCollector ec = new ExpressionCollector( node, kindAnalysis );
        Expr newExpr = (Expr)ec.transform();

        LinkedList<Stmt> backNewStmts = newStmts;
        LinkedList<Stmt> exprsNewStmts = new LinkedList();

        //loop through all the new assignment stmts and rewrite them
        while( !ec.getNewAssignments().isEmpty() ){
            
            Stmt workStmt = ec.getNewAssignments().removeLast();
            //System.out.println( deep + "- " + workStmt.getPrettyPrinted() );
            rewrite( workStmt );
            if( newNode != null )
                if( newNode.isMultipleNodes() )
                    exprsNewStmts.addAll(0, newNode.getMultipleNodes());
                else
                    exprsNewStmts.addFirst( (Stmt)newNode.getSingleNode() );
            else
                exprsNewStmts.addFirst( workStmt );
            
            //newStmts.addAll( ec.getNewAssignments() );
            //newNode = new TransformedNode( newExpr );

        }
        newStmts = backNewStmts;
        if( !exprsNewStmts.isEmpty() ){
            newStmts.addAll( exprsNewStmts );
            newNode = new TransformedNode( newExpr );
        }
        //deep = deep.substring(0,deep.length()-1);
    }
}
