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

import natlab.DecIntNumericLiteralValue;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.rewrite.TempFactory;
import natlab.toolkits.rewrite.TransformedNode;
import ast.ASTNode;
import ast.AssignStmt;
import ast.ColonExpr;
import ast.EndExpr;
import ast.Expr;
import ast.ForStmt;
import ast.IntLiteralExpr;
import ast.MatrixExpr;
import ast.Name;
import ast.NameExpr;
import ast.Opt;
import ast.ParameterizedExpr;
import ast.RangeExpr;
import ast.Stmt;


/**
 * Simplifies for statements. Reduces them so that there are only
 * simple range for loops. 
 *
 * @author Jesse Doherty
 */
public class ForSimplification extends AbstractSimplification
{

    public ForSimplification( ASTNode tree,
                              VFPreorderAnalysis kind )
    {
        super( tree, kind );
    }

    public Set<Class<? extends AbstractSimplification>> getDependencies()
    {
        return new HashSet();
    }

    /**
     * Builds a singleton start set containing this class.
     */
    public static Set<Class<? extends AbstractSimplification>> getStartSet()
    {
        //can suppress since it is a fresh HashSet
        @SuppressWarnings("unchecked")
        HashSet<Class<? extends AbstractSimplification>> dependencies = new HashSet();
        dependencies.add( ForSimplification.class );
        return dependencies;
    }

    /*
      for v=E
        ...
      end
      ========== if E not var
      t1=E;
      [t2,t3]=size(t1);
      i=[];
      for t4=1:t3
        i=t1(:,t4);
        ...
      end
     */
    public void caseForStmt( ForStmt node )
    {
        ast.List<Stmt> body = node.getStmts();

        AssignStmt assignStmt = node.getAssignStmt();

        Expr rhs = assignStmt.getRHS();
        Expr lhs = assignStmt.getLHS();
        if( !(rhs instanceof RangeExpr) ){
            
            LinkedList<Stmt> newStmts = new LinkedList();

            //build new temp assignments
            TempFactory t2F, t3F, t4F;

            //setup variables for two uses of the loop domain
            NameExpr[] t1Uses = new NameExpr[2];
            prepDomain( t1Uses, rhs, newStmts );

            t2F = TempFactory.genFreshTempFactory();
            t3F = TempFactory.genFreshTempFactory();
            t4F = TempFactory.genFreshTempFactory();
            

            AssignStmt t2t3Assign = ASTHelpers.buildMultiAssign( newSize(t1Uses[0]), true, 
                                                                 t2F.genNameExpr(),
                                                                 t3F.genNameExpr());
            newStmts.add( t2t3Assign );

            //build lvar assignments
            AssignStmt lvarDefaultAssign = new AssignStmt( (Expr)lhs.copy(), new MatrixExpr() );
            lvarDefaultAssign.setOutputSuppressed( true );
            newStmts.add( lvarDefaultAssign );

            AssignStmt lvarAssign = new AssignStmt( lhs, newParamFirstColon( t1Uses[1], t4F.genNameExpr() ) );
            lvarAssign.setOutputSuppressed( true );

            //build new for
            AssignStmt newLoopVar = new AssignStmt( t4F.genNameExpr(), new1ToExpr( t3F.genNameExpr() ) );
            newLoopVar.setOutputSuppressed( false );

            rewrite( body );
            body.insertChild( lvarAssign, 0 );
            ForStmt newFor = new ForStmt( newLoopVar, body );            

            newStmts.add( newFor );

            //set newNode
            newNode = new TransformedNode( newStmts );
        }
        else{
            rewrite( body );
        }
    }
    /**
     * Prepares the domain variable and uses for the loop. Takes an
     * array assumed to be length 2. It puts the two NameExpr for
     * domain uses in the array.
     * 
     * @param uses      where the vars for the domain uses are placed
     * @param domExpr   expr to construct the domain from
     * @param newStmts  where to put any new statements
     */
    protected void prepDomain( NameExpr[] uses, Expr domExpr, LinkedList<Stmt> newStmts )
    {
        NameExpr[] domainVar = TempFactory.genFreshTempNameExpr(3);
        uses[0] = domainVar[0];
        uses[1] = domainVar[1];
        AssignStmt tmpDomAssign = new AssignStmt( domainVar[2], domExpr );
        tmpDomAssign.setOutputSuppressed( true );
        newStmts.add( tmpDomAssign );
    }

    /**
     * Generates 1:expr for a given expr.
     */
    protected RangeExpr new1ToExpr( Expr stop )
    {
        DecIntNumericLiteralValue one = new DecIntNumericLiteralValue("1");
        return new RangeExpr( new IntLiteralExpr(one), new Opt(), stop );
    }

    /**
     * Generates 2:end
     */
    protected RangeExpr new2ToEnd()
    {
        DecIntNumericLiteralValue two = new DecIntNumericLiteralValue("2");
        return new RangeExpr( new IntLiteralExpr(two), new Opt(), new EndExpr() );
    }

    /**
     * Generates a new ParameterizedExpr with a single argument and a
     * given target.
     */
    protected ParameterizedExpr newParam( Expr target, Expr arg )
    {
        return new ParameterizedExpr( target, new ast.List<Expr>().add(arg) );
    }

    /**
     * Generates a new ParameterizedExpr with two arguments, the first
     * is a {@literal :} expression, and the second is given as {@code
     * arg}. 
     */
    protected ParameterizedExpr newParamFirstColon( Expr target, Expr arg )
    {
        return new ParameterizedExpr( target, new ast.List<Expr>().add(new ColonExpr()).add(arg) );
    }
    /**
     * Generates a new ParameterizedExpr with a single argument and a
     * given string as the target name.
     */
    protected ParameterizedExpr newFCall( String name, Expr arg )
    {
        Name FName = new Name(name);
        NameExpr fNE = new NameExpr( FName );
        return newParam( fNE, arg );
    }
    /**
     * Generates a new call to size.
     */
    protected ParameterizedExpr newSize( Expr arg )
    {
        return newFCall( "size", arg );
    }
    /**
     * Generates a new call to prod.
     */
    protected ParameterizedExpr newProd( Expr arg )
    {
        return newFCall( "prod", arg );
    }
    
}