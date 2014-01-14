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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import natlab.toolkits.analysis.varorfun.VFDatum;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.rewrite.AbstractLocalRewrite;
import natlab.toolkits.rewrite.TempFactory;
import natlab.toolkits.rewrite.TransformedNode;
import ast.ASTNode;
import ast.AssignStmt;
import ast.BinaryExpr;
import ast.CellArrayExpr;
import ast.CellIndexExpr;
import ast.ColonExpr;
import ast.DotExpr;
import ast.EndCallExpr;
import ast.EndExpr;
import ast.Expr;
import ast.LambdaExpr;
import ast.List;
import ast.LiteralExpr;
import ast.MatrixExpr;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.RangeExpr;
import ast.Row;
import ast.UnaryExpr;

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
    private LinkedList<AssignStmt> newAssignments;
    private Map<String, VFDatum> resolvedNames;
    private VFPreorderAnalysis kindAnalysis;
    protected boolean isLHS = false;
    //protected boolean rewritingEnds = false;
    //protected Expr lastTarget = null;
    //protected int lastIndex, lastDims;
    private static boolean DEBUG = false;

    protected HashMap< Expr, HashSet<EndCallExpr>> targetAndEndMap;
    
    public ExpressionCollector( ASTNode tree, 
                                Map<String, VFDatum> resolvedNames )
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

    public void caseExpr( Expr node )
    { return; }

    public void caseLambdaExpr( LambdaExpr node )
    { return; }

    public void caseParameterizedExpr( ParameterizedExpr node )
    {
        Expr paramTarget = node.getTarget();
        if( paramTarget instanceof DotExpr ){
            DotExpr dotParamTarget = (DotExpr)paramTarget;
            
            if( !isLHS && !(dotParamTarget.getTarget() instanceof NameExpr) ){
                dotParamTarget.setTarget( makeTempAssign(dotParamTarget.getTarget() ));
            }
        }
        replaceEnds( node );
        removeArgs( node );
        fixEnds( node );
    }

    public void caseCellIndexExpr( CellIndexExpr node )
    {
        Expr indexTarget = node.getTarget();
        if( !isLHS && !(indexTarget instanceof NameExpr))
            node.setTarget( makeTempAssign( indexTarget ));
        replaceEnds( node );
        removeArgs( node );
        fixEnds( node );
    }

    public void caseDotExpr( DotExpr node )
    {
        if( isLHS ){
            removeArgs( node );
        }else{
            if( !(node.getTarget() instanceof NameExpr) ){
                node.setTarget( makeTempAssign(node.getTarget() ));
            }
        }
    }

    public void caseMatrixExpr( MatrixExpr node )
    {
        if( collectFromRows( node.getRows() ))
            newNode = new TransformedNode( node );
    }
    public void caseCellArrayExpr( CellArrayExpr node )
    {
        if( collectFromRows( node.getRows() ))
            newNode = new TransformedNode( node );
    }

    private boolean collectFromRows( List<Row> rows ){
        if( !areAllVarsOrLiteralsInRows( rows )){
            boolean changed = false;
            for( Row r : rows ){
                for( int i=0; i< r.getNumElement(); i++ ){
                    if( !isLiteral(r.getElement(i)) ){
                        r.setElement( makeTempAssign(r.getElement(i)), i);
                        changed = true;
                    }
                }
            }
            return changed;
        }
        return false;
    }
    public void caseRangeExpr( RangeExpr node ){
        if( isVarOrLiteral(node.getLower()) &&
            isVarOrLiteral(node.getUpper()) && 
            ( !node.hasIncr() || 
              isVarOrLiteral(node.getIncr()) )){
            return;
        }else{
            boolean changed = false;
            if( !(isLiteral(node.getLower()))){
                changed = true;
                node.setLower( makeTempAssign( node.getLower() ));
            }
            if( node.hasIncr() && !(isLiteral(node.getIncr())) ){
                changed = true;
                node.setIncr( makeTempAssign( node.getIncr() ));
            }
            if( !(isLiteral(node.getUpper())) ){
                changed = true;
                node.setUpper( makeTempAssign( node.getUpper() ));
            }
            if( changed )
                newNode = new TransformedNode( node );
        }
    }

    public void caseBinaryExpr( BinaryExpr node )
    {
        if( !areAllVarsOrLiterals( node.getLHS(),
                                   node.getRHS() ) ){
            boolean changed = false;
            if( !isLiteral(node.getLHS()) ){
                changed = true;
                node.setLHS( makeTempAssign( node.getLHS() ));
            }
            if( !isLiteral(node.getRHS()) ){
                changed = true;
                node.setRHS( makeTempAssign( node.getRHS() ));
            }
            if( changed )
                newNode = new TransformedNode( node );
        }
    }

    public void caseUnaryExpr( UnaryExpr node )
    {
        if( !isVarOrLiteral( node.getOperand() )){
            node.setOperand( makeTempAssign( node.getOperand() ));
            newNode = new TransformedNode( node );
        }
    }

    /**
     * Takes the given expression, creates an assignment from the
     * expression to a fresh temp, adds it to the list of new
     * assignments, and returns the fresh temp.
     */
    protected NameExpr makeTempAssign( Expr node )
    {
        return makeTempAssign( node, false );
    }
    protected NameExpr makeTempAssign( Expr node, boolean useCSLtmps )
    {
        TempFactory tmp = TempFactory.genFreshTempFactory();
        NameExpr temp1, temp2;
        if( useCSLtmps ){
            temp1 = tmp.genCSLExpr();
            temp2 = tmp.genCSLExpr();
        } else{
            temp1 = tmp.genNameExpr();
            temp2 = tmp.genNameExpr();
        }
        AssignStmt newAssign = new AssignStmt(temp1, node);
        newAssign.setOutputSuppressed(true);
        newAssignments.add(newAssign);
        return temp2;
    }
    protected boolean isVarOrLiteral( ASTNode node ){
        return (isLiteral(node)) || isVar(node);
    }
    protected boolean areAllVarsOrLiterals( List<Expr> nodes ){
        for( ASTNode n : nodes )
            if( !isVarOrLiteral( n ) )
                return false;
        return true;
    }

    protected boolean areAllVarsOrLiterals( Expr... nodes ){
        for( ASTNode n : nodes )
            if( !isVarOrLiteral( n ) )
                return false;
        return true;
    }

    protected boolean areAllVarsOrLiteralsInRows( List<Row> rows )
    {
        for( Row r : rows )
            if( !areAllVarsOrLiterals( r.getElements() ) )
                return false;
        return true;
    }

    protected boolean areAllLiterals( Expr... nodes )
    {
        for( Expr n : nodes )
            if( !isLiteral( n ) )
                return false;
        return true;
    }

    protected boolean areAllLiterals( List<Expr> nodes )
    {
        for( Expr n : nodes )
            if( !isLiteral( n ))
                return false;
        return true;
    }

    protected boolean isLiteral( ASTNode node ){
        //TODO - does this make sense? - this expands colons, so we make colon expr's literals as well
        return node instanceof LiteralExpr || node instanceof ColonExpr;
    }

    protected boolean isVar( ASTNode node )
    {
        if( node instanceof NameExpr )
            return isVar((NameExpr)node);
        else
            return false;
    }

    protected boolean isVar( NameExpr nameExpr )
    {
        if( nameExpr.tmpVar )
            return true;
        else{
            VFDatum kind;
            if( resolvedNames == null )
                try{
                    kind = kindAnalysis.getFlowSets().get(nameExpr.getName()).get(nameExpr.getName().getID());
                }catch( NullPointerException e ){
                    kind = null;
                }
            else
                kind = resolvedNames.get( nameExpr.getName().getID() );
            //System.out.println("kind: "+nameExpr.getPrettyPrinted() + " " + kind);
            return (kind!=null) && kind.isVariable();
        }
    }
    protected boolean isFun( ASTNode node )
    {
        if( node instanceof NameExpr )
            return isFun((NameExpr)node);
        else
            return false;
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
                    //TODO - not efficient, but probably better than making assumptiions!
                }
                
                kind = kindAnalysis.getFlowSets().get(nameExpr.getName()).get(nameExpr.getName().getID());
                
            }else
                kind = resolvedNames.get( nameExpr.getName().getID() );
            //System.out.println("kind: "+nameExpr.getPrettyPrinted() + " " + kind);
            return (kind!=null) && kind.isFunction();
        }
    }

    private boolean canCSLExpand( Expr node )
    {
        if( node instanceof CellIndexExpr ){
            return !areAllLiterals( ((CellIndexExpr)node).getArgs() );
        } else if( node instanceof DotExpr ){
            DotExpr dot = (DotExpr)node;
            if( dot.getTarget() instanceof ParameterizedExpr ){
                ParameterizedExpr paramExpr = (ParameterizedExpr) dot.getTarget();
                return !areAllLiterals( paramExpr.getArgs() );
            }else
                return true;
        }
        return false;
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
     * Removes the args from all parameterized expressions in node and
     * replaces them with temps. Add appropriate assignment statements
     * to newAssignments.
     */
    private void removeArgs( ASTNode node )
    {
        ArgRemover argRemover = new ArgRemover( node );
        ASTNode cleanedNode = argRemover.transform();
        if( cleanedNode != null )
            newNode = new TransformedNode( cleanedNode );
    }

    private void replaceEnds( ASTNode node )
    {
        targetAndEndMap = new HashMap<Expr, HashSet<EndCallExpr>>();
        EndReplacer endReplacer = new EndReplacer( node );
        ASTNode cleanedNode = endReplacer.transform();
        if( cleanedNode != null )
            newNode = new TransformedNode( cleanedNode );
    }

    private void fixEnds( ASTNode node )
    {
        for( Expr t : targetAndEndMap.keySet() ){
            for( EndCallExpr e : targetAndEndMap.get(t) ){
                Expr tCopy = (Expr)t.copy();
                kindAnalysis.getFlowSets().put(tCopy, kindAnalysis.getFlowSets().get(t) );
                e.setArray( tCopy );
            }
        }
    }
    /**
     * A rewrite that replaces all end expressions with end call
     * expressions with the correct index bindings. It also associates
     * the end call expression with the expression it binds to. It
     * records this association in the targetAndEndMap. 
     *
     * This transformation does not extract the ends, which should be
     * done by the ArgRemover. This transformation also leaves the end
     * call expressions in an incorrect state, namely, they are not
     * bound correctly. 
     */
    private class EndReplacer extends AbstractLocalRewrite
    {
        private Expr target = null;
        private int totalIndices;
        private int currentIndex;
        public EndReplacer(ASTNode tree)
        {
            super( tree );
        }
        
        @Override
        public void caseParameterizedExpr( ParameterizedExpr node )
        {
            if( target == null ){
                if( canEndBind( node ) )
                    replaceEndsInArgs( node.getArgs(), node.getTarget() );
            } else{
                if( !canEndBind( node ) ){
                    rewrite( node.getArgs() );
                    if (DEBUG){
                        System.out.println("JESSE130 " + node.getPrettyPrinted());
                    }
                }
            }
            rewrite( node.getTarget() );
        }

        @Override
        public void caseCellIndexExpr( CellIndexExpr node )
        {
            if( target == null )
                replaceEndsInArgs( node.getArgs(), node.getTarget() );
            rewrite( node.getTarget() );
        }

        @Override
        public void caseEndExpr( EndExpr node )
        {
            EndCallExpr endCallExpr = new EndCallExpr( new NameExpr( new Name("THISSHOULDNOTBESEENINOUTPUT_LOVE_JESSE")),
                                                       totalIndices, 
                                                       currentIndex );
            addToMap( endCallExpr );
            newNode = new TransformedNode(endCallExpr);
            if (DEBUG) System.out.println("JESSE106 "+totalIndices+" "+currentIndex+" "+target.getPrettyPrinted());
        }

        private void addToMap( EndCallExpr endCall )
        {
            if( targetAndEndMap.containsKey( target ) ){
                HashSet<EndCallExpr> set = targetAndEndMap.get(target);
                if( set == null ){
                    set = new HashSet<EndCallExpr>();
                    targetAndEndMap.put(target, set);
                }
                set.add( endCall );
            }else{
                HashSet<EndCallExpr> set = new HashSet<EndCallExpr>();
                set.add( endCall );
                targetAndEndMap.put(target,set);
            }
        }
        /**
         * Deep replaces all the end expressions in a list of
         * expressions, binding them to the given target. It is
         * intended for the arguments of a parameterized or cell
         * indexing expression. It also keeps track of current index,
         * and total indices.
         *
         * Post Condition: target = null;
         */
        private void replaceEndsInArgs( List<Expr> nodes, Expr target )
        {
            this.target = target;
            totalIndices = nodes.getNumChild();
            for( int i = 0; i<totalIndices; i++ ){
                currentIndex = i+1;
                rewrite( nodes.getChild(i) );
                if( newNode != null )
                    nodes.setChild((Expr)newNode.getSingleNode(),i);
            }
            this.target = null;
        }
    }

    private class ArgRemover extends AbstractLocalRewrite
    {
        public ArgRemover(ASTNode tree)
        {
            super( tree );
        }
        
        @Override
        public void caseParameterizedExpr( ParameterizedExpr node )
        {
            rewrite(node.getTarget());
            if( rewriteArgList( node.getArgs() ))
                newNode = new TransformedNode( node );
        }

        @Override
        public void caseCellIndexExpr( CellIndexExpr node )
        {
            rewrite(node.getTarget());
            if( rewriteArgList( node.getArgs() )){
                newNode = new TransformedNode( node );
            }
        }

        public boolean rewriteArgList( List<Expr> args )
        {
            boolean change = false;
            if( !areAllVarsOrLiterals( args ) ){
                for(int i=0; i<args.getNumChild(); i++){
                    Expr arg = args.getChild(i);
                    if( !isLiteral(arg) ){
                        change = true;
                        args.setChild( makeTempAssign(arg, canCSLExpand(arg)), i);
                    }
                }
            }
            return change;
        }
    }
}
