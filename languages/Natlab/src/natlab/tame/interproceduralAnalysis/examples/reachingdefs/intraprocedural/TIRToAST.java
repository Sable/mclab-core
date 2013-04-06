package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;

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

@SuppressWarnings("rawtypes")
public class TIRToAST extends AbstractTIRLocalRewrite
{

    public static boolean DEBUG = false;
    private HashMap<TIRNode, ASTNode> fTIRToRawASTTable;
    private DefinedVariablesNameCollector fDefinedVariablesNameCollector;
    
    public TIRToAST(ASTNode tree, HashMap<TIRNode, ASTNode> TIRToRawASTTable, DefinedVariablesNameCollector definedVariablesNameCollector)
    {
        super(tree);
        fTIRToRawASTTable = TIRToRawASTTable;
        fDefinedVariablesNameCollector = definedVariablesNameCollector;
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
    
    public boolean nodeDefinesTmpVariables(TIRNode node)
    {
        Set<String> definedVariablesNames = fDefinedVariablesNameCollector.getDefinedVariablesNamesForNode(node);
        return isAnyVariableTemporary(definedVariablesNames);
    }
    
    public boolean isAnyVariableTemporary(Set<String> definedVariablesNames)
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
    
    public boolean isTemporaryVariable(String variableName)
    {
        return variableName.startsWith(TempFactory.getPrefix());
    }
    

}
