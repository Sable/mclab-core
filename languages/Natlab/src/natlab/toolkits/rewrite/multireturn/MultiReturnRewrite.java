package natlab.toolkits.rewrite.multireturn;

import ast.*;
import natlab.toolkits.analysis.AbstractNodeCaseHandler;
import natlab.toolkits.rewrite.*;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.ArrayList;

public class MultiReturnRewrite extends AbstractLocalRewrite
{

    public MultiReturnRewrite( ASTNode tree )
    {
        super( tree );
    }



    public void caseAssignStmt( AssignStmt node )
    {
        rewriteChildren( node );
        
        
        // Stmt[] stmts = new Stmt[2];
        // Expr lhs = node.getLHS();
        // Expr rhs = node.getRHS();

        // System.out.println( lhs.getClass() );
        
        // Name[] tmps = TempFactory.genFreshTempName(2);
            
        // stmts[0] = new AssignStmt( new NameExpr( tmps[0] ), rhs );
        // stmts[1] = new AssignStmt( lhs, new NameExpr( tmps[1] ) );

        // stmts[0].setOutputSuppressed( true );
        // stmts[1].setOutputSuppressed( node.isOutputSuppressed() );
        // newNode = new TransformedNode( stmts );
        

        if( node.getLHS() instanceof MatrixExpr ){
            //NOTE: assume lhs is a matrix expr with exactly 1 row
            Row lhs = ((MatrixExpr)node.getLHS()).getRow(0);
            Expr rhs = node.getRHS();

            HashSet<String> names = new HashSet<String>(lhs.getNumElement());
            LinkedList<AssignStmt> newStmts = new LinkedList<AssignStmt>();
            ArrayList<Expr> lvalues = new ArrayList<Expr>( lhs.getNumElement() );

            for( Expr e : lhs.getElements() ){
                if( e instanceof NameExpr ){
                    String name = ((NameExpr)e).getName().getID();
                    if( !names.contains( name ) ){
                        names.add(name);
                        lvalues.add( e );
                        continue;
                    }
                }
                TempFactory tmp = TempFactory.genFreshTempFactory();
                lvalues.add( tmp.genNameExpr() );
                AssignStmt newAssign = new AssignStmt( e, tmp.genNameExpr() );
                newAssign.setOutputSuppressed( true );
                newStmts.add( newAssign );
            }
            if( newStmts.size() > 0 ){
                Row newLHS = new Row( new List() );
                for( Expr e : lvalues )
                    newLHS.getElements().add( e );
                AssignStmt newAssign = new AssignStmt( new MatrixExpr( (new List()).add(newLHS) ),
                                                       rhs );
                newAssign.setOutputSuppressed( node.isOutputSuppressed() );
                newStmts.add( 0, newAssign );
                newNode = new TransformedNode( newStmts );
            }
        }
    }
}