package natlab.tame.interproceduralAnalysis.examples.reachingdefs.interprocedural;

import java.util.HashSet;

import natlab.tame.callgraph.StaticFunction;
import natlab.tame.interproceduralAnalysis.FunctionAnalysis;
import natlab.tame.interproceduralAnalysis.InterproceduralAnalysisNode;
import natlab.tame.tir.TIRAbstractAssignFromVarStmt;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRAbstractAssignToVarStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCellArraySetStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRDotSetStmt;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;
import natlab.toolkits.analysis.HashSetFlowSet;
import ast.ASTNode;
import ast.Function;
import ast.Name;
import ast.NameExpr;

/**
 * 
 * @author Amine Sahibi
 *
 */
public class IntraproceduralVarNamesAnalysis
    extends TIRAbstractSimpleStructuralForwardAnalysis<HashSetFlowSet<VarNamesValue>>
    implements FunctionAnalysis<VarNamesInput, HashSetFlowSet<VarNamesValue>>
{
    private VarNamesInput fInput;
    private HashSet<VarNamesValue> fResult;
    private HashSetFlowSet<VarNamesValue> fInputSet;
    private StaticFunction fFunction;
    protected IntraproceduralVarNamesAnalysis
    (
        InterproceduralAnalysisNode<IntraproceduralVarNamesAnalysis, VarNamesInput, HashSetFlowSet<VarNamesValue>> node,
        VarNamesInput input
    )
    {
        super(node.getFunction().getAst());
        fInput = input;
        fFunction = node.getFunction();
        fInputSet = new HashSetFlowSet<VarNamesValue>();
    }
    
    @Override 
    public HashSetFlowSet<VarNamesValue> getResult() 
    {
        if (!isAnalyzed()) analyze();
        if (fResult == null)
        {
            fResult = (HashSet<VarNamesValue>) getOutFlowSets().get(fFunction.getAst()).getSet();
        }
        return new HashSetFlowSet<VarNamesValue>(fResult);
    }
    
    @Override
    public HashSetFlowSet<VarNamesValue> getDefaultResult()
    {
        throw new UnsupportedOperationException("Variable names analysis doesn't support recursive programs");
    }
    
    @Override
    public Function getTree()
    {
        return (Function) super.getTree();
    }
    
    @Override
    public void copy(HashSetFlowSet<VarNamesValue> source, HashSetFlowSet<VarNamesValue> dest)
    {
        dest.clear();
        for (VarNamesValue varNamesValue : source.getSet())
        {
            dest.add(varNamesValue);
        }
    }
    
    @Override
    public  HashSetFlowSet<VarNamesValue> newInitialFlow()
    {
        return new HashSetFlowSet<VarNamesValue>();
    }
    
    @Override
    public void merge(HashSetFlowSet<VarNamesValue> in1, HashSetFlowSet<VarNamesValue> in2,
            HashSetFlowSet<VarNamesValue> out)
    {
        for (VarNamesValue varNamesValue : in1.getSet())
        {
            out.add(varNamesValue);
        }
        for (VarNamesValue varNamesValue : in2.getSet())
        {
            out.add(varNamesValue);
        }
    }
    
    @Override
    public void caseFunction(Function node)
    {
        setCurrentOutSet(fInputSet);
        caseASTNode(node);
        setInOutSet(node);
    }
    
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
       HashSetFlowSet<VarNamesValue> currentSet = copyOutSet();
       IRCommaSeparatedListToVaribaleNamesSet(((TIRAbstractAssignToListStmt)node).getTargets(), currentSet);
       setCurrentOutSet(currentSet);
       setInOutSet(node);
    }    
    
    @Override
    public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node) {
        HashSetFlowSet<VarNamesValue> currentSet = copyOutSet();
        TIRCommaSeparatedList declaredVariablesList = null;
        if (node instanceof TIRAbstractAssignFromVarStmt)
        {
            Name target;
            if (node instanceof TIRArraySetStmt)
            {
                target = ((TIRArraySetStmt)node).getArrayName();
            }
            else if (node instanceof TIRCellArraySetStmt)
            {
                target = ((TIRCellArraySetStmt)node).getCellArrayName();
            }
            else if (node instanceof TIRDotSetStmt)
            {
                target = ((TIRDotSetStmt)node).getDotName();
            }
            else 
            { 
                throw new UnsupportedOperationException("unknown assign from var stmt " + node); 
            }
            declaredVariablesList = new TIRCommaSeparatedList(new NameExpr(target));
        }
        else if (node instanceof TIRAbstractAssignToListStmt)
        {
            declaredVariablesList = ((TIRAbstractAssignToListStmt)node).getTargets();
        } 
        else if (node instanceof TIRAbstractAssignToVarStmt)
        {
            declaredVariablesList = new TIRCommaSeparatedList(new NameExpr(((TIRAbstractAssignToVarStmt)node).getTargetName()));
        }
        else 
        {
            throw new UnsupportedOperationException("unknown assignment statement " + node);
        }
        IRCommaSeparatedListToVaribaleNamesSet(declaredVariablesList, currentSet);
        setCurrentOutSet(currentSet);
        setInOutSet(node);
    }
    

    private void IRCommaSeparatedListToVaribaleNamesSet(TIRCommaSeparatedList csl, HashSetFlowSet<VarNamesValue> variableNames)
    {
        for(Name name : csl.asNameList())
        {
            variableNames.add(new VarNamesValue(name.getID()));
        }
    }
    
    private HashSetFlowSet<VarNamesValue> copyOutSet()
    {
        return new HashSetFlowSet<VarNamesValue>((HashSet<VarNamesValue>) getCurrentOutSet().getSet());
    }
    
    private void setInOutSet(ASTNode<?> node)
    {
        associateInSet(node, getCurrentInSet());
        associateOutSet(node, getCurrentOutSet());
    }
    
    public InterproceduralAnalysisNode<?, ?, ?> getPreviousAnalysisNode()
    {
        return fInput.getNode();
    }
}
