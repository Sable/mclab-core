// =========================================================================== //
//                                                                             //
// Copyright 2008-2011 Andrew Casey, Jun Li, Jesse Doherty,                    //
//   Maxime Chevalier-Boisvert, Toheed Aslam, Anton Dubrau, Nurudeen Lameed,   //
//   Amina Aslam, Rahul Garg, Soroush Radpour, Olivier Savary Belanger,        //
//   Laurie Hendren, Clark Verbrugge and McGill University.                    //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//   limitations under the License.                                            //
//                                                                             //
// =========================================================================== //

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
    private VFFlowset resolvedNames;
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
                                VFFlowset resolvedNames )
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
        boolean oldSub = isSub; //TODO is this correct??!?! preserve isSub
        isSub = true;
        rewriteArgs( args );
        rewritingEnds = false;
        //isSub = false;
        isSub = oldSub; 
        /**
         * this was changed because it seemed that sometimes, expressions wouldn't 
         * get properly expanded. Example
         * zeros(lengh(a),(b+1))
         * would be turned into
         * t = length(a)
         * zeros(t, (b+1))
         * presumably this is because after dealing with 'length', it would come
         * out as not in sub anymore, due to this assignment
         */
        
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

    /**
     * this is just a copy of BinaryExpression case, but with multiple args
     */
    @Override
    public void caseMatrixExpr(MatrixExpr node) {
        if (!isLHS){
            if (isSub){
                subExprHandler( node );
            } else if (!isLHS){
                List<ASTNode> newChildren = genericExprCase(node);
                if (newChildren != null){
                    newNode = new TransformedNode(new MatrixExpr((List)newChildren));
                }
            }
        }
    }
    
    
    @Override
    public void caseCellArrayExpr(CellArrayExpr node) {
        if (isSub){
            subExprHandler( node );
        } else {
            List<ASTNode> newChildren = genericExprCase(node);
            if (newChildren != null){
                newNode = new TransformedNode(new CellArrayExpr((List)newChildren));
            }
        }
    }
    
    /**
     * lambda expression
     * do nothing to the expr, except pull it out if it's a subexpr
     */
    public void caseLambdaExpr(LambdaExpr node) {
        if (isSub){
            TempFactory tmp = TempFactory.genFreshTempFactory();
            AssignStmt newAssign = new AssignStmt( tmp.genNameExpr(), node );
            newAssign.setOutputSuppressed(true);
            newAssignments.add(newAssign);
            newNode = new TransformedNode( tmp.genNameExpr() );
        }
    }
    
    
    /**
     * does basically what happens in any case, for example Plus
     * Returns the newNodes as a list, or null if it's unchanged
     * @param children
     */
    public List<ASTNode> genericExprCase(ASTNode node){
        List<ASTNode> children = new List<ASTNode>();
        for (int i = 0; i < node.getNumChild(); i ++){
            children.add(node.getChild(i));
        }
        List<ASTNode> newChildren = new List<ASTNode>();
        for (ASTNode n : children){
            newChildren.add(n);
        }

        boolean changed = false;

        isSub = true;
        for (int i = 0; i < children.getNumChild(); i++){
            rewrite(newChildren.getChild(i));
            if ( newNode != null){
                newChildren.setChild(newNode.getSingleNode(),i);
                changed = true;
            }
        }
        if( changed ){
            return newChildren;
        } else {
            return null;
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
                    kind = kindAnalysis.getFlowSets().get(nameExpr.getName()).contains(nameExpr.getName().getID());
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
            if( resolvedNames == null ){
                if (!kindAnalysis.getFlowSets().containsKey(nameExpr.getName())){
                    kindAnalysis.analyze();
                    //TODO - not efficient, but probably better than making assumptiions!1
                }
                
                kind = kindAnalysis.getFlowSets().get(nameExpr.getName()).contains(nameExpr.getName().getID());
                
            }else
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
