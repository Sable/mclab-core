package natlab.toolkits.rewrite.threeaddress;

import java.util.LinkedList;

import ast.*;
import natlab.toolkits.analysis.AbstractNodeCaseHandler;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.analysis.varorfun.VFStructuralForwardAnalysis;
import natlab.toolkits.analysis.varorfun.VFFlowset;
import natlab.toolkits.analysis.varorfun.VFDatum;

/**
 * Used to collect sub expressions from a left hand side
 * expression. For each appropriate sub expression it builds a new
 * assignment statement to a temporary and puts it in a list. It
 * then replaces the expression with the temporary that was
 * created for the assignment statement.
 */
public class ExpressionCollector extends AbstractLocalRewrite
{
    private boolean isSub = false;
    private LinkedList<AssignStmt> newAssignments;
    private VFFlowset<String, VFDatum> resolvedNames;
    
    public ExpressionCollector( ASTNode tree, 
                                VFFlowset<String, VFDatum> resolvedNames )
    {
        super( tree );
        this.resolvedNames = resolvedNames;
        newAssignments = new LinkedList<AssignStmt>();
    }
    
    public LinkedList<AssignStmt> getNewAssignments()
    {
        return newAssignments;
    }
    public void possibleSubExprHandler(Expr node, Expr target, List<Expr> args)
    {
        if( isSub ){
            subExprHandler( node );
        }
        else{
            rewrite(target);
            isSub = true;
            //rewriteChildren( node );
            rewrite(args);
            isSub = false;
        }
    }
    public void subExprHandler(Expr node)
    {
        TempFactory tmp = TempFactory.genFreshTempFactory();
        AssignStmt newAssign = new AssignStmt( tmp.genNameExpr(), node );
        newAssignments.add( newAssign );
        newNode = new TransformedNode( tmp.genNameExpr() );
    }
    public void caseParameterizedExpr( ParameterizedExpr node )
    {
        possibleSubExprHandler( node, node.getTarget(), node.getArgs() );
    }
    public void caseCellIndexExpr( CellIndexExpr node )
    {
        possibleSubExprHandler( node, node.getTarget(), node.getArgs() );
    }

    public void caseRangeExpr( RangeExpr node )
    {
        if( isSub ){
            subExprHandler( node );
        }
        else{
            Expr newLower = node.getLower();
            Opt newIncr = node.getIncrOpt();
            Expr newUpper = node.getUpper();
            boolean changed = false;

            isSub = true;
            rewrite( node.getLower() );
            if( newNode!=null ){
                newLower = (Expr)newNode.getSingleNode();
                changed = true;
            }
            if( node.hasIncr() ){
                rewrite( node.getIncr() );
                if( newNode!=null ){
                    newIncr = new Opt((Expr)newNode.getSingleNode());
                    changed = true;
                }
            }
            rewrite( node.getUpper() );
            if( newNode != null ){
                newUpper = (Expr)newNode.getSingleNode();
                changed = true;
            }
            isSub = false;

            if( changed ){
                RangeExpr newRange = new RangeExpr(newLower,newIncr,newUpper);
                newNode = new TransformedNode(newRange);
            }
        }
        
    }
    public void caseBinaryExpr( BinaryExpr node )
    {
        if( isSub )
            subExprHandler( node );
        else{
            Expr lhs = node.getLHS();
            Expr rhs = node.getRHS();
            
            Expr newLhs = lhs;
            Expr newRhs = rhs;
            boolean changed = false;
            
            isSub = true;
            rewrite( lhs );
            if( newNode != null ){
                newLhs = (Expr)newNode.getSingleNode();
                changed = true;
            }
            rewrite( rhs );
            if( newNode != null ){
                newRhs = (Expr)newNode.getSingleNode();
                changed = true;
            }
            if( changed ){
                node.setLHS(newLhs);
                node.setRHS(newRhs);
                newNode = new TransformedNode(node);
            }
        }
    }

    public void caseNameExpr( NameExpr node )
    {
        if( isSub ){
            VFDatum datum = resolvedNames.contains( node.getName().getID() );
            if( !node.tmpVar && ( datum == null || !datum.isVariable() ) ){
                subExprHandler( node );
            }
        }
    }
    public void caseExpr( Expr node )
    {
        if( isSub )
            subExprHandler( node );
        else
            rewriteChildren( node );
    }
    public void caseLiteralExpr( LiteralExpr node )
    {
        return;
    }
    public void caseColonExpr( ColonExpr node )
    {
        return;
    }
    
        
}
