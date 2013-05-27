package natlab.tame.tamerplus.transformation;

import java.util.NoSuchElementException;
import java.util.Set;

import natlab.tame.tamerplus.analysis.AnalysisEngine;
import natlab.tame.tamerplus.analysis.DefinedVariablesNameCollector;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCommentStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRWhileStmt;
import natlab.toolkits.rewrite.TempFactory;
import natlab.toolkits.rewrite.TransformedNode;
import ast.ASTNode;
import ast.AssignStmt;
import ast.ForStmt;
import ast.Function;
import ast.IfStmt;
import ast.WhileStmt;

import com.google.common.collect.HashBiMap;

@SuppressWarnings("rawtypes")
public class TIRToMcSAFIRWithoutTemp extends AbstractTIRLocalRewrite implements TamerPlusTransformation
{
    private HashBiMap<TIRNode, ASTNode> fTIRToRawASTTable;
    private DefinedVariablesNameCollector fDefinedVariablesNameCollector;
    private ASTNode<?> fTransformedTree;
    
    public TIRToMcSAFIRWithoutTemp(ASTNode tree)
    {
        super(tree);
        fTransformedTree = null;
    }
    
    @Override
    public void transform(TransformationEngine engine)
    {
        AnalysisEngine analysisEngine = engine.getAnalysisEngine();
        fTIRToRawASTTable = analysisEngine.getTemporaryVariablesRemovalAnalysis().getTIRToMcSAFIRTable();
        fDefinedVariablesNameCollector = analysisEngine.getDefinedVariablesAnalysis();
        fTransformedTree = super.transform();
    }
    
    @Override
    public void caseTIRFunction(TIRFunction node)
    {
        rewriteChildren(node);
        Function outputFunction = (Function) getReplacementNode(node);
        fNewNode = new TransformedNode(outputFunction);
    }
    
    @Override
    public void caseTIRIfStmt(TIRIfStmt node)
    {
        rewriteChildren(node);
        IfStmt outputIfStmt = (IfStmt) getReplacementNode(node);
        fNewNode = new TransformedNode(outputIfStmt);
    }
    
    @Override
    public void caseTIRForStmt(TIRForStmt node)
    {
        rewrite(node.getStmtList());
        ForStmt outputForStmt = (ForStmt) getReplacementNode(node);
        fNewNode = new TransformedNode(outputForStmt);
    }
    
    @Override
    public void caseTIRWhileStmt(TIRWhileStmt node)
    {
        rewriteChildren(node);
        WhileStmt outputWhileStmt = (WhileStmt) getReplacementNode(node);
        fNewNode = new TransformedNode(outputWhileStmt);
    }
    
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
        rewriteChildren(node);
        if (!nodeDefinesTmpVariables(node))
        {
            AssignStmt outputCallStmt = (AssignStmt) getReplacementNode(node);
            fNewNode = new TransformedNode(outputCallStmt);
        }
        else
        {
            fNewNode = new TransformedNode(new TIRCommentStmt());
        }
    }
    
    @Override
    public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node)
    {
        if (!nodeDefinesTmpVariables(node))
        {
            AssignStmt outputAssignStmt = (AssignStmt) getReplacementNode(node);
            fNewNode = new TransformedNode(outputAssignStmt);
        }
        else
        {
            fNewNode = new TransformedNode(new TIRCommentStmt());
        }
    }
    
    public ASTNode getReplacementNode(TIRNode node)
    {
        if (fTIRToRawASTTable.containsKey(node))
        {
            ASTNode replacementNode = fTIRToRawASTTable.get(node);
            if (replacementNode != null)
            {
                return replacementNode;
            }
            else
            {
                throw new NoSuchElementException("Entry for TIRNode " + node + " is null in TIR to AST table");
            }
        }
        else
        {
            throw new NoSuchElementException("Key TIRNode " + node + " is not present in TIR to AST table");
        }
    }
    
    private boolean nodeDefinesTmpVariables(TIRNode node)
    {
        Set<String> definedVariablesNames = fDefinedVariablesNameCollector.getDefinedVariablesForNode(node);
        return isAnyVariableTemporary(definedVariablesNames);
    }
    
    private boolean isAnyVariableTemporary(Set<String> definedVariablesNames)
    {
        for (String variableName : definedVariablesNames)
        {
            if (isTemporaryVariable(variableName))
            {
                return true;
            }
        }
        return false;
    }
    
    private boolean isTemporaryVariable(String variableName)
    {
        return variableName.startsWith(TempFactory.getPrefix());
    }
    
    public ASTNode<?> getTransformedTree()
    {
        return fTransformedTree;
    }
}
