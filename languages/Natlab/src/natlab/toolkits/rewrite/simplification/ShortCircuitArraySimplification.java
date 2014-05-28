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
import ast.AndExpr;
import ast.AssignStmt;
import ast.BinaryExpr;
import ast.ElseBlock;
import ast.Expr;
import ast.IfBlock;
import ast.IfStmt;
import ast.OrExpr;
import ast.Stmt;
import ast.WhileStmt;

/**
 * Simplifies Array short circuiting boolean expressions into explicit
 * control flow.
 * @author Jesse Doherty
 */
public class ShortCircuitArraySimplification extends AbstractSimplification
{

    public ShortCircuitArraySimplification( ASTNode tree,
                                            VFPreorderAnalysis kind )
    {
        super( tree, kind );
    }

    /**
     * Builds a singleton start set containing this class.
     */ 
    public static Set<Class<? extends AbstractSimplification>> getStartSet()
    {
        HashSet<Class<? extends AbstractSimplification>> set = new HashSet();
        set.add( ShortCircuitArraySimplification.class );
        return set;
    }

    public Set<Class<? extends AbstractSimplification>> getDependencies()
    {
        HashSet<Class<? extends AbstractSimplification>> dependencies = new HashSet();
        dependencies.add( SimpleIfSimplification.class );
        return dependencies;
    }

    /*
      if E1 op E2
      ...
      end
      ==========
      [E1 op E2, t1]
      if t1
      ...
      end

      where op is & or | and [t1 = E1 op E2] is the result of further
      simplifying

      Note: the SimpleIfSimplification is assumed to have been run,
      so, no elseif's 
    */
    public void caseIfStmt( IfStmt node )
    {
        LinkedList<Stmt> newStmts = null;
        TempFactory condFact = null;

        IfBlock ifBlock = node.getIfBlock(0);
        ast.List<Stmt> thenBody = ifBlock.getStmts();

        //prepare new stmts if condition is an and or an or
        Expr cond = ifBlock.getCondition();
        if( cond instanceof AndExpr ){
            condFact = TempFactory.genFreshTempFactory();
            newStmts = handleAnd( (AndExpr)cond, condFact );
        }
        else if( cond instanceof OrExpr ){
            condFact = TempFactory.genFreshTempFactory();
            newStmts = handleOr( (OrExpr)cond, condFact );
        }

        //rewrite bodies
        rewrite( thenBody );
        ElseBlock elseBlock = null;
        if( node.hasElseBlock() ){
            elseBlock = node.getElseBlock();
            rewrite( elseBlock );
        }

        //deal with new statements
        if( newStmts != null ){
            
            IfStmt newIfStmt;
            newIfStmt = ASTHelpers.newIfStmt( condFact.genNameExpr(), thenBody, elseBlock );
            newStmts.add( newIfStmt );
            newNode = new TransformedNode( newStmts );
        }
            
    }

    public void caseWhileStmt( WhileStmt node )
    {
    	
    	
    	
    	
        LinkedList<Stmt> newStmts = null;
        TempFactory condFact = null;
        ast.List<Stmt> body = node.getStmts();

        //prepare new stmts if condition is an and or an or
        Expr cond = node.getExpr();
        if( cond instanceof AndExpr ){
        	condFact = TempFactory.genFreshTempFactory();
            newStmts = handleAnd( (AndExpr)cond, condFact );
        }
        else if( cond instanceof OrExpr ){
            condFact = TempFactory.genFreshTempFactory();
            newStmts = handleOr( (OrExpr)cond, condFact );
        }

        //rewrite body
        rewrite( body );
        
        if( newStmts != null ){
        	/*Bug fix by vkumar5
        	 * Add newStmts at the end of the body 
        	 * to reset the condition
        	 */
            for(Stmt s : newStmts)
            {
            	body.add(s);
            }
            WhileStmt newWhileStmt;
            newWhileStmt = new WhileStmt( condFact.genNameExpr(), body );
            newStmts.add( newWhileStmt );
            newNode = new TransformedNode( newStmts );
        }
    }
        

    /**
     * Determines what to do with the cond and temporary. It will
     * either treat it as an or, and and, or just an expression. In
     * all cases it the resulting value will be assigned to the
     * temporary represented by the condFact. 
     */
    protected LinkedList<Stmt> handleExp( Expr cond, TempFactory condFact )
    {
        LinkedList<Stmt> newStmts;
        if( cond instanceof AndExpr ){
            newStmts = handleAnd( (AndExpr)cond, condFact );
        }
        else if( cond instanceof OrExpr ){
            newStmts = handleOr( (OrExpr)cond, condFact );
        }
        else{
            AssignStmt newAssignment = new AssignStmt( condFact.genNameExpr(),
                                                       cond );
            newAssignment.setOutputSuppressed( true );
            newStmts = new LinkedList();
            newStmts.add( newAssignment );
        }
        return newStmts;
    }

    /*
      [E1 & E2, t1]
      ==========
      [E1, t2]
      if t2
        [E2, t3]
        t1 = t2 & t3;
      else
        t1 = t2;
      end
     */
    protected LinkedList<Stmt> handleAnd( AndExpr cond, TempFactory condFact )
    {
        return handleAndOr( true, cond, condFact );
    }
    /*
      [E1 | E2, t1]
      ==========
      [E1, t2]
      if t2
        t1 = t2;
      else
        [E2, t3]
        t1 = t2 | t3;
      end
    */
    protected LinkedList<Stmt> handleOr( OrExpr cond, TempFactory condFact )
    {
        return handleAndOr( false, cond, condFact );
    }

    /*
      [E1 op E2, t1]
      ==========
      ---if op is &
      [E1, t2]
      if t2
        [E2,t3]
        t1 = t2 op t3;
      else
        t1 = t2;
      end
      ----------
      ---if op is |
      [E1, t2]
      if t2
        t1 = t2;
      else
        [E2,t3]
        t1 = t2 op t3;
      end
    */
    protected LinkedList<Stmt> handleAndOr( boolean isAnd, BinaryExpr cond, TempFactory condFact )
    {


        Expr E1 = cond.getLHS();
        Expr E2 = cond.getRHS();

        TempFactory lhsTFact = TempFactory.genFreshTempFactory(); //t2
        TempFactory rhsTFact = TempFactory.genFreshTempFactory(); //t3

        LinkedList<Stmt> E1List = handleExp( E1, lhsTFact ); //[E1, t2]
        LinkedList<Stmt> E2List = handleExp( E2, rhsTFact ); //[E2, t3]

        //t1 = t2 op t3
        Expr boolExpr;
        if( isAnd )
            boolExpr = new AndExpr( lhsTFact.genNameExpr(), rhsTFact.genNameExpr() );
        else
            boolExpr = new OrExpr( lhsTFact.genNameExpr(), rhsTFact.genNameExpr() );
        AssignStmt thenAssign;
        thenAssign = new AssignStmt( condFact.genNameExpr(), boolExpr );
        thenAssign.setOutputSuppressed( true );

        E2List.add( thenAssign ); 

        //t1 = t2
        AssignStmt elseAssign;
        elseAssign = new AssignStmt( condFact.genNameExpr(),
                                     lhsTFact.genNameExpr() );
        elseAssign.setOutputSuppressed( true );

        Expr newCondExpr;
        newCondExpr = lhsTFact.genNameExpr();

        //new if
        if( isAnd ){
            E1List.add( ASTHelpers.newIfStmt( newCondExpr,
                                              ASTHelpers.listToList( E2List ),
                                              new ast.List().add( elseAssign ) ) );
        } else {
            E1List.add( ASTHelpers.newIfStmt( newCondExpr,
                                              new ast.List().add( elseAssign ),
                                              ASTHelpers.listToList( E2List ) ) );
        }
            
        return E1List;

    }


}
