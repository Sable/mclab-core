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
        
        LinkedList<AssignStmt> newAssignments = new LinkedList();
        newAssignments.add(node);
        newAssignments = processAssignmentList( newAssignments,
                                                nameResolver.getInFlowSets().get(node) );

        if( newAssignments.size() > 0 ){
            newNode = new TransformedNode( newAssignments );
        }
    }

    public void caseExprStmt( ExprStmt node )
    {

        rewriteChildren( node );

        // Expr lhs = node.getLHS();
        // Expr rhs = node.getRHS();
        Expr expr = node.getExpr();
        ExpressionCollector ec;
        ec = new ExpressionCollector( expr, nameResolver.getInFlowSets().get(node) );

        Expr newExpr = (Expr)ec.transform();
        if( ec.getNewAssignments().size() > 0 ){
            LinkedList<AssignStmt> newAssignments;
            newAssignments = processAssignmentList( ec.getNewAssignments(),
                                                    nameResolver.getInFlowSets().get(node) );
            newNode = new TransformedNode( newAssignments );
            node.setExpr( newExpr );
            newNode.add( node );
        }
    }

    public void caseForStmt( ForStmt node )
    {
        rewrite( node.getStmts() );

        AssignStmt loopAssign = node.getAssignStmt();
        Expr rhs = loopAssign.getRHS();


        ExpressionCollector ec;
        ec = new ExpressionCollector( rhs, nameResolver.getInFlowSets().get(loopAssign) );

        Expr newRHS = (Expr)ec.transform();

        if( ec.getNewAssignments().size() > 0 ){
            LinkedList<AssignStmt> newAssignments;
            newAssignments = processAssignmentList( ec.getNewAssignments(),
                                                    nameResolver.getInFlowSets().get(loopAssign) );
            newNode = new TransformedNode( newAssignments );
            loopAssign.setRHS( newRHS );
            newNode.add( node );
        }
    }

    
    public LinkedList<AssignStmt> processAssignmentList( LinkedList<AssignStmt> assignList, 
                                                   VFFlowset<String,VFDatum> resolvedNames )
    {

        LinkedList<AssignStmt> newAssignList = new LinkedList();
        Expr lhs;
        Expr rhs;
        ExpressionCollector ec;
        while( assignList.size() > 0 ){
            AssignStmt node = assignList.remove(0);
            lhs = node.getLHS();
            rhs = node.getRHS();
            ec = new ExpressionCollector( rhs, resolvedNames );

            Expr newRHS = (Expr)ec.transform();
            if( ec.getNewAssignments().size() > 0 ){
                LinkedList<AssignStmt> generatedList = ec.getNewAssignments();
                generatedList = processAssignmentList( generatedList, resolvedNames );
                newAssignList.addAll(generatedList);
                node.setRHS( newRHS );
            }

            newAssignList.add( node );
        }
        return newAssignList;
    }
                
}
