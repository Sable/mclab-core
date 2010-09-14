package natlab.toolkits.rewrite.threeaddress;

import java.util.LinkedList;
import java.lang.NullPointerException;

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
        ExpressionCollector ec = null;
        try{
            ec = new ExpressionCollector( lhs, nameResolver.getInFlowSets().get(node) );
        }catch(NullPointerException e){
            rethrowWithMoreInfoNR( nameResolver, e );
        }

        Expr newLHS = (Expr)ec.transform();
        if( ec.getNewAssignments().size() > 0 ){
            newNode = new TransformedNode( ec.getNewAssignments() );
            node.setLHS( newLHS );
            newNode.add( node );
        }
    }

    private void rethrowWithMoreInfoNR( VFStructuralForwardAnalysis nr, NullPointerException e)
    {
        if( nr==null || nr.getInFlowSets()==null ){
            String m = "nameResolver not initialized correctly, was this rewriter constructed with an " +
                "ASTNode containing a Program node? Because it's supposed to be!";
            throw new NullPointerException(m);
        }
        else
            throw e;
    }
}