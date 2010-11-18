package natlab.toolkits.analysis.handlepropagation;


import ast.*;
import java.util.*;
import natlab.toolkits.analysis.*;
import natlab.toolkits.analysis.handlepropagation.handlevalues.*;
import natlab.toolkits.analysis.varorfun.*;

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
    //flags to controll behaviour of how function call are handled
    protected boolean destructiveCalls = false;
    protected boolean doTypeLookup = false;

    //public TreeSet<Value> newValues = new TreeSet();
    public TreeSet<Value> newHandleTargets = new TreeSet();

    //Set of global variable names.
    protected HashSet<String> globalNames;

    //current statement being operated on.
    protected Stmt currentStmt = null;


    protected boolean change = false;

    //The kind analysis used to compute values.
    protected VFStructuralForwardAnalysis kindAnalysis;

    public TreeSet<Value> allHandleTargets = new TreeSet();
    private boolean inAssignment = false;

    public HandleFlowset newInitialFlow()
    {
        return new HandleFlowset();
    }

    public HandlePropagationAnalysis( ASTNode tree )
    {
        super( tree );
        kindAnalysis = new VFStructuralForwardAnalysis( tree );
        kindAnalysis.analyze();
        currentOutSet = newInitialFlow();
    }
    //destructiveCalls=false;
    //doTypeLookup=false;
    public HandlePropagationAnalysis( ASTNode tree, boolean destructiveCalls, boolean doTypeLookup )
    {
        this(tree);
        this.destructiveCalls = destructiveCalls;
        this.doTypeLookup = doTypeLookup;
    }

    public HandlePropagationAnalysis( ASTNode tree, VFStructuralForwardAnalysis kind,
                                      boolean destructiveCalls, boolean doTypeLookup )
    {
        this(tree, kind);
        this.destructiveCalls = destructiveCalls;
        this.doTypeLookup = doTypeLookup;
    }

    public HandlePropagationAnalysis( ASTNode tree, VFStructuralForwardAnalysis kind )
    {
        super( tree );
        kindAnalysis = kind;
        if( !kind.isAnalyzed() )
            kind.analyze();
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

    /**
     * Function case sets up the in data for the function body. It
     * does this based on the in and out parameters and the identifier
     * information from the kind analysis.
     */
    public void caseFunction( Function node )
    {
        HandleFlowset tmpInSet = currentInSet.clone();
        HandleFlowset newOutSet = tmpInSet.clone();

        for( Name out : node.getOutputParams() )
            newOutSet.add(out.getID(), newUndefSet() );

        for( Name in : node.getInputParams() )
            newOutSet.add(in.getID(), newGeneralSet() );


        inFlowSets.put( node, tmpInSet );
        currentInSet = newOutSet;
        caseASTNode(node);
        outFlowSets.put( node, currentOutSet.clone());
    }



    //store flow sets for all stmts. do it after analyzing it's
    //children. This is because handle expressions will cause a copy
    //so you want to store the copy of the flowset in those cases and
    //not a copy otherwise.
    public void caseStmt( Stmt node )
    {
        //inFlowSets.put(node,currentInSet.clone() );
        inAssignment = false;
        boolean oldChange = change;
        change = false;
        currentStmt = node;
        HandleFlowset backupIn =(HandleFlowset)currentInSet.clone();
        caseASTNode( node );
        if( change )
            inFlowSets.put(node, backupIn);
        else
            inFlowSets.put(node, currentInSet);
            
        outFlowSets.put(node, (HandleFlowset)currentOutSet.clone());
        change = oldChange||change;
    }
    /*public void caseExprStmt( ExprStmt node )
    {
        inAssignment = false;
        boolean oldChange = change
        currentStmt = node;
        inFlowSets.put(node, currentInSet);
        outFlowSets.put(node, currentOutSet);
        }*/
    public void caseGlobalStmt( GlobalStmt node )
    {
        
        inFlowSets.put(node, currentInSet);
        HandleFlowset newOut = currentInSet.clone();

        for( Name n : node.getNames() ){
            newOut.add(n.getID(), newGeneralSet());
        }
        currentOutSet = newOut;
        outFlowSets.put(node, currentOutSet);
    }
    public void casePersistentStmt( PersistentStmt node )
    {
        inFlowSets.put(node, currentInSet);
        HandleFlowset newOut = currentInSet.clone();

        for( Name n : node.getNames() ){
            newOut.add(n.getID(), newGeneralSet());
        }
        currentOutSet = newOut;
        outFlowSets.put(node, currentOutSet);
    }

    public void caseFunctionHandleExpr( FunctionHandleExpr node )
    {
        //note this is wrong due to the implicit ans assignment
        Value target = new NamedHandleValue( node.getName().getID() );
        newHandleTargets.add( target );
        allHandleTargets.add( target );
    }
    public void caseLambdaExpr( LambdaExpr node )
    {
        //note this is wrong due to the implicit ans assignment
        if( inAssignment ){
            Value target = new AnonymousHandleValue( node );
            newHandleTargets.add( target );
            allHandleTargets.add( target );
        }
    }

    /**
     * There are 4 cases for what to generate for a NameExpr.
     * 1) a copy of the values of the NameExpr if the kind of the NameExpr
     * is a variable.
     * 2) {H,DWH} if it is a function
     * 3) a copy of the values can't be undefined
     * 4) {H,DWH} unioned with the values of the NameExpr except for
     * undefined values,  other wise
     *
     * Note that case 2 and 4 are treated as function calls. 
     */
    public void caseNameExpr( NameExpr node )
    {
        currentOutSet = currentInSet;
        VFFlowset kinds =  kindAnalysis.getInFlowSets().get(currentStmt);
        if( kinds != null ){
            VFDatum kind = kinds.contains(node.getName().getID() );
            if( kind != null ){
                //case 1
                if(kind.isVariable()){
                    TreeSet<Value> namesTargets = new TreeSet();
                    namesTargets.addAll(currentInSet.get( node.getName().getID() ));
                    namesTargets.remove(AbstractValue.newUndef());
                    newHandleTargets.addAll( namesTargets );
                    return;
                }
                //case 2
                else if(kind.isFunction()){
                    newHandleTargets.addAll( handleFunctionCall( node.getName().getID() ) );
                    return;
                }
            }
        }
        //case 3
        if( !isIdUndef(node.getName().getID()) ){
            newHandleTargets.addAll( currentInSet.get(node.getName().getID()) );
            return;
        }
        
        //Other wise
        TreeSet<Value> newValues = handleFunctionCall( node.getName().getID() );
        TreeSet<Value> idIn = currentInSet.get(node.getName().getID());
        if( idIn != null )
            newValues.addAll( idIn );
        newValues.remove(AbstractValue.newUndef());
        newHandleTargets.addAll( newValues );

    }

    /**
     * The arguments need to be analyzed. If the target is not a
     * NameExpr then analyze the target. If the target is a NameExpr
     * then there are 2 cases. 
     *
     * 1) the name has kind NFN and is treated as a function call
     * giving values dependent on the function call
     * 2) otherwise it has some value and the values generated are the
     * union for all values v of:
     * (a) {H,DWH} if v is some sort of handle
     * (b) {H,DWH} if v=U and kind(name)!=VAR, or 
     * (c) simply v otherwise.
     */
    public void caseParameterizedExpr( ParameterizedExpr node )
    {
        
        currentOutSet= currentInSet;
        for( Expr arg : node.getArgs() ){
            analyze(arg);
        }
        if( node.getTarget() instanceof NameExpr ){
            NameExpr nameExpr = (NameExpr)node.getTarget();
            VFFlowset kinds =  kindAnalysis.getInFlowSets().get(currentStmt);
            if( kinds != null ){
                VFDatum kind = kinds.contains(nameExpr.getName().getID() );
                if( kind != null ){
                    // Case 1
                    if( kind.isFunction() ){
                        newHandleTargets.addAll( handleFunctionCall( nameExpr.getName().getID() ) );
                        return;
                    }
                }
            }
            //Case 2
            for( Value v : currentInSet.get( nameExpr.getName().getID() ) ){
                //case 2.a
                if( v instanceof NamedHandleValue ){
                    NamedHandleValue nhv = (NamedHandleValue)v;
                    newHandleTargets.addAll( handleFunctionCall( nhv.getName() ) );
                    break;
                }
                if( v instanceof AnonymousHandleValue ){
                    newHandleTargets.addAll( handleUnknownFunctionCall() );
                    break;
                }
                if( v instanceof AbstractValue ){
                    AbstractValue av = (AbstractValue)v;
                    if( av.isHandle() ){
                        newHandleTargets.addAll( handleUnknownFunctionCall() );
                        break;
                    }
                    //case 2.b
                    else if( av.isUndef() ){
                        if( kinds != null ){
                            VFDatum kind = kinds.contains(nameExpr.getName().getID() );
                            if( kind != null ){
                                if( !kind.isVariable() ){
                                    newHandleTargets.addAll( handleFunctionCall( nameExpr.getName().getID() ));
                                    break;
                                }
                            }
                        }
                    }
                }
                newHandleTargets.add(v);
            }
        }
        //if it isn't a name expr, analyze the target.
        else{
            analyze(node.getTarget());
        }
    }
    public void caseRangeExpr( RangeExpr node)
    {
        newHandleTargets.addAll( newDOSet() );
    }
    public void caseLiteralExpr( LiteralExpr node)
    {
        newHandleTargets.addAll( newDOSet() );
    }
    public void caseUnaryExpr( UnaryExpr node)
    {
        //this assumes that Unary operators have not been overridden
        //to return a function handle when given a handle
        newHandleTargets.addAll( newDOSet() );
        return;
    }
    public void caseBinaryExpr( BinaryExpr node)
    {
        //this assumes that Binary operators have not been overridden
        //to return a function handle when given handles
        newHandleTargets.addAll( newDOSet() );
        return;
    }
    //param, cellindex, dot, matrix, cellArray
    //  set to may of Exp primary symbol or of {} if no val exists for it
    public void caseAssignStmt( AssignStmt node )
    {
        inAssignment = true;
        currentStmt = node;
        boolean oldChange = change;
        change = false;

        HandleFlowset tmpInSet = currentInSet.clone();
        HandleFlowset newOutSet = tmpInSet.clone();

        Collection<String> lvalues = node.getLValues();

        currentOutSet = newOutSet;
        newHandleTargets = new TreeSet();
        analyze( node.getRHS() );

        killKeys( lvalues, newOutSet );

        for( String lv : lvalues ){
            newOutSet.addAll( lv, newHandleTargets );
        }

        inFlowSets.put(node, tmpInSet);
        outFlowSets.put(node, currentOutSet.clone());
        inAssignment = false;
        change = oldChange || change;
    }
    protected boolean killKeys( Collection<String> keys, HandleFlowset set )
    {
        boolean change = false;
        for( String s : keys )
            change = false || set.removeByKey( s );
        return change;
    }


    /** Converts the values in a set into the struct data versions. So
     * if these values were in a struct, the result represents the
     * struct's possible values.
     */
    public TreeSet<Value> makeStructDataSet( TreeSet<Value> set)
    {
        if( set.size() == 0 )
            return new TreeSet();
        else if( set.size() == 1 && set.contains( AbstractValue.newDataOnly() ) )
            return (TreeSet)set.clone();
        else{
            boolean allHandles = true;
            // test if all values are handles
            for( Value v : set ){
                if( !((v instanceof AbstractValue && 
                       ((AbstractValue)v).isHandle()) ||
                      v instanceof NamedHandleValue ||
                      v instanceof AnonymousHandleValue )){
                    allHandles = false;
                    break;
                }
            }
            TreeSet newSet = new TreeSet();
            if( allHandles )
                newSet.add( AbstractValue.newDataHandleOnly() );
            else
                newSet.add( AbstractValue.newDataWithHandles() );
            return newSet;
        }
    }

    /** Gets values from a set, but only structured data values. This
     * also means that the set returned will have at most one value,
     * since all sets can have at most one structured data value.
     */
    public TreeSet<Value> getStructuredDataOnly( TreeSet<Value> set )
    {
        TreeSet<Value> newSet = new TreeSet();
        
        if(set.contains(AbstractValue.newDataOnly()))
            newSet.add( AbstractValue.newDataOnly());
        if(set.contains(AbstractValue.newDataHandleOnly()))
            newSet.add( AbstractValue.newDataHandleOnly());
        if(set.contains(AbstractValue.newDataWithHandles()))
            newSet.add( AbstractValue.newDataWithHandles());
        return newSet;
    }

    /** 
     * Builds a version of the most general set for assigned values, a
     * set containing {H,DWH}
     */
    protected TreeSet<Value> newGeneralAssignedSet()
    {
        TreeSet<Value> set = new TreeSet();
        set.add( AbstractValue.newHandle() );
        set.add( AbstractValue.newDataWithHandles());
        return set;
    }
    /**
     * Builds a version of the most general set for values that can be
     * unassigned. This set is {UNDEF,H,DWH}
     */
    protected TreeSet<Value> newGeneralSet()
    {
        TreeSet<Value> set = new TreeSet();
        set.add( AbstractValue.newHandle() );
        set.add( AbstractValue.newDataWithHandles());
        set.add( AbstractValue.newUndef());
        return set;
    }
    
    /**
     * Builds a set containing only UNDEF.
     */
    protected TreeSet<Value> newUndefSet()
    {
        TreeSet<Value> set = new TreeSet();
        set.add( AbstractValue.newUndef());
        return set;
    }

    /**
     * Builds a set containing only DataOnly.
     */
    protected TreeSet<Value> newDOSet()
    {
        TreeSet<Value> set = new TreeSet();
        set.add( AbstractValue.newDataOnly());
        return set;
    }
    /**
     * Checks if a given id has a possible undefined value. This is
     * done in the context of the current stmt. An id has such a value
     * if it's set of possible values contains UNDEF or if it has no
     * possible values and it's kind is ID (Bottom) or VAR.
     */
    protected boolean isIdUndef( String id )
    {
        TreeSet<Value> values = currentInSet.get(id);
        if( values != null && values.contains( AbstractValue.newUndef() ) )
            return true;
        else if( values == null || values.size()==0){
            VFDatum kind = kindAnalysis.getInFlowSets().get(currentStmt).contains(id);
            if( kind == null || kind.isBottom() || 
                kind.isVariable() )
                return true;
        }
        return false;
    }

    /**
     * Deals with the affects of function calls. If set to do so then
     * this will destroy all knowledge of all identifiers. Will also
     * return the values resulting from the call.
     */
    protected TreeSet<Value> handleFunctionCall( String name )
    {
        if( destructiveCalls ){
            destroyInfo();
        }

        if( doTypeLookup ){
            //this needs to be implemented
            return newGeneralAssignedSet();
        }
        else
            return newGeneralAssignedSet();
    }
    /**
     * Deals with the affects of function calls when the function name
     * is unknown. Works the same as hadleFunctionCall but without the
     * possible lookup.
     */
    protected TreeSet<Value> handleUnknownFunctionCall()
    {
        if( destructiveCalls )
            destroyInfo();

        return newGeneralAssignedSet();
    }
    /**
     * Destroys the information in the current set due to a function
     * call. 
     */
    protected void destroyInfo()
    {
        for( TreeSet<Value> values : currentOutSet.values() ){
            change = true;
            values.clear();
            values.addAll( newGeneralSet() );
        }
    }
}