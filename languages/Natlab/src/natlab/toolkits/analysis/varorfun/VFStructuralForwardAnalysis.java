package natlab.toolkits.analysis.varorfun;

import ast.*;
import natlab.toolkits.analysis.*;

//note, must be applied on a tree containing a function or script node 
public class VFStructuralForwardAnalysis extends AbstractSimpleStructuralForwardAnalysis< VFFlowset<String, VFDatum> >
{
    //public static boolean DEBUG = true;
    protected boolean isScript = true;
    protected VFPreorderAnalysis functionAnalysis = null;

    protected VFFlowset<String, ? extends VFDatum> initialFlow = null;

    public VFFlowset<String, VFDatum> newInitialFlow()
    {
        return (VFFlowset<String, VFDatum>)initialFlow.clone();
    }

    //Helper methods to create new datums
    public VFDatum newVariableDatum()
    {
        if( isScript ){
            ScriptVFDatum d = new ScriptVFDatum();
            d.makeVariable();
            return d;
        }
        else{
            FunctionVFDatum d = new FunctionVFDatum();
            d.makeVariable();
            return d;
        }
    }
    public VFDatum newAssignedVariableDatum()
    {
        if( isScript ){
            ScriptVFDatum d = new ScriptVFDatum();
            d.makeVariable();
            return d;
        }
        else{
            FunctionVFDatum d = new FunctionVFDatum();
            d.makeAssignedVariable();
            return d;
        }
    }
    public VFDatum newFunctionDatum()
    {
        if( isScript ){
            ScriptVFDatum d = new ScriptVFDatum();
            d.makeVariable();
            return d;
        }
        else{
            FunctionVFDatum d = new FunctionVFDatum();
            d.makeFunction();
            return d;
        }
    }

    //constructor
    public VFStructuralForwardAnalysis( ASTNode tree ){
        super( tree );
        initialFlow = new VFFlowset<String, VFDatum>();
        currentOutSet = newInitialFlow();
    }

    //Begin cases
    public void caseScript( Script node )
    {
        isScript = true;
        initialFlow = new VFFlowset();
        currentOutSet = newInitialFlow();
    }

    public void caseFunction( Function node )
    {
        isScript = false;
        if( functionAnalysis == null || functionAnalysis.getFlowSets().get(node) == null){
            functionAnalysis = new VFPreorderAnalysis( node );
            functionAnalysis.analyze();
        }
        initialFlow = functionAnalysis.getFlowSets().get(node);
        currentInSet = newInitialFlow();
        currentOutSet = newInitialFlow();
        caseASTNode( node );
    }



    public void caseCondition( Expr node ){
        inFlowSets.put(node, currentInSet.clone() );
        currentOutSet = newInitialFlow();
        copy( currentInSet, currentOutSet );
        outFlowSets.put(node, currentOutSet.clone() );
        caseExpr( node );
    }

    public void caseLoopVarAsCondition( AssignStmt node )
    {
        caseAssignStmt( node );
    }
    public void caseLoopVarAsInit( AssignStmt node )
    {
        caseAssignStmt( node );
    }
    public void caseLoopVarAsUpdate( AssignStmt node )
    {
        caseAssignStmt( node );
    }

    public void copy( VFFlowset<String, VFDatum> source, VFFlowset<String,  VFDatum> dest)
    {
        source.copy(dest);
    }

    public void merge( VFFlowset<String, VFDatum> in1, VFFlowset<String,  VFDatum> in2, VFFlowset<String,  VFDatum> out )
    {
        in1.union( in2, out );
        if(DEBUG)
            System.out.println("done merging " + out);
    }
    
    public void caseAssignStmt(AssignStmt node){
        if(DEBUG)
            System.err.println("hey in assign stmt");
        inFlowSets.put(node, currentInSet.clone() );
        copy(currentInSet, currentOutSet);
        for(String s : node.getLValues()){
            if(DEBUG)
                System.err.println("makeing " + s + " an AVAR if we can");
            VFDatum d = currentOutSet.contains( s );
            if( d == null )
                currentOutSet.add(new ValueDatumPair(s, newAssignedVariableDatum()) );
            else
                d.makeAssignedVariable();
        }
        outFlowSets.put(node, currentOutSet.clone() );
    }
    public void caseStmt( Stmt node )
    {
        inFlowSets.put(node, currentInSet.clone() );
        currentOutSet = newInitialFlow();
        copy( currentInSet, currentOutSet );
        outFlowSets.put(node, currentOutSet.clone() );
    }
    public void caseEmptyStmt( EmptyStmt node )
    {
        //currentOutSet = newInitialFlow();
        //copy( currentInSet, currentOutSet );
        return;
    }
}
        