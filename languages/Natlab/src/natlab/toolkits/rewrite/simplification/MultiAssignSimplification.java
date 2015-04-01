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

package natlab.toolkits.rewrite.simplification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.rewrite.TempFactory;
import natlab.toolkits.rewrite.TransformedNode;
import ast.ASTNode;
import ast.AssignStmt;
import ast.CSLExpr;
import ast.Expr;
import ast.MatrixExpr;
import ast.NameExpr;
import ast.Row;


/**
 * Simplifies multi-assignments to only contain simple variables, CSL
 * variables, and only a single occurrence of each variable.
 *
 * @author Jesse Doherty
 */
public class MultiAssignSimplification extends AbstractSimplification
{
    public MultiAssignSimplification( ASTNode tree, VFPreorderAnalysis kind )
    {
        super( tree, kind );
    }

    /**
     * Builds a singleton start set containing this class.
     */ 
    public static Set<Class<? extends AbstractSimplification>> getStartSet()
    {
        HashSet<Class<? extends AbstractSimplification>> set = new HashSet();
        set.add( MultiAssignSimplification.class );
        return set;
    }

    public Set<Class<? extends AbstractSimplification>> getDependencies()
    {
        HashSet<Class<? extends AbstractSimplification>> dependencies = new HashSet();
        dependencies.add( CommaSepListLeftSimplification.class );
        return dependencies;
    }
    
    public void caseAssignStmt( AssignStmt node )
    {
        rewriteChildren( node );

        if( node.getLHS() instanceof MatrixExpr ){
            //NOTE: assume lhs is a matrix expr with exactly 1 row
            Row lhs = ((MatrixExpr)node.getLHS()).getRow(0);
            Expr rhs = node.getRHS();
            
            boolean notSimplified;
            notSimplified = !(lhs.getNumElement() <= 1 && rhs instanceof CSLExpr);
            if( notSimplified ){

                LinkedList<AssignStmt> newStmts = new LinkedList<AssignStmt>();
                ArrayList<Expr> lvalues = new ArrayList<Expr>( lhs.getNumElement() );
                
                buildTempStatements( newStmts, lvalues, lhs.getElements() );
                
                if( newStmts.size() > 0 ){
                    AssignStmt newAssign = ASTHelpers.buildMultiAssign( lvalues, rhs, 
                                                                        node.isOutputSuppressed() );
                    newStmts.add( 0, newAssign );
                    newNode = new TransformedNode( newStmts );
                }
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
                                      ast.List<Expr> elements )
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

}
