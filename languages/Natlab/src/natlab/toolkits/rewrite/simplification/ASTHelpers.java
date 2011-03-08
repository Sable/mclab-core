package natlab.toolkits.rewrite.simplification;

import java.util.*;

import ast.*;

/**
 * A collection of helper methods for working with AST nodes.
 *
 * @author Jesse Doherty
 */
public class ASTHelpers
{
    /**
     * Generate an AST list from a java.util.list.
     */
    public static <T extends ASTNode> ast.List<T> listToList( java.util.List<T> l )
    {
        ast.List<T> newL = new ast.List();
        for( T e : l )
            newL.add( e );
        return newL;
    }
    /**
     * Generates a simple If then else.
     */
    public static IfStmt newIfStmt( Expr cond, ast.List<Stmt> thenBody, ast.List<Stmt> elseBody )
    {
        ElseBlock elseBlock = null;
        if( elseBody != null )
            elseBlock = new ElseBlock( elseBody );
        return newIfStmt( cond, thenBody, elseBlock );
    }
    /**
     * Generates a simple if then else.
     */
    public static IfStmt newIfStmt( Expr cond, ast.List<Stmt> thenBody, ElseBlock elseBlock )
    {
        return newIfStmt( new IfBlock( cond, thenBody ), elseBlock );
    }
    /**
     * Generates a simple if then else.
     */
    public static IfStmt newIfStmt( IfBlock ifBlock, ElseBlock elseBlock )
    {
        Opt<ElseBlock> elseOpt;
        if( elseBlock == null )
            elseOpt = new Opt();
        else
            elseOpt = new Opt(elseBlock);
        return newIfStmt( ifBlock, elseOpt );
    }
    /**
     * Generates a simple if then else.
     */
    public static IfStmt newIfStmt( IfBlock ifBlock, Opt<ElseBlock> elseOpt )
    {
        return new IfStmt( new ast.List().add( ifBlock ), elseOpt );
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
    public static AssignStmt buildMultiAssign( java.util.List<Expr> lvalues, Expr rhs, boolean outputSuppressed )
    {
        Row newLHS = new Row( new ast.List() );
        for( Expr e : lvalues )
            newLHS.getElements().add( e );
        AssignStmt newAssign = new AssignStmt( new MatrixExpr( new ast.List().add(newLHS) ),
                                               rhs );
        newAssign.setOutputSuppressed( outputSuppressed );
        return newAssign;
    }


    /**
     * Tests if a given expression is a scalar short-circuit
     * expression. 
     */
    public static boolean isScalarSC( Expr node )
    {
        if(node instanceof ShortCircuitAndExpr)
            return true;
        else if( node instanceof ShortCircuitOrExpr )
            return true;
        else if( node instanceof NotExpr )
            return isScalarSC( ((NotExpr)node).getOperand() );
        else
            return false;
    }

    /**
     * Builds a new boolean literal expression.
     */
    public static NameExpr buildBoolLit( boolean value )
    {
        if( value )
            return new NameExpr( new Name( "true" ) );
        else
            return new NameExpr( new Name( "false" ) );
    }


}
