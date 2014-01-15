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
import java.util.Map;

import natlab.toolkits.analysis.varorfun.VFDatum;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.rewrite.AbstractLocalRewrite;
import natlab.toolkits.rewrite.TransformedNode;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Expr;
import ast.ExprStmt;
import ast.ForStmt;
import ast.Program;

import com.google.common.collect.Lists;

/**
 * Transforms and simplifies RValue expressions. Results in such
 * expressions containing at most one indexing, field access, operator
 * or function call. 
 */
public class RightThreeAddressRewrite extends AbstractLocalRewrite
{

    private VFPreorderAnalysis nameResolver;

    public RightThreeAddressRewrite( ASTNode tree )
    {
        super( tree );
    }

    public void caseProgram( Program node )
    {
        nameResolver = new VFPreorderAnalysis( node );
        nameResolver.analyze();
        rewriteChildren( node );
    }

    public void caseAssignStmt( AssignStmt node )
    {
        rewriteChildren( node );
        
        LinkedList<AssignStmt> newAssignments = Lists.newLinkedList();
        newAssignments.add(node);
        newAssignments = processAssignmentList( newAssignments,
                                                nameResolver.getFlowSets().get(node) );

        if( newAssignments.size() > 0 ){
            newNode = new TransformedNode( newAssignments );
        }
    }

    public void caseExprStmt( ExprStmt node )
    {
        rewriteChildren( node );
        Expr expr = node.getExpr();
        ExpressionCollector ec;
        ec = new ExpressionCollector( expr, nameResolver.getFlowSets().get(node) );

        Expr newExpr = (Expr)ec.transform();
        if( ec.getNewAssignments().size() > 0 ){
            LinkedList<AssignStmt> newAssignments;
            newAssignments = processAssignmentList( ec.getNewAssignments(),
                                                    nameResolver.getFlowSets().get(node) );
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

        ExpressionCollector ec =
            new ExpressionCollector( rhs, nameResolver.getFlowSets().get(loopAssign) );

        Expr newRHS = (Expr)ec.transform();

        if( ec.getNewAssignments().size() > 0 ){
            LinkedList<AssignStmt> newAssignments = processAssignmentList( ec.getNewAssignments(),
                nameResolver.getFlowSets().get(loopAssign) );
            newNode = new TransformedNode( newAssignments );
            loopAssign.setRHS( newRHS );
            newNode.add( node );
        }
    }

    /**
     * Recursively processes a list of assignments. Removes
     * sub-expressions into temporaries. 
     */
    public LinkedList<AssignStmt> processAssignmentList( LinkedList<AssignStmt> assignList, 
                                                   Map<String, VFDatum> resolvedNames )
    {
        LinkedList<AssignStmt> newAssignList = Lists.newLinkedList();
        Expr rhs;
        ExpressionCollector ec;
        while( assignList.size() > 0 ){
            AssignStmt node = assignList.remove(0);
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
