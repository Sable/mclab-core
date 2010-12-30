package natlab.toolkits.rewrite.simplification;

import java.util.*;

import ast.*;
import natlab.DecIntNumericLiteralValue;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.analysis.varorfun.*;


/**
 * Makes the use of end as an index more explicit. Removes uses of end
 * and replaces them with a variable containing the index value. This
 * value is given the value with the use of the non syntactic
 * EndCallExpr call.
 * @author Jesse Doherty
 */
public class EndSimplification extends AbstractSimplification
{
    public EndSimplification( ASTNode tree, VFPreorderAnalysis kind )
    {
        super( tree, kind );
    }

    /**
     * Builds a singleton start set containing this class.
     */ 
    public static Set<Class<? extends AbstractSimplification>> getStartSet()
    {
        HashSet<Class<? extends AbstractSimplification>> set = new HashSet();
        set.add( EndSimplification.class );
        return set;
    }

    public Set<Class<? extends AbstractSimplification>> getDependencies()
    {
        HashSet<Class<? extends AbstractSimplification>> dependencies = new HashSet();
        dependencies.add( RightSimplification.class );
        return dependencies;
    }

    protected LinkedList<Stmt> newStmts = null;
    protected boolean hasEnd;
    protected TempFactory endTempFact;
    
    /*
      S1
      ==========
      ...
      tn = END(En, x, y );
      ...
      new S1
     */
    public void caseStmt( Stmt node )
    {
        LinkedList<Stmt> oldNewStmts = newStmts;
        newStmts = new LinkedList();
        hasEnd = false;
        rewriteChildren( node );
        if( !newStmts.isEmpty() ){
            newStmts.add( node );
            newNode = new TransformedNode( newStmts );
        }
        newStmts = oldNewStmts;
    }

    /*
      Replaces the end with a temp var and records that an end was
      seen. 
    */
    public void caseEndExpr( EndExpr node )
    {
        if( endTempFact == null )
            endTempFact = TempFactory.genFreshTempFactory();
        newNode = new TransformedNode( endTempFact.genNameExpr() );
        hasEnd = true;
    }

    //This and caseCellIndexExpr should be refactored.
    public void caseParameterizedExpr( ParameterizedExpr node )
    {

        boolean change = false;
        ArrayList<Expr> newArgs = new ArrayList(node.getNumArg());
        if( canEndBind( node ) ){
            boolean oldHasEnd = hasEnd;
            TempFactory oldEndTempFact = endTempFact;

            int numDim = node.getNumArg();
            for( int i=0; i<numDim; i++ ){
                rewriteArg( node.getArg(i) );
                if( hasEnd ){
                    addNewAssignment(node.getTarget(), numDim, i );
                    if( newNode != null )
                        newArgs.add( (Expr)newNode.getSingleNode() );
                    else 
                        newArgs.add( node.getArg(i) );
                    change = true;
                }
                else
                    newArgs.add( node.getArg(i) );
            }
            if( change ){
                ParameterizedExpr newPara = new ParameterizedExpr( node.getTarget(),
                                                                   ASTHelpers.listToList( newArgs ) );
                newNode = new TransformedNode( newPara );
            }
            else{
                newNode = null;
            }
            hasEnd = oldHasEnd;
            endTempFact = oldEndTempFact;

        }
        else{
            rewriteChildren( node );
        }
    }

    public void caseCellIndexExpr( CellIndexExpr node )
    {
        boolean change = false;
        ArrayList<Expr> newArgs = new ArrayList(node.getNumArg());
        boolean oldHasEnd = hasEnd;
        TempFactory oldEndTempFact = endTempFact;

        int numDim = node.getNumArg();
        for( int i=0; i<numDim; i++ ){
            rewriteArg( node.getArg(i) );
            if( hasEnd ){
                addNewAssignment(node.getTarget(), numDim, i );
                if( newNode != null )
                    newArgs.add( (Expr)newNode.getSingleNode() );
                else 
                    newArgs.add( node.getArg(i) );
                change = true;
            }
            else
                newArgs.add( node.getArg(i) );
        }
        if( change ){
            ParameterizedExpr newPara = new ParameterizedExpr( node.getTarget(),
                                                               ASTHelpers.listToList( newArgs ) );
            newNode = new TransformedNode( newPara );
        }
        else{
            newNode = null;
        }
        hasEnd = oldHasEnd;
        endTempFact = oldEndTempFact;
        
    }
    /**
     * Determines if a given ParameterizedExpr can have an END bound
     * to it.
     */
    public boolean canEndBind( ParameterizedExpr node )
    {
        return !(node.getTarget() instanceof NameExpr) || !isFun(node.getTarget() );
    }

    /**
     * Rewrites a given argument and does some bookkeeping. 
     */
    protected void rewriteArg( Expr arg )
    {
        hasEnd = false;
        endTempFact = null; //TempFactory.genFreshTempFactory();
        rewrite( arg );
    }

    /**
     * Adds a new assignment of an EndCallExpr to the current temp
     * variable. 
     */
    protected void addNewAssignment( Expr target, int numDim, int dim )
    {
        EndCallExpr end = new EndCallExpr( (Expr)target.copy(), numDim, dim );
        AssignStmt assign = new AssignStmt( endTempFact.genNameExpr(), end );
        assign.setOutputSuppressed( true );
        newStmts.add( assign );
    }
}
