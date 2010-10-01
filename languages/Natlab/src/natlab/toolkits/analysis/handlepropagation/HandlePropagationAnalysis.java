package natlab.toolkits.analysis.handlepropagation;


import ast.*;
import java.util.*;
import natlab.toolkits.analysis.*;

/**
 * This is an intraprocedural function handle propagation analysis. 
 * For every statement it computes mapping from variable name strings
 * to possible handle targets. If no entry exists then nothing is
 * known about that variable name, it could not be a handle, or it
 * could be a handle with no information associated with it such as
 * input parameters.
 */
public class HandlePropagationAnalysis extends AbstractSimpleStructuralForwardAnalysis< HandleFlowset >
{

    public MayMustTreeSet<HandleTarget> newHandleTargets = new MayMustTreeSet();
    public TreeSet<HandleTarget> allHandleTargets = new TreeSet();
    private boolean inAssignment = false;

    public HandleFlowset newInitialFlow()
    {
        return new HandleFlowset();
    }
    
    public HandlePropagationAnalysis( ASTNode tree )
    {
        super( tree );
        currentOutSet = newInitialFlow();
        //DEBUG = true;
    }
    
    public void copy( HandleFlowset source, HandleFlowset dest)
    {
        source.copy(dest);
    }

    /**
     * Merge is union because computing all possible targets for handles.
     */
    public void merge( HandleFlowset in1, HandleFlowset in2, HandleFlowset out)
    {
        in1.union(in2, out);
    }

    //Begin cases

    //store flow sets for all stmts. do it after analyzing it's
    //children. This is because handle expressions will cause a copy
    //so you want to store the copy of the flowset in those cases and
    //not a copy otherwise.
    public void caseStmt( Stmt node )
    {
        //inFlowSets.put(node,currentInSet.clone() );
        inAssignment = false;
        inFlowSets.put(node, currentInSet);
        caseASTNode( node );
        outFlowSets.put(node, currentOutSet);
    }
    public void caseExprStmt( ExprStmt node )
    {
        inFlowSets.put(node, currentInSet);
        outFlowSets.put(node, currentOutSet);
    }
    public void caseGlobalStmt( GlobalStmt node )
    {
        inFlowSets.put(node, currentInSet);
        outFlowSets.put(node, currentOutSet);
    }
    public void casePersistentStmt( PersistentStmt node )
    {
        inFlowSets.put(node, currentInSet);
        outFlowSets.put(node, currentOutSet);
    }

    public void caseFunctionHandleExpr( FunctionHandleExpr node )
    {
        //note this is wrong due to the implicit ans assignment
        HandleTarget target = new NamedHandleTarget( node.getName().getID() );
        newHandleTargets.add( target );
        allHandleTargets.add( target );
    }
    public void caseLambdaExpr( LambdaExpr node )
    {
        //note this is wrong due to the implicit ans assignment
        if( inAssignment ){
            HandleTarget target = new AnonymousHandleTarget( node );
            newHandleTargets.add( target );
            allHandleTargets.add( target );
        }
    }
    public void caseNameExpr( NameExpr node )
    {
        //note this is wrong due to the implicit ans assignment
        if( inAssignment ){
            MayMustTreeSet<HandleTarget> namesTargets;
            namesTargets = currentInSet.get( node.getName().getID() );
            if( namesTargets != null )
                newHandleTargets.addAll( namesTargets );
            else{
                newHandleTargets.makeMay();
            }
        }
    }
    public void caseRangeExpr( RangeExpr node)
    {
        return;
    }
    public void caseLiteralExpr( LiteralExpr node)
    {
        return;
    }
    public void caseUnaryExpr( UnaryExpr node)
    {
        //this assumes that Unary operators have not been overridden
        //to return a function handle when given a handle
        return;
    }
    public void caseBinaryExpr( BinaryExpr node)
    {
        //this assumes that Binary operators have not been overridden
        //to return a function handle when given handles
        return;
    }
    //param, cellindex, dot, matrix, cellArray
    //  set to may of Exp primary symbol or of {} if no val exists for it
    public void caseAssignStmt( AssignStmt node )
    {
        inAssignment = true;
        HandleFlowset tmpInSet = currentInSet.clone();
        HandleFlowset newOutSet = tmpInSet.clone();
        boolean change = false;

        Collection<String> lvalues = node.getLValues();
        killKeys( lvalues, newOutSet );

        newHandleTargets = new MayMustTreeSet( true );
        analyze( node.getRHS() );
        
        for( String lv : lvalues )
            newOutSet.addAll( lv, newHandleTargets );
        currentOutSet = newOutSet;
        inFlowSets.put(node, currentInSet);
        outFlowSets.put(node, currentOutSet);
        inAssignment = false;
    }
    protected boolean killKeys( Collection<String> keys, HandleFlowset set )
    {
        boolean change = false;
        for( String s : keys )
            change = false || set.removeByKey( s );
        return change;
    }

}