// =========================================================================== //
//                                                                             //
// Copyright 2011 Jesse Doherty and McGill University.                         //
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
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

package natlab.toolkits.rewrite.multireturn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import natlab.toolkits.rewrite.AbstractLocalRewrite;
import natlab.toolkits.rewrite.TempFactory;
import natlab.toolkits.rewrite.TransformedNode;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Expr;
import ast.List;
import ast.MatrixExpr;
import ast.NameExpr;
import ast.Row;

/**
 * A rewrite to simplify multi return assignment statements. The
 * rewrite results in all multi return assignments having only simple
 * name expressions appearing at most once on the left hand side. It
 * replaces all complex lvalues with temporaries and then assigns the
 * temporaries to the appropriate lvalue afterwards. 
 *
 * Note: this does not simplify the complex lvalue expressions at
 * all. This is left to other rewrites.
 */
public class MultiReturnRewrite extends AbstractLocalRewrite
{

    public MultiReturnRewrite( ASTNode tree )
    {
        super( tree );
    }

    public void caseAssignStmt( AssignStmt node )
    {
        rewriteChildren( node );

        if( node.getLHS() instanceof MatrixExpr ){
            //NOTE: assume lhs is a matrix expr with exactly 1 row
            Row lhs = ((MatrixExpr)node.getLHS()).getRow(0);
            Expr rhs = node.getRHS();

            LinkedList<AssignStmt> newStmts = new LinkedList<AssignStmt>();
            ArrayList<Expr> lvalues = new ArrayList<Expr>( lhs.getNumElement() );

            buildTempStatements( newStmts, lvalues, lhs.getElements() );

            if( newStmts.size() > 0 ){
                AssignStmt newAssign = buildNewAssignment( lvalues, rhs, node.isOutputSuppressed() );
                newStmts.add( 0, newAssign );
                newNode = new TransformedNode( newStmts );
            }
        }
    }

    /**
     * Builds up the lists of new assignment statemens and a list of
     * variable names to use in multi return assignment. 
     *
     * @param outNewStmts   the list to filled with new assignment
     *                      statements
     * @param outLValues    the list to be filled with variable names
     * @param elements      AST List expressions from the lhs
     */
    private void buildTempStatements( LinkedList<AssignStmt> outNewStmts, 
                                      ArrayList<Expr> outLValues, 
                                      List<Expr> elements )
    {
        HashSet<String> names = new HashSet<String>(elements.getNumChild());

        for( Expr e : elements ){
            if( e instanceof NameExpr ){
                String name = ((NameExpr)e).getName().getID();
                if( !names.contains( name ) ){
                    names.add(name);
                    outLValues.add( e );
                    continue;
                }
            }
            TempFactory tmp = TempFactory.genFreshTempFactory();
            outLValues.add( tmp.genNameExpr() );
            AssignStmt newAssign = new AssignStmt( e, tmp.genNameExpr() );
            newAssign.setOutputSuppressed( true );
            outNewStmts.add( newAssign );
        }
    }
    
    /**
     * Builds the new assignment statement to replace the original
     * multi return assignment.
     *
     * @param lvalues          list of lvalues for new assignment lhs
     * @param rhs              the rhs expression of original assignment
     * @param outputSuppressed boolean used to suppress output of new
     *                         assignment 
     *
     * @return the new AssignStmt generated
     */
    private AssignStmt buildNewAssignment( ArrayList<Expr> lvalues, Expr rhs, boolean outputSuppressed )
    {
        Row newLHS = new Row( new List() );
        for( Expr e : lvalues )
            newLHS.getElements().add( e );
        AssignStmt newAssign = new AssignStmt( new MatrixExpr( (new List()).add(newLHS) ),
                                               rhs );
        newAssign.setOutputSuppressed( outputSuppressed );
        return newAssign;
    }
}