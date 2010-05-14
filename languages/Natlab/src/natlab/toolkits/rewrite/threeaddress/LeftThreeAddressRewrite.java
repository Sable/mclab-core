package natlab.toolkits.rewrite.threeaddress;

import java.util.LinkedList;

import ast.*;
import natlab.toolkits.analysis.AbstractNodeCaseHandler;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.analysis.varorfun.VFStructuralForwardAnalysis;
import natlab.toolkits.analysis.varorfun.VFFlowset;
import natlab.toolkits.analysis.varorfun.VFDatum;


public class LeftThreeAddressRewrite extends AbstractLocalRewrite
{
    private VFStructuralForwardAnalysis nameResolver;

    public LeftThreeAddressRewrite( ASTNode tree )
    {
        super( tree );
    }

    public void caseProgram( Program node )
    {
        nameResolver = new VFStructuralForwardAnalysis( node );
        nameResolver.analyze();
        rewriteChildren( node );
    }

    public void caseAssignStmt( AssignStmt node )
    {
        rewriteChildren( node );
        Expr lhs = node.getLHS();
        Expr rhs = node.getRHS();
        ExpressionCollector ec = new ExpressionCollector( lhs, nameResolver.getInFlowSets().get(node) );

        Expr newLHS = (Expr)ec.transform();
        if( ec.getNewAssignments().size() > 0 ){
            newNode = new TransformedNode( ec.getNewAssignments() );
            node.setLHS( newLHS );
            newNode.add( node );
        }
    }
        
        

    /**
     * Used to collect sub expressions from a left hand side
     * expression. For each appropriate sub expression it builds a new
     * assignment statement to a temporary and puts it in a list. It
     * then replaces the expression with the temporary that was
     * created for the assignment statement.
     */
    private class ExpressionCollector extends AbstractLocalRewrite
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
        public void caseNameExpr( NameExpr node )
        {
            if( isSub ){
                VFDatum datum = resolvedNames.contains( node.getName().getID() );
                if( datum == null || !datum.isVariable() ){
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
                
    }
}