package natlab.tame.tamerplus.transformation;

import java.util.ArrayList;
import java.util.Map;

import natlab.tame.tamerplus.analysis.AnalysisEngine;
import natlab.tame.tamerplus.analysis.UDDUWeb;
import natlab.tame.tir.TIRAbstractAssignToVarStmt;
import natlab.tame.tir.TIRArrayGetStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRAssignLiteralStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCellArrayGetStmt;
import natlab.tame.tir.TIRCellArraySetStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRCopyStmt;
import natlab.tame.tir.TIRDotGetStmt;
import natlab.tame.tir.TIRDotSetStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRWhileStmt;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;
import ast.ASTNode;
import ast.Name;
import ast.NameExpr;

public class RenameVariablesForTIRNodes extends TIRAbstractNodeCaseHandler implements TamerPlusTransformation
{
    private UDDUWeb fUDDUWeb;
    public static final String COLOR_PREFIX = "#";
    private ASTNode<?> fTransformedTree;
    
    public RenameVariablesForTIRNodes(ASTNode<?> tree)
    {
        fTransformedTree = tree;
    }
    
    @Override
    public void transform(TransformationEngine transformationEngine)
    {
        AnalysisEngine analysisEngine = transformationEngine.getAnalysisEngine();
        fUDDUWeb = analysisEngine.getUDDUWebAnalysis();
        getFunctionNode().tirAnalyze(this);
    }
    
    @Override
    @SuppressWarnings("rawtypes")
    public void caseASTNode(ASTNode node) 
    {
        int nodeChildrenCount = node.getNumChild();
        for (int i = 0; i < nodeChildrenCount; i++)
        {
            if (node.getChild(i) instanceof TIRNode)
            {
                ((TIRNode) node.getChild(i)).tirAnalyze(this);
            }
            else 
            {
                node.getChild(i).analyze(this);
            }
        }
    }

    @Override
    public void caseTIRFunction(TIRFunction node)
    {
        renameDefinedVariablesForNode(getInputParamsForFunction(node), node);
        caseASTNode(node);
    }
    
    private TIRCommaSeparatedList getInputParamsForFunction(TIRFunction node)
    {
        java.util.List<Name> inputParams = new ArrayList<Name>();

        for (Name inputParam : node.getInputParamList())
        {
            inputParams.add(inputParam);
        }
        
        return TIRCommaSeparatedList.createFromNames(inputParams);
    }
    
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
       TIRCommaSeparatedList definedVariablesNames = node.getTargets();
       
       TIRCommaSeparatedList usedVariablesNames = node.getArguments();
       
       renameDefinedVariablesForNode(definedVariablesNames, node);
       renameUsedVariablesForNode(usedVariablesNames, node);
    }
    
    @Override
    public void caseTIRIfStmt(TIRIfStmt node)
    {
        Name conditionVariableName = node.getConditionVarName();
        renameConditionVariableForNode(conditionVariableName, node);
        caseIfStmt(node);
    }
    
    @Override
    public void caseTIRWhileStmt(TIRWhileStmt node)
    {
        Name conditionVariableName = node.getCondition().getName();
        renameConditionVariableForNode(conditionVariableName, node);
        caseWhileStmt(node);
    }
    
    private void renameConditionVariableForNode(Name conditionVariableName, TIRNode node)
    {
        Map<TIRNode, Integer> nodeToColorMap = fUDDUWeb.getNodeAndColorForUse(conditionVariableName.getID());
        if (nodeToColorMap != null &&  nodeToColorMap.containsKey(node))
        {
            renameVariableWithColor(conditionVariableName, nodeToColorMap.get(node));
        }
    }
    
    @Override
    public void caseTIRForStmt(TIRForStmt node)
    {
        TIRCommaSeparatedList definedVariablesNames = null;
        TIRCommaSeparatedList usedVariablesNames = null;
        
        definedVariablesNames = new TIRCommaSeparatedList(new NameExpr(node.getLoopVarName()));

        usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(node.getLowerName()));
        if (node.hasIncr())
        {
            usedVariablesNames.add(new NameExpr(node.getIncName()));
        }
        usedVariablesNames.add(new NameExpr(node.getUpperName()));

        renameDefinedVariablesForNode(definedVariablesNames, node);
        renameUsedVariablesForNode(usedVariablesNames, node);
        caseForStmt(node);
    }
    
    @Override
    public void caseTIRArraySetStmt(TIRArraySetStmt node)
    {
        TIRCommaSeparatedList definedVariablesNames = new TIRCommaSeparatedList(new NameExpr(node.getArrayName()));
        
        TIRCommaSeparatedList usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(node.getValueName()));
        usedVariablesNames.add(new NameExpr(node.getArrayName()));
        TIRCommaSeparatedList indices = node.getIndizes();
        addIndicesToUsedVariablesNames(indices, usedVariablesNames);
        
        renameDefinedVariablesForNode(definedVariablesNames, node);
        renameUsedVariablesForNode(usedVariablesNames, node);
    }
    
    @Override
    public void caseTIRCellArraySetStmt(TIRCellArraySetStmt node)
    {
        TIRCommaSeparatedList definedVariablesNames = new TIRCommaSeparatedList(new NameExpr(node.getCellArrayName()));
        
        TIRCommaSeparatedList usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(node.getValueName()));
        usedVariablesNames.add(new NameExpr(node.getCellArrayName()));
        TIRCommaSeparatedList indices = node.getIndizes();
        addIndicesToUsedVariablesNames(indices, usedVariablesNames);
        
        renameDefinedVariablesForNode(definedVariablesNames, node);
        renameUsedVariablesForNode(usedVariablesNames, node);
    }
    
    @Override
    public void caseTIRDotSetStmt(TIRDotSetStmt node)
    {
        TIRCommaSeparatedList definedVariablesNames = new TIRCommaSeparatedList(new NameExpr(node.getDotName()));
        definedVariablesNames.add(new NameExpr(node.getFieldName()));
        
        TIRCommaSeparatedList usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(node.getValueName()));
        usedVariablesNames.add(new NameExpr(node.getDotName()));
        
        renameDefinedVariablesForNode(definedVariablesNames, node);
        renameUsedVariablesForNode(usedVariablesNames, node);
    }
    
    @Override
    public void caseTIRCopyStmt(TIRCopyStmt node)
    {
        TIRCommaSeparatedList definedVariablesNames = new TIRCommaSeparatedList(new NameExpr(((TIRAbstractAssignToVarStmt)node).getTargetName()));
        
        Name usedVarName = node.getSourceName();
        TIRCommaSeparatedList usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(usedVarName));
        
        renameDefinedVariablesForNode(definedVariablesNames, node);
        renameUsedVariablesForNode(usedVariablesNames, node);
    }
    
    @Override
    public void caseTIRCellArrayGetStmt(TIRCellArrayGetStmt node)
    {
        TIRCommaSeparatedList definedVariablesNames = node.getTargets();
        TIRCommaSeparatedList usedVariablesNames = null;
        
        TIRCellArrayGetStmt cellArrayGetStmt = (TIRCellArrayGetStmt) node;
        Name cellArrayName = cellArrayGetStmt.getCellArrayName();
        usedVariablesNames = new TIRCommaSeparatedList(new NameExpr((cellArrayName)));
        TIRCommaSeparatedList indices = cellArrayGetStmt.getIndices();
        addIndicesToUsedVariablesNames(indices, usedVariablesNames);
        
        renameDefinedVariablesForNode(definedVariablesNames, node);
        renameUsedVariablesForNode(usedVariablesNames, node);
    }
    
    @Override
    public void caseTIRDotGetStmt(TIRDotGetStmt node)
    {
        TIRCommaSeparatedList definedVariablesNames = node.getTargets();
        TIRCommaSeparatedList usedVariablesNames = null;
        
        TIRDotGetStmt dotGetStmt = (TIRDotGetStmt) node;
        Name dotName = dotGetStmt.getDotName();
        Name fieldName = dotGetStmt.getFieldName();
        usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(dotName));
        usedVariablesNames.add(new NameExpr(fieldName));
        
        renameDefinedVariablesForNode(definedVariablesNames, node);
        renameUsedVariablesForNode(usedVariablesNames, node);
    }
    
    @Override
    public void caseTIRArrayGetStmt(TIRArrayGetStmt node)
    {
        TIRCommaSeparatedList definedVariablesNames = node.getTargets();
        TIRCommaSeparatedList usedVariablesNames = null;
        
        TIRArrayGetStmt arrayGetStmt = (TIRArrayGetStmt) node;
        Name arrayName = arrayGetStmt.getArrayName();
        usedVariablesNames = new TIRCommaSeparatedList(new NameExpr((arrayName)));
        TIRCommaSeparatedList indices = arrayGetStmt.getIndizes();
        addIndicesToUsedVariablesNames(indices, usedVariablesNames);
        
        renameDefinedVariablesForNode(definedVariablesNames, node);
        renameUsedVariablesForNode(usedVariablesNames, node);
    }
    
    @Override
    public void caseTIRAssignLiteralStmt(TIRAssignLiteralStmt node)
    {
        TIRCommaSeparatedList definedVariablesNames = new TIRCommaSeparatedList(new NameExpr(node.getTargetName()));
        renameDefinedVariablesForNode(definedVariablesNames, node);
    }
    
    private void addIndicesToUsedVariablesNames(TIRCommaSeparatedList indices, TIRCommaSeparatedList usedVariablesNames)
    {
        for (int i = 0; i < indices.size(); i++)
        {
            NameExpr indexNameExpr = indices.getNameExpresion(i);
            if (indexNameExpr != null)
            {
                usedVariablesNames.add(indexNameExpr);
            }
        }
    }
    
    private void renameUsedVariablesForNode(TIRCommaSeparatedList usedVariablesNames, TIRNode parentNode)
    {
        if (usedVariablesNames == null)
        {
            return;
        }
        for (int i = 0; i < usedVariablesNames.getNumChild(); i++)
        {
            Name usedVariableName = usedVariablesNames.getName(i);
            Map<TIRNode, Integer> nodeToColorMap = fUDDUWeb.getNodeAndColorForUse(usedVariableName.getID());
            if (nodeToColorMap != null && nodeToColorMap.containsKey(parentNode))
            {
                renameVariableWithColor(usedVariableName, nodeToColorMap.get(parentNode));
            }
        }
    }
    
    private void renameDefinedVariablesForNode(TIRCommaSeparatedList definedVariablesNames, TIRNode parentNode)
    {
        if (definedVariablesNames == null)
        {
            return;
        }
        for (int i = 0; i < definedVariablesNames.getNumChild(); i++)
        {
            Name definedVariableName = definedVariablesNames.getName(i);
            Map<TIRNode, Integer> nodeToColorMap = fUDDUWeb.getNodeAndColorForDefinition(definedVariableName.getID());
            if (nodeToColorMap != null && nodeToColorMap.containsKey(parentNode))
            {
                renameVariableWithColor(definedVariableName, nodeToColorMap.get(parentNode));
            }
        }
    }
    
    private void renameVariableWithColor(Name variableName, Integer color)
    {
        variableName.setID(String.format("%s%s%s", variableName.getNodeString(), COLOR_PREFIX, color));
    }
    
    private TIRNode getFunctionNode()
    {
        return fUDDUWeb.getVisitedStmtsLinkedList().get(0);
    }
    
    /**
     * Returns the new tree after UDDU web based variable coloring is applied
     * @return transformed tree 
     */
    public ASTNode<?> getTransformedTree()
    {
        return fTransformedTree;
    }
}
