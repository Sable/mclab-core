package natlab.toolkits.rewrite.threeaddress;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;

import ast.*;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.analysis.varorfun.*;

/**
 * Used to collect sub expressions from an expression. For each
 * appropriate sub expression it builds a new assignment statement to
 * a temporary and puts it in a list. It then replaces the expression
 * with the temporary that was created for the assignment statement.
 *
 * This will also deal with end statements that bind to a non sub
 * expression. It will replace them with EndCallExpr. To do this the
 * collector has 2 modes. Normal mode where it simply extracts sub
 * expressions, and End searching mode where it tries to fix ends. It
 * enters End searching mode when it sees an expression to which an
 * end can bind. 
 */
public class ExpressionCollector extends AbstractLocalRewrite
{
    private boolean isSub = false;
    private LinkedList<AssignStmt> newAssignments;
    private VFFlowset<String, VFDatum> resolvedNames;
    private VFPreorderAnalysis kindAnalysis;
    protected boolean isLHS = false;
    protected boolean rewritingEnds = false;
    protected Expr lastTarget = null;
    protected int lastIndex, lastDims;

    protected HashMap< Expr, HashSet<EndCallExpr>> targetAndEndMap;
    

    public ASTNode transform()
    {
        targetAndEndMap = new HashMap();
        ASTNode returnNode = super.transform();
        for( Expr t : targetAndEndMap.keySet() )
            for( EndCallExpr e : targetAndEndMap.get(t) ){
                Expr tCopy = (Expr)t.copy();
                kindAnalysis.getFlowSets().put(tCopy, kindAnalysis.getFlowSets().get(t) );
                e.setArray( tCopy );
            }
        return returnNode;
    }

    public ExpressionCollector( ASTNode tree, 
                                VFFlowset<String, VFDatum> resolvedNames )
    {
        super( tree );
        this.resolvedNames = resolvedNames;
        kindAnalysis = null;
        newAssignments = new LinkedList<AssignStmt>();
    }

    /**
     * This version of the constructor takes in isLHS. All others will
     * set this value to false.
     */
    public ExpressionCollector( ASTNode tree,
                                VFPreorderAnalysis kind,
                                boolean isLHS )
    {
        this( tree, kind );
        this.isLHS = isLHS;
    }
    public ExpressionCollector( ASTNode tree,
                                VFPreorderAnalysis kind )
    {
        super( tree );
        kindAnalysis = kind;
        resolvedNames = null;
        newAssignments = new LinkedList();
    }
    
    public LinkedList<AssignStmt> getNewAssignments()
    {
        return newAssignments;
    }

    /**
     * Determines if a given ParameterizedExpr can have an END bound
     * to it.
     */
    public boolean canEndBind( ParameterizedExpr node )
    {
        return !(node.getTarget() instanceof NameExpr) || !isFun((NameExpr)node.getTarget() );
    }

    /**
     * Deals with cell or range expression. Will perform different
     * action depending on if it is a sub expression or not.
     */
    public void possibleSubExprHandler(Expr node, Expr target, List<Expr> args, boolean isCell )
    {
        //System.out.println("&& pseh "+ node.getPrettyPrinted() + " " + isSub + " " + isLHS + " " + isCell );
        if( isSub ){
            subExprHandler(node, target, args, isCell );
        }
        else{
            if( isLHS || isCell || canEndBind( (ParameterizedExpr)node ) ){
                targetAndEndMap.put( target, new HashSet() );
                lastTarget = target;
            }
            /*isSub = true;
            rewritingEnds = false;
            rewriteArgs(args);
            lastTarget = null;


            isSub = false;
            rewrite(target);
            isSub = false;*/
            Expr oldLastTarget = lastTarget;
            lastTarget = null;
            rewrite(target);

            lastTarget = oldLastTarget;
            isSub = true;
            rewritingEnds = false;
            //System.out.println("rewriting args");
            rewriteArgs(args);
            isSub = false;
            lastTarget = null;
        }
        //System.out.println("&& done: " + node.getPrettyPrinted() );
    }
    
    protected void rewriteEnds( Expr node )
    {
        
        caseASTNode( node );
    }

    /*
      Note: we assume that no args are removed or added in this
      rewrite, so we can be assured that if newNode is set, then it
      will be a single node.
     */
    protected void rewriteArgs(List<Expr> args )
    {
        if( lastTarget != null && !rewritingEnds ){
            boolean change = false;
            lastDims = args.getNumChild();
            for( int i =0; i< lastDims; i++ ){
                //System.out.println( args.getChild(i) + " " + args.getChild(i).getPrettyPrinted() );
                lastIndex = i+1;
                rewrite( args.getChild(i) );
                if( newNode != null ){
                    change = true;
                    args.setChild( (Expr)newNode.getSingleNode(), i );
                }
            }
            newNode = null;
        }
        else
            rewrite( args );
    }

    /**
     * Simple helper method to wrap subExprHandler(Expr,boolean) call.
     */
    public void subExprHandler(Expr node )
    {
        subExprHandler(node, false);
    }

    /**
     * Rewrites sub expressions. If the collector is in end finding
     * mode, then it simply continues looking for ends and nothing
     * else. If there was something that an end could bind to, then it
     * will rewrite the ends. It will then replace the expression with
     * a temp variable, and add an assignment for the given expression
     * to the temp variable.
     */
    public void subExprHandler(Expr node, boolean canExpand)
    {
        if( rewritingEnds )
            rewriteEnds( node );
        else{
            if( lastTarget != null ){
                rewritingEnds = true; 
                rewriteEnds( node );
                rewritingEnds = false;
            }
            rewriteSubExpr( node, canExpand );
        }
    }

    /**
     * Deals with Cell indexing and parameterized expressions when
     * they are sub expressions.
     */
    public void subExprHandler(Expr node, Expr target, List<Expr> args, boolean isCell)
    {
        if( !isCell && !canEndBind( (ParameterizedExpr)node ) ){
            //can't bind an end
            if( rewritingEnds ){
                rewriteArgs( args );
            }
            else{
                fixEndsAndRewrite( node, args, isCell );
            }
        }
        else{
            //all contained ends will be bound to this expr, so just
            //rewrite it, don't care about the ends
            rewriteSubExpr( node, isCell );
        }
    }

    /**
     * Rewrite a sub expression containing ends to rewrite. In
     * particular, deals with parameterized and cell indexing expressions.
     */
    protected void fixEndsAndRewrite(Expr node, List<Expr> args, boolean canExpand ){
        rewritingEnds = true;
        isSub = true;
        rewriteArgs( args );
        rewritingEnds = false;
        isSub = false;
        rewriteSubExpr( node, canExpand );
        //Expr oldLastTarget = lastTarget;
        //lastTarget = null;
        //subExprHandler( node );
        //lastTarget = oldLastTarget;
    }

    /**
     * Creates the new assignment to a temporary from a given
     * expression and replaces the expression with the temporary. It
     * adds the new assignment to the newAssignments list.
     */
    protected void rewriteSubExpr( Expr node, boolean canExpand )
    {
        TempFactory tmp = TempFactory.genFreshTempFactory();
        AssignStmt newAssign;
        if( canExpand ){
            MatrixExpr newMat = new MatrixExpr(
                                               new ast.List().add(
                                                                  new Row(
                                                                          new ast.List().add(tmp.genCSLExpr())
                                                                          )
                                                                  )
                                               );
            newAssign = new AssignStmt( newMat, node );
        }
        else
            newAssign = new AssignStmt( tmp.genNameExpr(), node );
        newAssign.setOutputSuppressed(true);
        newAssignments.add( newAssign );
        if( canExpand )
            newNode = new TransformedNode( tmp.genCSLExpr() );
        else
            newNode = new TransformedNode( tmp.genNameExpr() );
    }


    /**
     * Extracts out the target if not in the LHS of an assignment.
     */
    public void caseDotExpr( DotExpr node )
    {
        if( isSub )
            subExprHandler( node, true );
        else if( !isLHS ){
            isSub = true;
            rewrite( node.getTarget() );
            if( newNode != null ){
                node.setTarget( (Expr)newNode.getSingleNode() );
                newNode = new TransformedNode( node );
            }
            isSub = false;
        }
    }
                

    public void caseParameterizedExpr( ParameterizedExpr node )
    {
        possibleSubExprHandler( node, node.getTarget(), node.getArgs(), false );
    }
    public void caseCellIndexExpr( CellIndexExpr node )
    {
        possibleSubExprHandler( node, node.getTarget(), node.getArgs(), true );
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
    public void caseUnaryExpr( UnaryExpr node )
    {
        if( isSub )
            subExprHandler( node );
        else{
            Expr operand = node.getOperand();
            
            Expr newOperand = operand;
            boolean changed = false;
            
            isSub = true;
            rewrite( operand );
            if( newNode != null ){
                newOperand = (Expr)newNode.getSingleNode();
                changed = true;
            }
            if( changed ){
                node.setOperand(newOperand);
                newNode = new TransformedNode(node);
            }
        }
    }

    public void caseNameExpr( NameExpr node )
    {
        if( isSub ){
            if( !isVar( node ) ){
                subExprHandler( node );
            }
        }
    }

    protected boolean isVar( NameExpr nameExpr )
    {
        if( nameExpr.tmpVar )
            return true;
        else{
            VFDatum kind;
            if( resolvedNames == null )
                try{
                    kind = kindAnalysis.getFlowSets().get(nameExpr).contains(nameExpr.getName().getID());
                }catch( NullPointerException e ){
                    kind = null;
                }
            else
                kind = resolvedNames.contains( nameExpr.getName().getID() );
            //System.out.println("kind: "+nameExpr.getPrettyPrinted() + " " + kind);
            return (kind!=null) && kind.isVariable();
        }
    }
    protected boolean isFun( NameExpr nameExpr )
    {
        if( nameExpr.tmpVar )
            return false;
        else{
            VFDatum kind;
            if( resolvedNames == null )
                kind = kindAnalysis.getFlowSets().get(nameExpr).contains(nameExpr.getName().getID());
            else
                kind = resolvedNames.contains( nameExpr.getName().getID() );
            //System.out.println("kind: "+nameExpr.getPrettyPrinted() + " " + kind);
            return (kind!=null) && kind.isFunction();
        }
    }
    public void caseExpr( Expr node )
    {
        //System.out.println("^^^ ce " + isSub + " " + node + " " + node.getPrettyPrinted());
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

    public void caseEndExpr( EndExpr node )
    {
        EndCallExpr newEnd = new EndCallExpr( new NameExpr( new Name("BLARG")), lastDims, lastIndex );
        targetAndEndMap.get(lastTarget).add( newEnd );
        if( !rewritingEnds )
            caseExpr( newEnd );
        else 
            newNode = new TransformedNode( newEnd );
    }
        
}
