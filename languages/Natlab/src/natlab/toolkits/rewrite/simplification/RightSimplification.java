package natlab.toolkits.rewrite.simplification;

import java.util.*;

import ast.*;
import natlab.DecIntNumericLiteralValue;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.rewrite.threeaddress.ExpressionCollector;
import natlab.toolkits.analysis.varorfun.*;


/**
 * Simplifies expressions so that there is at most one complex
 * operation, all operands must me literals or variables. This makes
 * short circuiting control flow explicit.
 * 
 * This simplification introduces the CheckScalar non-syntactic node for
 * short-circuiting expansion.
 * @author Jesse Doherty
 */
public class RightSimplification extends AbstractSimplification
{
    protected boolean inAssignStmt = false;
    protected boolean inExprStmt = false;

    public RightSimplification( ASTNode tree, VFPreorderAnalysis kind )
    {
        super( tree, kind );
    }

    /**
     * Builds a singleton start set containing this class.
     */ 
    public static Set<Class<? extends AbstractSimplification>> getStartSet()
    {
        HashSet<Class<? extends AbstractSimplification>> set = new HashSet();
        set.add( RightSimplification.class );
        return set;
    }

    public Set<Class<? extends AbstractSimplification>> getDependencies()
    {
        HashSet<Class<? extends AbstractSimplification>> dependencies = new HashSet();
        dependencies.add(LeftSimplification.class);
        dependencies.add(ForSimplification.class);
        dependencies.add(ConditionalSimplification.class);
        dependencies.add(SimpleAssignment.class);
        //dependencies.add(CommaSepListRightSimplification.class);
        return dependencies;
    }


    /*
      E1 && E2
      ==========
      t1 = E1;
      CheckScalarStmt(t1);
      if t1
        t2 = E2;
        CheckScalarStmt( t2 );
      else
        t2 = false;
      end
      ----------
      E1 || E2
      ==========
      t1 = E1;
      CheckScalarStmt(t1);
      if ~t1
        t2 = E2;
        CheckScalarStmt(t2);
      else
        t2 = false;
      end
     */

    protected LinkedList<Stmt> newStmts;

    /**
     * Takes note that it is in an ExprStmt, then forwards to
     * caseStmt. 
     */
    public void caseExprStmt( ExprStmt node )
    {
        caseStmt( node );

        inExprStmt = false;
    }

    /**
     * Takes note that it is in an AssignStmt, then forwards to
     * caseStmt or simplifySC. 
     */
    public void caseAssignStmt( AssignStmt node )
    {
        inAssignStmt = true;

        caseStmt( node );

        inAssignStmt = false;
    }
    protected boolean inSCStmt = false;

    
    public void caseForStmt(ForStmt node) {
        //rewrite statements
        rewrite(node.getStmts());
        
        //get stuff out of the assignment statement
        caseAssignStmt(node.getAssignStmt());
        if (newNode != null){ //if there was a change to the assign stmt
            AssignStmt assign = (AssignStmt)newNode.remove(newNode.size()-1);
            newNode.add(new ForStmt(assign, node.getStmts()));
        }
    }
    
    public void caseStmt( Stmt node )
    {
        newStmts = new LinkedList();
        rewriteChildren( node );
        if( !newStmts.isEmpty() ){
            newStmts.add( node );
            newNode = new TransformedNode( newStmts );
        }
    }

    String deep = "";
    public void caseExpr( Expr node )
    {
        //deep += "!";
        //System.out.println(deep + " " + node.getPrettyPrinted());
        ExpressionCollector ec = new ExpressionCollector( node, kindAnalysis );
        Expr newExpr = (Expr)ec.transform();

        LinkedList<Stmt> backNewStmts = newStmts;
        LinkedList<Stmt> exprsNewStmts = new LinkedList();

        //loop through all the new assignment stmts and rewrites them
        while( !ec.getNewAssignments().isEmpty() ){
            
            Stmt workStmt = ec.getNewAssignments().removeLast();
            //System.out.println( deep + "- " + workStmt.getPrettyPrinted() );
            rewrite( workStmt );
            if( newNode != null )
                if( newNode.isMultipleNodes() )
                    exprsNewStmts.addAll(0, (Collection)newNode.getMultipleNodes());
                else
                    exprsNewStmts.addFirst( (Stmt)newNode.getSingleNode() );
            else
                exprsNewStmts.addFirst( workStmt );
            
            //newStmts.addAll( ec.getNewAssignments() );
            //newNode = new TransformedNode( newExpr );

        }
        newStmts = backNewStmts;
        if( !exprsNewStmts.isEmpty() ){
            newStmts.addAll( exprsNewStmts );
            if( !inSCStmt )
                newNode = new TransformedNode( newExpr );
        }
        if( currentSCTempFact!=null ){
            AssignStmt newAssign = new AssignStmt( currentSCTempFact.genNameExpr(),
                                                   newExpr );
            newAssign.setOutputSuppressed(true);
            newStmts.add( newAssign );
            CheckScalarStmt newCheck = new CheckScalarStmt( currentSCTempFact.genNameExpr() );
            newCheck.setOutputSuppressed(true);
            newStmts.add( newCheck );
            newNode = new TransformedNode( currentSCTempFact.genNameExpr() );
        }

        //deep = deep.substring(0,deep.length()-1);
    }

    protected TempFactory currentSCTempFact;


    protected boolean setupSCSimp()
    {
        if( currentSCTempFact == null ){
            currentSCTempFact = TempFactory.genFreshTempFactory();
            return true;
        }
        else
            return false;
    }
    protected void teardownSCSimp(boolean isBase)
    {
        if( isBase )
            currentSCTempFact = null;
    }
    /**
     * Simplifies assignments with short-circuit expressions on the
     * rhs. 
     *
     * Should only be given an AssignStmt with a short-circuit
     * expression on the rhs.
     */
    protected void simplifySC( AssignStmt node )
    {
        Expr lhs = node.getLHS();
        Expr rhs = node.getRHS();


        currentSCTempFact = TempFactory.genFreshTempFactory();
    }

    protected void simplifySC( ExprStmt node )
    {
        Expr expr = node.getExpr();

        currentSCTempFact = TempFactory.genFreshTempFactory();
    }

    public void caseShortCircuitAndExpr( ShortCircuitAndExpr node )
    {
        Expr e1 = node.getLHS();
        Expr e2 = node.getRHS();

        simplifySCIfPat( e1, e2, false );
    }

    public void caseShortCircuitOrExpr( ShortCircuitOrExpr node )
    {
        Expr e1 = node.getLHS();
        Expr e2 = node.getRHS();

        simplifySCIfPat( new NotExpr(e1), e2, true );
    }

    public void caseNotExpr( NotExpr node )
    {
        Expr e = node.getOperand();

        if( e instanceof NotExpr )
            rewrite( ((NotExpr)e).getOperand() );
        else if( e instanceof ShortCircuitAndExpr ){
            Expr e1 = ((ShortCircuitAndExpr)e).getLHS();
            Expr e2 = ((ShortCircuitAndExpr)e).getRHS();

            simplifySCIfPat( e1, new NotExpr(e2), true );
        }
        else if( e instanceof ShortCircuitOrExpr ){
            Expr e1 = ((ShortCircuitOrExpr)e).getLHS();
            Expr e2 = ((ShortCircuitOrExpr)e).getRHS();

            caseShortCircuitAndExpr( new ShortCircuitAndExpr( new NotExpr( e1 ),
                                                              new NotExpr( e2 )));
        }
        else
            caseExpr(node);
    }

    protected void simplifySCIfPat( Expr e1, Expr e2, boolean elseVal )
    {
        boolean isBaseSC = setupSCSimp();
        LinkedList<Stmt> thenPartList = makeThenPart( e2 );
        simplifySCIfPat( e1, thenPartList, elseVal );
        teardownSCSimp(isBaseSC);
    }
    /**
     * Creates the then part contents for simplifying SC
     * expressions. This is used to transition from the expression
     * simplification to the if simplification for SC simplification.
     */
    protected LinkedList<Stmt> makeThenPart( Expr e )
    {
        LinkedList<Stmt> backupNewStmts = newStmts;
        newStmts = new LinkedList();

        rewrite(e);

        LinkedList<Stmt> thenPartList = newStmts;
        newStmts = backupNewStmts;
        return thenPartList;
    }
    protected void simplifySCIfPat( Expr e, LinkedList<Stmt> thenPart, boolean elseVal )
    {
        simplifySCIfPat( e, 
                         ASTHelpers.listToList(thenPart), elseVal );
        //new ast.List<Stmt>().add( boolAssign ) );
    }

    protected void simplifySCIfPat( Expr e, ast.List<Stmt> thenPart, boolean elseVal )
    {
        if( e instanceof ShortCircuitAndExpr ){
            Expr e1 = ((ShortCircuitAndExpr)e).getLHS();
            Expr e2 = ((ShortCircuitAndExpr)e).getRHS();

            LinkedList<Stmt> backupNewStmts = newStmts;
            newStmts = new LinkedList();

            
            simplifySCIfPat( e2, thenPart, elseVal );

            newStmts = backupNewStmts;

            simplifySCIfPat( e1, ASTHelpers.listToList(newStmts), elseVal );
        }
        else if( e instanceof NotExpr && ( ((NotExpr)e).getOperand() instanceof ShortCircuitOrExpr )){
            ShortCircuitOrExpr orExpr = (ShortCircuitOrExpr)((NotExpr)e).getOperand();
            Expr e1 = orExpr.getLHS();
            Expr e2 = orExpr.getRHS();
            ShortCircuitAndExpr andExpr = new ShortCircuitAndExpr( new NotExpr(e1), new NotExpr(e2) );

            simplifySCIfPat( andExpr, thenPart, elseVal );
            return;
        }
        else{
            rewrite(e);
            e = currentSCTempFact.genNameExpr();
            AssignStmt boolAssign = new AssignStmt(currentSCTempFact.genNameExpr(), 
                                                   ASTHelpers.buildBoolLit(elseVal) );
            boolAssign.setOutputSuppressed(true);

            newStmts.add( ASTHelpers.newIfStmt( e, thenPart, new ast.List<Stmt>().add(boolAssign) ) );
        }

    }
}
