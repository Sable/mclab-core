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

import java.util.*;

import ast.*;
import natlab.DecIntNumericLiteralValue;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.analysis.varorfun.*;


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
        
        if( !isVar( cond ) ){
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

        if( !isVar( cond ) ){
            LinkedList<Stmt> newStmts = new LinkedList();
            TempFactory tempF = TempFactory.genFreshTempFactory(); //t1
            
            //t1 = E;
            AssignStmt condAssign = new AssignStmt( tempF.genNameExpr(), cond );
            condAssign.setOutputSuppressed( true );

            newStmts.add( condAssign );

            ElseBlock elseBlock = null;
            if( node.hasElseBlock() )
                elseBlock = node.getElseBlock();

            IfStmt newIfStmt;
            newIfStmt = ASTHelpers.newIfStmt( tempF.genNameExpr(), ifblock.getStmts(), elseBlock );
            
            newStmts.add( newIfStmt );

            newNode = new TransformedNode( newStmts );
        }
    }
}
