package natlab.toolkits.rewrite.simplification;


import java.util.*;

import ast.*;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.analysis.varorfun.*;

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
      [t1 = E1 op E2]
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
            newIfStmt = newIfStmt( condFact.genNameExpr(), thenBody, elseBlock );
            newStmts.add( newIfStmt );
            newNode = new TransformedNode( newStmts );
        }
            
    }

    protected LinkedList<Stmt> handleAnd( AndExpr cond, TempFactory condFact )
    {
        AssignStmt tmpAssign = new AssignStmt( condFact.genNameExpr(), cond );
        LinkedList<Stmt> newAssings = new LinkedList();
        newAssings.add( tmpAssign );
        return newAssings;
    }
    protected LinkedList<Stmt> handleOr( OrExpr cond, TempFactory condFact )
    {
        AssignStmt tmpAssign = new AssignStmt( condFact.genNameExpr(), cond );
        LinkedList<Stmt> newAssings = new LinkedList();
        newAssings.add( tmpAssign );
        return newAssings;
    }

    /**
     * Generates a simple If then else.
     */
    protected IfStmt newIfStmt( Expr cond, ast.List<Stmt> thenBody, ast.List<Stmt> elseBody )
    {
        ElseBlock elseBlock = null;
        if( elseBody != null )
            elseBlock = new ElseBlock( elseBody );
        return newIfStmt( cond, thenBody, elseBlock );
    }
    /**
     * Generates a simple if then else.
     */
    protected IfStmt newIfStmt( Expr cond, ast.List<Stmt> thenBody, ElseBlock elseBlock )
    {
        return newIfStmt( new IfBlock( cond, thenBody ), elseBlock );
    }
    /**
     * Generates a simple if then else.
     */
    protected IfStmt newIfStmt( IfBlock ifBlock, ElseBlock elseBlock )
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
    protected IfStmt newIfStmt( IfBlock ifBlock, Opt<ElseBlock> elseOpt )
    {
        return new IfStmt( new ast.List().add( ifBlock ), elseOpt );
    }

}
