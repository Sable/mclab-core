package natlab.toolkits.rewrite.threeaddress;

import java.util.*;

import ast.*;
import natlab.toolkits.analysis.AbstractNodeCaseHandler;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.analysis.varorfun.VFStructuralForwardAnalysis;
import natlab.toolkits.analysis.varorfun.VFFlowset;
import natlab.toolkits.analysis.varorfun.VFDatum;

/**
 * Transforms and simplifies RValue expressions. Results in such
 * expressions containing at most one indexing, field access, operator
 * or function call. 
 */
public class RightThreeAddressRewrite extends AbstractLocalRewrite
{

    private VFStructuralForwardAnalysis nameResolver;


    public RightThreeAddressRewrite( ASTNode tree )
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
        ExpressionCollector ec;
        ec = new ExpressionCollector( rhs, nameResolver.getInFlowSets().get(node) );

        Expr newRHS = (Expr)ec.transform();
        if( ec.getNewAssignments().size() > 0 ){
            newNode = new TransformedNode( ec.getNewAssignments() );
            node.setRHS( newRHS );
            newNode.add( node );
        }
    }

        
}
