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
package natlab.toolkits.rewrite;

import java.util.HashSet;
import java.util.Set;

import analysis.AbstractDepthFirstAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.BinaryExpr;
import ast.DotExpr;
import ast.EQExpr;
import ast.EndCallExpr;
import ast.Expr;
import ast.ForStmt;
import ast.GEExpr;
import ast.GTExpr;
import ast.IfBlock;
import ast.IfStmt;
import ast.LEExpr;
import ast.LTExpr;
import ast.LiteralExpr;
import ast.MatrixExpr;
import ast.NEExpr;
import ast.NameExpr;
import ast.NotExpr;
import ast.ParameterizedExpr;
import ast.RangeExpr;
import ast.UnaryExpr;
import ast.WhileStmt;

/**
 * @author Jesse Doherty
 */
public class Validator extends AbstractDepthFirstAnalysis<Boolean>
{
    private boolean pass = true;
    private StringBuilder failureReasons = new StringBuilder();

    /**
     * Used to tell if, at a given point, the analysis is in the lhs
     * of an assignment. This should be maintained by analysis code.
     */
    private boolean inAssignLHS = false;

    /**
     * Used to query if it is in the lhs of an assignment. 
     */
    private boolean inAssignmentLHS()
    {
        return inAssignLHS;
    }

    /**
     * Sets whether or not the analysis is in the lhs of an
     * assignment. 
     */
    private void setInAssignmentLHS( boolean inLHS )
    {
        inAssignLHS = inLHS;
    }

    private void fail()
    {
        pass = false;
    }

    private void fail(ASTNode node)
    {
        failureReasons.append( "failed on "+ node +" "+ node.getPrettyPrinted() + "\n" );
        fail();
    }
    private void fail(ASTNode node, String reason)
    {
        failureReasons.append( "failed on "+ node +" "+ node.getPrettyPrinted() + "\n" );
        failureReasons.append( "   reason: " + reason + "\n");
        fail();
    }
    public Validator( ASTNode tree )
    {
        super(tree);
    }

    public boolean isValid()
    {
        return pass;
    }
    public String getReasons()
    {
        return failureReasons.toString();
    }

    @Override
    public Boolean newInitialFlow()
    {
        return Boolean.TRUE;
    }

    public void caseWhileStmt( WhileStmt node )
    {
        caseCondition( node.getExpr() );
        caseASTNode( node.getStmts() );
    }
    @Override
    public void caseIfBlock( IfBlock node )
    {
        caseCondition( node.getCondition() );
        caseASTNode( node.getStmts() );
    }
    public void caseForStmt( ForStmt node )
    {
        caseLoopVar( node.getAssignStmt() );
        caseASTNode( node.getStmts() );
    }

    /**
     * Ensures assignments are simple, and the l- and r-hs are
     * valid. It also has the side effect of setting
     * {@code inAssignmentLHS} when analyzing the lhs.
     */
    public void caseAssignStmt( AssignStmt node )
    {
        if( node.getLHS() instanceof MatrixExpr )
            handleMultiAssigns( node, (MatrixExpr)node.getLHS(), node.getRHS() );
        else if( isName(node.getLHS()) || isNameOrValue(node.getRHS()) ){
            if( !isName(node.getLHS()) ){
                setInAssignmentLHS( true );
                analyze( node.getLHS() );
                setInAssignmentLHS( false );
            }
            if( !isNameOrValue( node.getRHS() ) )
                analyze( node.getRHS() );
        }
        else
            fail(node);
    }

    /**
     * Ensures that multi-assignment statements are valid. They are
     * valid if they only contain {@code NameExpr}s and names are not
     * repeated. 
     */
    public void handleMultiAssigns( AssignStmt node, MatrixExpr lhs, Expr rhs )
    {
        if( lhs.getNumRow() != 1 )
            fail(node, "LHS of multi-assignment has wrong number of rows");
        else{
            boolean passed = true;
            Set<String> names = new HashSet<String>();
            for( Expr e : lhs.getRow(0).getElements() ){
                if( !isName(e) ){
                    fail(node);
                    passed = false;
                    break;
                }
                else{
                    if( names.contains(((NameExpr)e).getName().getID()) ){
                        fail(node);
                        passed = false;
                        break;
                    }
                    else
                        names.add( ((NameExpr)e).getName().getID() );
                }
            }
            if( passed )
                analyze(rhs);
        }
    }
    /**
     * Default behaviour for expressions. Validation fails if this
     * case is reached.
     */
    public void caseExpr(Expr node)
    {
        fail(node);
    }

    /**
     * Passes validation for LiteralExpr if not in lhs of assignment.
     */
    public void caseLiteralExpr( LiteralExpr node )
    {
        if( inAssignmentLHS() )
            fail(node, "no literals on LHS of assignment");
    }
    /**
     * Passes validation for NameExpr. NameExprs always pass.
     * Note this also covers {@code CSLExpr}s
     */
    public void caseNameExpr( NameExpr node )
    {
        //always passes
    }

    /**
     * Returns a standard reason for when a non lvalue is used on the
     * lhs of an assignment. Reason is based on a string representing
     * the type of expression found.
     *
     * @param expr  The type of expression found
     */
    private String rvalueReason(String expr)
    {
        return "no "+expr+" expressions on LHS of assignments";
    }

    /**
     * Performs a test to see if on the lhs of an assignment. If it
     * is, then it fails for the given node with the given reason.
     *
     * @param node  The node currently being analyzed 
     * @param failReason  The reason given if test fails
     * @return Whether or not the test passed, false implies that
     * {@code fail(...)} was called.
     */
    private boolean lhsTest(ASTNode node, String failReason)
    {
        if( inAssignmentLHS() ){
            fail(node, failReason);
            return false;
        }
        else
            return true;
    }
    /**
     * Ensures that binary expressions are simple and that they are
     * not on the lhs of assignments. They are simple if
     * their left- and right- hand side's are names or values.
     */
    public void caseBinaryExpr( BinaryExpr node )
    {
        if( lhsTest(node, rvalueReason("binary")) )
            if( !( isNameOrValue(node.getLHS()) && isNameOrValue(node.getRHS())) )
                fail(node);
    }

    /**
     * Ensures that unary operations are simple and that they are not
     * on the LHS of assignments. They are simple if their operand is
     * a name or value.
     */
    public void caseUnaryExpr( UnaryExpr node )
    {
        if( lhsTest( node, rvalueReason("unary")) )
            if( !isNameOrValue(node.getOperand()) )
                fail(node);
    }
    /**
     * Ensures that range expressions are valid and that they are not
     * on the LHS of assignments. They are simple if all it's children
     * are names or values
     */
    public void caseRangeExpr( RangeExpr node )
    {
        if( lhsTest( node, rvalueReason("range")) )
            if( !( isNameOrValue(node.getLower()) &&
                   isNameOrValue(node.getUpper()) ) )
                fail(node);
            else
                if( node.hasIncr() )
                    if( !isNameOrValue( node.getIncr() ) )
                        fail(node);
    }
    /**
     * Ensures that {@code EndCallExpr} are simple and do not appear
     * on lhs of assignments. They are simple if the array is a name.
     */
    public void caseEndCallExpr( EndCallExpr node )
    {
        if( lhsTest( node, rvalueReason("EndCall") ) )
            if( !isName(node.getArray()) )
                fail(node);
    }
    /**
     * Ensures that {@code ParameterizedExpr}s are simple. This
     * applies to both left- and right-hand sides. One is simple if
     * it's target is just a name or a simple field access and all
     * arguments are names or values.
     */
    public void caseParameterizedExpr( ParameterizedExpr node )
    {
        if( !allNameOrValue( node.getArgs() ) )
            fail(node);
        else if( !isName(node.getTarget()) )
            if( !( node.getTarget() instanceof DotExpr ) )
                fail(node);
            else
                //it is a DotExpr so let the that case deal with it
                analyze( node.getTarget() );
    }

    /**
     * Ensures that field accesses are simple. This differs depending
     * on whether or not it is on the left- or right-hand side of an
     * assignment. 
     * 
     * On the right-hand side, they can only be a single field deep,
     * and the target must be a name.
     *
     * On the left-hand side, they can be arbitrarily deep. The target
     * can be either a name, another {@code DotExpr} or a {@code
     * ParameterizedExpr}.
     */
    public void caseDotExpr( DotExpr node )
    {
        if( inAssignmentLHS() ){
            if( isName( node.getTarget() ) )
                return; //good so far
            else if( node.getTarget() instanceof ParameterizedExpr ||
                     node.getTarget() instanceof DotExpr )
                analyze(node.getTarget());
            else
                fail(node);
        }
        else
            if( !isName( node.getTarget() ) )
                fail(node);
    }
    /**
     * Ensures that if stmts are simple. They must have at most one
     * {@code IfBlock}. Also continues analysis of the contained
     * {@code IfBlock} and {@code ElseBlock} when the if is simple.
     */
    public void caseIfStmt( IfStmt node )
    {
        if( node.getNumIfBlock() > 1 )
            fail(node);
        else
            caseASTNode( node );
    }
    /**
     * Checks if a condition is simple. A condition is simple if it is
     * a name or value, the negation of a name or value, the result of
     * a relational operator.
     */    
    @Override
    public void caseCondition( Expr condExpr )
    {
        if( !isNameOrValue( condExpr ) ){
            if( isRelationalOp( condExpr ) ){
                Expr lhs = ((BinaryExpr)condExpr).getLHS();
                Expr rhs = ((BinaryExpr)condExpr).getRHS();
                if( !( isNameOrValue(lhs) && 
                       isNameOrValue(rhs)) )
                    //is a rel op and l- and r-hs are not both simple
                    fail(condExpr);
            }
            else if( condExpr instanceof NotExpr ){
                if( !isNameOrValue( ((NotExpr)condExpr).getOperand() ) )
                    //is a negation and operand is not simple
                    fail(condExpr);
            }
            else
                //is not a name or val nor a rel op nor a negation
                fail(condExpr);
        }
    }

    @Override
    public void caseLoopVar( AssignStmt lv )
    {
        if( lv.getRHS() instanceof RangeExpr )
            caseASTNode( lv );
        else
            fail(lv);
    }
    //Some helper methods

    //NOTE: CslExpr is considered a name
    private boolean isName( Expr expr )
    {
        return expr instanceof NameExpr;
    }

    private boolean isValue( Expr expr )
    {
        return expr instanceof LiteralExpr;
    }
    private boolean isNameOrValue( Expr expr )
    {
        return isName(expr) || isValue(expr);
    }
    private boolean allNames( ast.List<Expr> exprs )
    {
        for( Expr e : exprs )
            if( !isName( e ) )
                return false;
        return true;
    }
    private boolean allValues( ast.List<Expr> exprs )
    {
        for( Expr e : exprs )
            if( !isValue( e ) )
                return false;
        return true;
    }
    private boolean allNameOrValue( ast.List<Expr> exprs )
    {
        for( Expr e : exprs )
            if( !isNameOrValue( e ) )
                return false;
        return true;
    }
    private boolean isRelationalOp( Expr expr )
    {
        if( expr instanceof LTExpr )
            return true;
        if( expr instanceof GTExpr )
            return true;
        if( expr instanceof LEExpr )
            return true;
        if( expr instanceof GEExpr )
            return true;
        if( expr instanceof EQExpr )
            return true;
        if( expr instanceof NEExpr )
            return true;
        return false;
    }
}
