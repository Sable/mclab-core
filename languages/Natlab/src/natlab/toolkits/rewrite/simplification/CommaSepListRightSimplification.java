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
import ast.CSLExpr;
import ast.CellArrayExpr;
import ast.CellIndexExpr;
import ast.DotExpr;
import ast.Expr;
import ast.ForStmt;
import ast.MatrixExpr;
import ast.ParameterizedExpr;
import ast.Stmt;


/**
 * Make comma separated list expansion explicit on the right hand side
 * of assignments. 
 * 
 * This simplification introduces the non-syntactic CSL node.
 * @author Jesse Doherty
 */
public class CommaSepListRightSimplification extends AbstractSimplification
{

    protected LinkedList<Stmt> newStmts = null;

    /**
     * Keeps track of if an appropriate expression can expand to a
     * comma separated list.
     */
    protected boolean canExpand = false;

    public CommaSepListRightSimplification( ASTNode tree, VFPreorderAnalysis kind )
    {
        super( tree, kind );
    }

    /**
     * Builds a singleton start set containing this class.
     */ 
    public static Set<Class<? extends AbstractSimplification>> getStartSet()
    {
        HashSet<Class<? extends AbstractSimplification>> set = new HashSet();
        set.add( CommaSepListRightSimplification.class );
        return set;
    }

    public Set<Class<? extends AbstractSimplification>> getDependencies()
    {
        HashSet<Class<? extends AbstractSimplification>> dependencies = new HashSet();
        return dependencies;
    }

    /*
      E0 = E1(...,E2,...)
      ==========
      [CSL{t1}] = E2;
      E0 = E1(...,CSL{t1},...);

      Where E1 is an expression that could possibly expand to a comma
      separated list, such as a struct access or a cell access.
    */
    public void caseAssignStmt( AssignStmt node )
    {
        setupStmts();
        rewrite( node.getRHS() );
        makeNewNodeIfNeeded( node );
    }

    
    /*
      E1(...,E2,...)
      ==========
      [CSL{t1}] = E2;
      E1(...,CSL{t1},...);

      Where E1 is an expression that could possibly expand to a comma
      separated list, such as a struct access or a cell access.
    */    
    public void caseStmt( Stmt node )
    {
        setupStmts();
        rewriteChildren( node );
        makeNewNodeIfNeeded( node );
    }

    /**
     * Special case for ForStmts so the loop variable assignment
     * statement does not get captured by the caseAssignStmt. 
     */
    public void caseForStmt( ForStmt node )
    {
        rewrite( node.getStmts() );
        setupStmts();
        rewrite( node.getAssignStmt().getRHS() );
        makeNewNodeIfNeeded( node );
    }

    /*
      Some cases to mark when expanding can happen;
    */
    public void caseMatrixExpr( MatrixExpr node )
    {
        caseContainsExpand( node );
    }
    public void caseCellArrayExpr( CellArrayExpr node )
    {
        caseContainsExpand( node );
    }

    public void caseParameterizedExpr( ParameterizedExpr node )
    {
        //if( node.getTarget() instanceof DotExpr )
        caseParamAndCellIndExpr( node, node.getTarget(), node.getArgs(), false );
        //else
        //caseContainsExpand( node );
    }

    public void caseCellIndexExpr( CellIndexExpr node )
    {
        caseParamAndCellIndExpr( node, node.getTarget(), node.getArgs(), true );
    }
    /**
     * Deals with ParameterizedExpr and CellIndexExpr. These two cases
     * are basically the same, but are not abstracted in the class
     * hierarchy.
     */
    public void caseParamAndCellIndExpr( Expr node, Expr target, ast.List<Expr> args, boolean expand )
    {
        boolean prevCanExpand = canExpand;
        canExpand = false;
        rewrite( target );
        canExpand = true;
        rewrite( args );
        if( expand && prevCanExpand )
            rewriteExpanding( node );
        canExpand = prevCanExpand;
    }
    /**
     * Deals with nodes that can contain expanding nodes and do not,
     * themselves expand.
     */
    protected void caseContainsExpand( Expr node )
    {
        canExpand = true;
        caseExpr( node );
    }

    public void caseDotExpr( DotExpr node )
    {
        if( canExpand )
            rewriteExpanding( node );
    }

    /**
     * Rewrites an expanding node. Sets newNode to contain the correct
     * csl temp and adds the temp assignment to the newStmts list.
     */
    protected void rewriteExpanding( Expr node )
    {
        TempFactory tempF = TempFactory.genFreshTempFactory();
        createNewAssign( node, tempF );
        newNode = new TransformedNode( tempF.genCSLExpr() );
    }
    /**
     * Creates a new CSL temp assignment and adds it to the list of
     * newStmts. 
     */
    protected void createNewAssign( Expr node, TempFactory tempF )
    {
        CSLExpr tmpCSL = tempF.genCSLExpr();
        AssignStmt newAssign = new AssignStmt( tmpCSL, node );
        newAssign.setOutputSuppressed( true );
        newStmts.add( newAssign );
    }

    /**
     * Does some setup for stmt cases.
     */
    protected void setupStmts()
    {
        newStmts = new LinkedList();
        canExpand = false;
    }

    /**
     * If there were new assignments generated, then add the given
     * node to the list of new stmts and create a new TransformedNode
     * assigned to newNode.
     *
     * Uses the newStmts list and assigns to newNode. 
     */
    protected void makeNewNodeIfNeeded( Stmt node )
    {
        if( !newStmts.isEmpty() ){
            newStmts.add( node );
            newNode = new TransformedNode( newStmts );
        }
    }
}
