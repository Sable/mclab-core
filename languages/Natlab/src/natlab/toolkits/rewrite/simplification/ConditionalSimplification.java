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
import ast.BinaryExpr;
import ast.EQExpr;
import ast.ElseBlock;
import ast.Expr;
import ast.GEExpr;
import ast.GTExpr;
import ast.IfBlock;
import ast.IfStmt;
import ast.LEExpr;
import ast.LTExpr;
import ast.LiteralExpr;
import ast.NEExpr;
import ast.NotExpr;
import ast.Stmt;
import ast.WhileStmt;


/**
 * Simplifies while and if so that the condition only contains
 * variable uses or simple binary comparison or ~ of variable uses.
 * @author Jesse Doherty
 */
public class ConditionalSimplification extends AbstractSimplification
{
    public ConditionalSimplification( ASTNode tree, VFPreorderAnalysis kind )
    {
        super( tree, kind );
    }

    /**
     * Builds a singleton start set containing this class.
     */ 
    public static Set<Class<? extends AbstractSimplification>> getStartSet()
    {
        HashSet<Class<? extends AbstractSimplification>> set = new HashSet();
        set.add( ConditionalSimplification.class );
        return set;
    }

    public Set<Class<? extends AbstractSimplification>> getDependencies()
    {
        HashSet<Class<? extends AbstractSimplification>> dependencies = new HashSet();
        dependencies.add( ShortCircuitArraySimplification.class );
        return dependencies;
    }

    /*
      while E
        ...
      end
      ==========
      t1 = E;
      while t1
        ...
        t1 = E;
      end
     */
    public void caseWhileStmt( WhileStmt node )
    {
        Expr cond = node.getExpr();
        rewrite( node.getStmts() );
        
        if( !isSimpleCond( cond ) ){

            CondSplitter splitter = new CondSplitter( cond );
            java.util.List<Stmt> newStmts = new LinkedList(splitter.getNewAssigns());

            ast.List<Stmt> newBody = node.getStmts();
            for( Stmt s : splitter.getNewAssigns() )
                newBody.add((Stmt)s.copy());

            WhileStmt newWhile = new WhileStmt( splitter.getNewCond(), newBody );
            newStmts.add( newWhile );
            
            newNode = new TransformedNode( newStmts );


            /*
            LinkedList<Stmt> newStmts = new LinkedList();
            TempFactory tempF = TempFactory.genFreshTempFactory();
            Expr condClone = (Expr)cond.copy();
            
            AssignStmt condAssignment1, condAssignment2;
            condAssignment1 = new AssignStmt( tempF.genNameExpr(), cond );
            condAssignment2 = new AssignStmt( tempF.genNameExpr(), condClone );
            condAssignment1.setOutputSuppressed( true );
            condAssignment2.setOutputSuppressed( true );

            newStmts.add( condAssignment1 );
            
            WhileStmt newWhile = new WhileStmt(  tempF.genNameExpr(),
                                                 node.getStmts().add(condAssignment2) );
            newStmts.add( newWhile );
            
            newNode = new TransformedNode( newStmts );
            */
        }
            
    }

    /*
      if E
        ...
      else
        ...
      end
      ==========
      t1 = E;
      if t1
        ...
      else
        ...
      end

      Note: simple if simplification is assumed to have been run, this
      means that there are no else ifs. 
     */
    public void caseIfStmt( IfStmt node )
    {
        rewriteChildren( node );

        IfBlock ifblock = node.getIfBlock(0);

        Expr cond = ifblock.getCondition(); //E

        if( !isSimpleCond( cond ) ){
            CondSplitter splitter = new CondSplitter( cond );
            java.util.List<Stmt> newStmts = new LinkedList(splitter.getNewAssigns());

            ElseBlock elseBlock = null;
            if( node.hasElseBlock() )
                elseBlock = node.getElseBlock();

            IfStmt newIfStmt;
            newIfStmt = ASTHelpers.newIfStmt( splitter.getNewCond(), ifblock.getStmts(), elseBlock );
            
            newStmts.add( newIfStmt );
            
            newNode = new TransformedNode( newStmts );
        }
        //otherwise do nothing
    }

    private boolean isValue( Expr expr )
    {
        return expr instanceof LiteralExpr;
    }
    private boolean isVarOrValue( Expr expr )
    {
        return isValue(expr) || isVar(expr);
    }

    private boolean isRelOp( Expr expr )
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

    private boolean isSimpleRelOp( Expr expr )
    {
        if( isRelOp( expr ) ){
            BinaryExpr bexpr = (BinaryExpr)expr;
            return isVarOrValue(bexpr.getLHS()) && isVarOrValue(bexpr.getRHS());
        }
        else
            return false;
    }

    private boolean isNeg( Expr expr )
    {
        return expr instanceof NotExpr;
    }

    private boolean isSimpleNeg( Expr expr )
    {
        if( isNeg( expr ) ){
            NotExpr notExpr = (NotExpr)expr;
            return isVarOrValue(notExpr.getOperand());
        }
        else
            return false;
    }

    private boolean isSimpleCond( Expr expr )
    {
        return isVarOrValue(expr) || isSimpleNeg(expr) || isSimpleRelOp(expr);
    }


    private class CondSplitter
    {
        private Expr newCond;
        private java.util.List<Stmt> newAssignments;
        private CondSplitter( Expr cond ){
            if( isSimpleCond( cond ) )
                throw new IllegalArgumentException("Condition shouldn't be simple.");
            newAssignments = new LinkedList<Stmt>();

            if( isNeg( cond ) ){
                TempFactory t1 = TempFactory.genFreshTempFactory();
                NotExpr notExpr = (NotExpr)cond;
                newAssignments.add( ASTHelpers.suppressOutput( new AssignStmt( t1.genNameExpr(), 
                                                                               notExpr.getOperand() ) ) );
                newCond = new NotExpr(t1.genNameExpr());
            }
            else if( isRelOp( cond ) ){
                TempFactory t1 = TempFactory.genFreshTempFactory();
                TempFactory t2 = TempFactory.genFreshTempFactory();
                
                BinaryExpr binExpr = (BinaryExpr)cond;
                
                if( !isVarOrValue( binExpr.getLHS() )){
                    newAssignments.add( ASTHelpers.suppressOutput( new AssignStmt( t1.genNameExpr(), 
                                                                                   binExpr.getLHS() )));
                    binExpr.setLHS( t1.genNameExpr() );
                }
                if( !isVarOrValue( binExpr.getRHS() )){
                    newAssignments.add( ASTHelpers.suppressOutput( new AssignStmt( t2.genNameExpr(), 
                                                                                   binExpr.getRHS() )));
                    binExpr.setRHS( t2.genNameExpr() );
                }
                newCond = binExpr;
            }
            else{
                TempFactory t1 = TempFactory.genFreshTempFactory();
                newAssignments.add( ASTHelpers.suppressOutput( new AssignStmt( t1.genNameExpr(), cond ) ) );
                newCond = t1.genNameExpr();
            }
            
        }

        public java.util.List<Stmt> getNewAssigns()
        { return newAssignments; }
        public Expr getNewCond()
        { return newCond; }
    }
}
