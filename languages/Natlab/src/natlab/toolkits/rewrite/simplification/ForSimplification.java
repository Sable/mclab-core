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

    /*
      for v=E
        ...
      end
      ========== if E not var
      t1=E;
      t2=size(t1);
      t3=prod(t2(2:end));
      for t4=1:t3
        i = t1(t4);
        ...
      end
      ========== if E is var
      t1=size(E);
      t2=prod(t1(2:end));
      for t3=1:t2
        i = E(t3);
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
            TempFactory sizeF, stopF, tlvarF;
            Expr domainVal1, domainVal2; //two uses of loop domain value

            //setup variables for two uses of the loop domain
            NameExpr[] domUses = new NameExpr[2];
            prepDomain( domUses, rhs, newStmts );

            sizeF = TempFactory.genFreshTempFactory();
            stopF = TempFactory.genFreshTempFactory();
            tlvarF = TempFactory.genFreshTempFactory();

            AssignStmt sizeAssign = new AssignStmt( sizeF.genNameExpr(), newSize(domUses[0]) );
            AssignStmt stopAssign = new AssignStmt( stopF.genNameExpr(), 
                                                    newProd( newParam( sizeF.genNameExpr(),
                                                                       new2ToEnd() ) ) );
            sizeAssign.setOutputSuppressed( true );
            stopAssign.setOutputSuppressed( true );

            newStmts.add( sizeAssign );
            newStmts.add( stopAssign );

            //build lvar assignment
            AssignStmt lvarAssign = new AssignStmt( lhs, newParam( domUses[1], tlvarF.genNameExpr() ) );
            lvarAssign.setOutputSuppressed( true );

            //build new for
            AssignStmt newLoopVar = new AssignStmt( tlvarF.genNameExpr(), new1ToExpr( stopF.genNameExpr() ) );

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
        if( isVar( domExpr )){
            NameExpr domainVar = (NameExpr)domExpr;
            uses[0] = domainVar;
            uses[1] = new NameExpr( new Name(domainVar.getName().getID()) );
        }
        else{
            NameExpr[] domainVar = TempFactory.genFreshTempNameExpr(3);
            uses[0] = domainVar[0];
            uses[1] = domainVar[1];
            AssignStmt tmpDomAssign = new AssignStmt( domainVar[2], domExpr );
            tmpDomAssign.setOutputSuppressed( true );
            newStmts.add( tmpDomAssign );
        }
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
        return new ParameterizedExpr( target, new ast.List().add(arg) );
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