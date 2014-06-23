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
import java.util.Set;

import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.rewrite.TempFactory;
import natlab.toolkits.rewrite.TransformedNode;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Expr;
import ast.LiteralExpr;
import ast.MatrixExpr;


/**
 * Simplifies assignment statements. Ensures that in an assignments
 * statement, between the left and right hand sides, there is at most
 * one complex operation. Either the left or right hand side must be a
 * variable or literal.
 *
 * @author Jesse Doherty
 */
public class SimpleAssignment extends AbstractSimplification
{
    public SimpleAssignment( ASTNode tree, VFPreorderAnalysis kind )
    {
        super( tree, kind );
    }

    /**
     * Builds a singleton start set containing this class.
     */ 
    public static Set<Class<? extends AbstractSimplification>> getStartSet()
    {
        HashSet<Class<? extends AbstractSimplification>> set = new HashSet();
        set.add( SimpleAssignment.class );
        return set;
    }

    public Set<Class<? extends AbstractSimplification>> getDependencies()
    {
        HashSet<Class<? extends AbstractSimplification>> dependencies = new HashSet();
        return dependencies;
    }

    /*
      Exp1 = Exp2
      =========
      t1 = Exp2;
      Exp1 = t1
      ---------
      if Exp1 and Exp2 are both not simple variables or Ex2 is not a literal

      Does not deal with multi assignment statements.
     */
    public void caseAssignStmt( AssignStmt node )
    {
        Expr lhs = node.getLHS();
        Expr rhs = node.getRHS();
        if( !(lhs instanceof MatrixExpr) ){
            if( !isVar(lhs)  )
                if( !(rhs instanceof LiteralExpr) && !isVar(rhs) ){
                    TempFactory tempF = TempFactory.genFreshTempFactory();
                    AssignStmt a1 = new AssignStmt( tempF.genNameExpr(), rhs );
                    AssignStmt a2 = new AssignStmt( lhs, tempF.genNameExpr() );
                    
                    newNode = new TransformedNode( a1 );
                    newNode.add(a2);
                }
        }
    }

}
