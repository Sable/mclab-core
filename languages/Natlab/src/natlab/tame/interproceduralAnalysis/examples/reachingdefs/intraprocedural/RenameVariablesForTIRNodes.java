package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.ArrayList;
import java.util.HashMap;

import natlab.tame.tir.TIRAbstractAssignFromVarStmt;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRAbstractAssignToVarStmt;
import natlab.tame.tir.TIRArrayGetStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCellArrayGetStmt;
import natlab.tame.tir.TIRCellArraySetStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRCopyStmt;
import natlab.tame.tir.TIRDotGetStmt;
import natlab.tame.tir.TIRDotSetStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRWhileStmt;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;
import ast.ASTNode;
import ast.Name;
import ast.NameExpr;

/**
 * Rename variables using the UDDU web
 * @author Amine Sahibi
 *
 */
@SuppressWarnings("rawtypes")
public class RenameVariablesForTIRNodes extends TIRAbstractNodeCaseHandler
{
    UDDUWeb fUDDUWeb;
    RenameVariablesForASTNodes fRenameVariablesForASTNodes;
    
    public RenameVariablesForTIRNodes(UDDUWeb udduWeb)
    {
        fUDDUWeb = udduWeb;
        fUDDUWeb.constructUDDUWeb();
        fRenameVariablesForASTNodes = new RenameVariablesForASTNodes(null, fUDDUWeb);
        fRenameVariablesForASTNodes.analyze();
    }
    
    /**
     * Analyze only the visited statements
     */
    public void analyze()
    {
        for (ASTNode node : fUDDUWeb.getVisitedStmtsLinkedList())
        {
            // TODO I should not have to make this call explicit!!!
            if (node instanceof TIRFunction)
            {
                caseTIRFunction((TIRFunction) node);
            }
            (node).analyze(this);  
        }
    }
    
    @Override
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
        java.util.List<Name> inputParams = new ArrayList<Name>();

        for (Name inputParam : node.getInputParamList())
        {
            inputParams.add(inputParam);
        }
        
        TIRCommaSeparatedList definedVariablesNames = TIRCommaSeparatedList.createFromNames(inputParams);
        renameDefinedVariablesForNode(definedVariablesNames, node);
        caseASTNode(node);
    }
    
    @Override
    public void caseTIRIfStmt(TIRIfStmt node)
    {
        Name conditionVariableName = node.getConditionVarName();
        if (conditionVariableName != null)
        {
            HashMap<ASTNode, Integer> nodeAndColor = fUDDUWeb.getNodeAndColorForUse(conditionVariableName.getNodeString());
            if (nodeAndColor != null &&  nodeAndColor.containsKey(node))
            {
                renameVariableWithColor(conditionVariableName, nodeAndColor.get(node));
            }
        }
        caseIfStmt(node);
    }
    
    @Override
    public void caseTIRWhileStmt(TIRWhileStmt node)
    {
        Name conditionVariableName = node.getCondition().getName();
        if (conditionVariableName != null)
        {
            HashMap<ASTNode, Integer> nodeAndColor = fUDDUWeb.getNodeAndColorForUse(conditionVariableName.getNodeString());
            if (nodeAndColor != null &&  nodeAndColor.containsKey(node))
            {
                renameVariableWithColor(conditionVariableName, nodeAndColor.get(node));
            }
        }
        caseWhileStmt(node);
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
    public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node)
    {
        TIRCommaSeparatedList definedVariablesNames = null;
        TIRCommaSeparatedList usedVariablesNames = null;
        
        if (node instanceof TIRAbstractAssignFromVarStmt)
        {
            Name target;
            Name targetField = null;
            if (node instanceof TIRArraySetStmt)
            {
                TIRArraySetStmt arraySetStmt = (TIRArraySetStmt) node;
                
                target = arraySetStmt.getArrayName();
                
                usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(arraySetStmt.getValueName()));
                usedVariablesNames.add(new NameExpr(arraySetStmt.getArrayName()));
                TIRCommaSeparatedList indices = arraySetStmt.getIndizes();
                addIndicesToUsedVariablesNames(indices, usedVariablesNames);
                
            }
            else if (node instanceof TIRCellArraySetStmt)
            {
                TIRCellArraySetStmt cellArraySetStmt = (TIRCellArraySetStmt) node;
                
                target = cellArraySetStmt.getCellArrayName();

                usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(cellArraySetStmt.getValueName()));
                usedVariablesNames.add(new NameExpr(cellArraySetStmt.getCellArrayName()));
                TIRCommaSeparatedList indices = cellArraySetStmt.getIndizes();
                addIndicesToUsedVariablesNames(indices, usedVariablesNames);
                
            }
            else if (node instanceof TIRDotSetStmt)
            {
                TIRDotSetStmt dotSetStmt = (TIRDotSetStmt) node;
                
                target = dotSetStmt.getDotName();
                targetField = dotSetStmt.getFieldName();
                
                usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(dotSetStmt.getValueName()));
                usedVariablesNames.add(new NameExpr(dotSetStmt.getDotName()));
            }
            else 
            { 
                throw new UnsupportedOperationException("unknown assign from var stmt " + node); 
            }
            
            definedVariablesNames = new TIRCommaSeparatedList(new NameExpr(target));
            if (targetField != null)
            {
                definedVariablesNames.add(new NameExpr(targetField));
            }
        }
        else if (node instanceof TIRAbstractAssignToVarStmt)
        {
            if (node instanceof TIRCopyStmt)
            {
                TIRCopyStmt copySmtmt = (TIRCopyStmt) node;
                Name usedVarName = copySmtmt.getSourceName();
                usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(usedVarName));
            }
            definedVariablesNames = new TIRCommaSeparatedList(new NameExpr(((TIRAbstractAssignToVarStmt)node).getTargetName()));
        }
        else 
        {
            throw new UnsupportedOperationException("unknown assignment statement " + node);
        }
        renameDefinedVariablesForNode(definedVariablesNames, node);
        renameUsedVariablesForNode(usedVariablesNames, node);
    }
    
    @Override
    public void caseTIRAbstractAssignToListStmt(TIRAbstractAssignToListStmt node)
    {
        TIRCommaSeparatedList definedVariablesNames = null;
        TIRCommaSeparatedList usedVariablesNames = null;
        
        definedVariablesNames = node.getTargets();
        
        if (node instanceof TIRArrayGetStmt)
        {
            TIRArrayGetStmt arrayGetStmt = (TIRArrayGetStmt) node;
            Name arrayName = arrayGetStmt.getArrayName();
            usedVariablesNames = new TIRCommaSeparatedList(new NameExpr((arrayName)));
            TIRCommaSeparatedList indices = arrayGetStmt.getIndizes();
            addIndicesToUsedVariablesNames(indices, usedVariablesNames);
        }
        else if (node instanceof TIRDotGetStmt)
        {
            TIRDotGetStmt dotGetStmt = (TIRDotGetStmt) node;
            Name dotName = dotGetStmt.getDotName();
            Name fieldName = dotGetStmt.getFieldName();
            usedVariablesNames = new TIRCommaSeparatedList(new NameExpr(dotName));
            usedVariablesNames.add(new NameExpr(fieldName));
        }
        else if (node instanceof TIRCellArrayGetStmt)
        {
            TIRCellArrayGetStmt cellArrayGetStmt = (TIRCellArrayGetStmt) node;
            Name cellArrayName = cellArrayGetStmt.getCellArrayName();
            usedVariablesNames = new TIRCommaSeparatedList(new NameExpr((cellArrayName)));
            TIRCommaSeparatedList indices = cellArrayGetStmt.getIndices();
            addIndicesToUsedVariablesNames(indices, usedVariablesNames);
        }
        
        renameDefinedVariablesForNode(definedVariablesNames, node);
        renameUsedVariablesForNode(usedVariablesNames, node);
    }
    
    public void addIndicesToUsedVariablesNames(TIRCommaSeparatedList indices, TIRCommaSeparatedList usedVariablesNames)
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
    
    public void renameUsedVariablesForNode(TIRCommaSeparatedList usedVariablesNames, ASTNode parentNode)
    {
        if (usedVariablesNames == null)
        {
            return;
        }
        for (int i = 0; i < usedVariablesNames.getNumChild(); i++)
        {
            Name usedVariableName = usedVariablesNames.getName(i);
            HashMap<ASTNode, Integer> nodeAndColor = fUDDUWeb.getNodeAndColorForUse(usedVariableName.getNodeString());
            if (nodeAndColor != null && nodeAndColor.containsKey(parentNode))
            {
                renameVariableWithColor(usedVariableName, nodeAndColor.get(parentNode));
            }
        }
    }
    
    public void renameDefinedVariablesForNode(TIRCommaSeparatedList definedVariablesNames, ASTNode parentNode)
    {
        if (definedVariablesNames == null)
        {
            return;
        }
        for (int i = 0; i < definedVariablesNames.getNumChild(); i++)
        {
            Name usedVariableName = definedVariablesNames.getName(i);
            HashMap<ASTNode, Integer> nodeAndColor = fUDDUWeb.getNodeAndColorForDefinition(usedVariableName.getNodeString());
            if (nodeAndColor != null && nodeAndColor.containsKey(parentNode))
            {
                renameVariableWithColor(usedVariableName, nodeAndColor.get(parentNode));
            }
        }
    }
    
    public void renameVariableWithColor(Name variableName, Integer color)
    {
        String updatedVariableName = variableName.getNodeString() + "#" + color;
        variableName.setID(updatedVariableName);
    }

}
