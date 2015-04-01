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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.rewrite.TempFactory;
import natlab.toolkits.rewrite.TransformedNode;
import ast.ASTNode;
import ast.AssignStmt;
import ast.CellIndexExpr;
import ast.DotExpr;
import ast.Expr;
import ast.MatrixExpr;
import ast.Row;
import ast.Stmt;


/**
 * Makes comma separated list expansion explicit on the left hand side
 * of assignments. This will only modify multi-assignment statements. 
 *
 * This simplification introduces the non-syntactic CSLExpr node.
 * @author Jesse Doherty
 */
public class CommaSepListLeftSimplification extends AbstractSimplification
{
    public CommaSepListLeftSimplification( ASTNode tree, VFPreorderAnalysis kind )
    {
        super( tree, kind );
    }

    /**
     * Builds a singleton start set containing this class.
     */ 
    public static Set<Class<? extends AbstractSimplification>> getStartSet()
    {
        HashSet<Class<? extends AbstractSimplification>> set = new HashSet();
        set.add( CommaSepListLeftSimplification.class );
        return set;
    }

    public Set<Class<? extends AbstractSimplification>> getDependencies()
    {
        HashSet<Class<? extends AbstractSimplification>> dependencies = new HashSet();
        return dependencies;
    }


    /*
      [..., E1, ...] = E2
      ==========
      [...,CSL{t1},...] = E2;
      [E1] = CSL{t1};

      Where E1 is an expression that could possibly expand to a comma
      separated list, such as a struct access or a cell access. 

      Note: Since ForStmts should only have simple variables on the
      lhs of their AssignStmts, this rule will not cause trouble in
      for loops.
     */
    public void caseAssignStmt( AssignStmt node )
    {
        Expr lhs = node.getLHS();

        if( lhs instanceof MatrixExpr ){

            ast.List<Expr> lhsExprs = ((MatrixExpr)lhs).getRow(0).getElements(); 
            
            LinkedList<Stmt> newStmts = new LinkedList();
            LinkedList<Expr> lvalues = new LinkedList();
            buildTempStatements( newStmts, lvalues, lhsExprs );

            if( newStmts.size()>0 ){
                newStmts.add( 0, ASTHelpers.buildMultiAssign( lvalues, node.getRHS(), 
                                                              node.isOutputSuppressed() ) );
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
    private void buildTempStatements( LinkedList<Stmt> outNewStmts, 
                                      LinkedList<Expr> outLValues, 
                                      ast.List<Expr> elements )
    {
        for( Expr e : elements ){
            if( e instanceof CellIndexExpr ||
                e instanceof DotExpr ){
                
                TempFactory tmp = TempFactory.genFreshTempFactory();
                outLValues.add( tmp.genCSLExpr() );
                MatrixExpr newMat = new MatrixExpr( 
                                                   new ast.List().add( 
                                                                      new Row( 
                                                                              new ast.List().add( e )
                                                                               )
                                                                       )
                                                    );
                AssignStmt newAssign = new AssignStmt( newMat, tmp.genCSLExpr() );
                newAssign.setOutputSuppressed( true );
                outNewStmts.add( newAssign );
            }
            else{
                outLValues.add( e );
            }
        }
    }
    

}
